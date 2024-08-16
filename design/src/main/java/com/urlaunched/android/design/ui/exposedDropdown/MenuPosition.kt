package com.urlaunched.android.design.ui.exposedDropdown

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

/**
 * Interfaces for positioning a menu within a window. This is the same purpose as the interface
 * [PopupPositionProvider], except [Vertical] and [Horizontal] separate out the positioning logic
 * for each direction individually.
 */
@Stable
internal object MenuPosition {
    /**
     * An interface to calculate the vertical position of a menu with respect to its anchor and
     * window. The returned y-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Vertical {
        fun position(anchorBounds: IntRect, windowSize: IntSize, menuHeight: Int): Int
    }

    /**
     * An interface to calculate the horizontal position of a menu with respect to its anchor,
     * window, and layout direction. The returned x-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Horizontal {
        fun position(anchorBounds: IntRect, windowSize: IntSize, menuWidth: Int, layoutDirection: LayoutDirection): Int
    }

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the start of the menu to the start of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun startToAnchorStart(offset: Int = 0): Horizontal = AnchorAlignmentOffsetPosition.Horizontal(
        menuAlignment = Alignment.Start,
        anchorAlignment = Alignment.Start,
        offset = offset
    )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the end of the menu to the end of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun endToAnchorEnd(offset: Int = 0): Horizontal = AnchorAlignmentOffsetPosition.Horizontal(
        menuAlignment = Alignment.End,
        anchorAlignment = Alignment.End,
        offset = offset
    )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the left of the menu to the left of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun leftToWindowLeft(margin: Int = 0): Horizontal = WindowAlignmentMarginPosition.Horizontal(
        alignment = AbsoluteAlignment.Left,
        margin = margin
    )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the right of the menu to the right of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun rightToWindowRight(margin: Int = 0): Horizontal = WindowAlignmentMarginPosition.Horizontal(
        alignment = AbsoluteAlignment.Right,
        margin = margin
    )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the bottom of the
     * anchor.
     */
    fun topToAnchorBottom(offset: Int = 0): Vertical = AnchorAlignmentOffsetPosition.Vertical(
        menuAlignment = Alignment.Top,
        anchorAlignment = Alignment.Bottom,
        offset = offset
    )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the top of the
     * anchor.
     */
    fun bottomToAnchorTop(offset: Int = 0): Vertical = AnchorAlignmentOffsetPosition.Vertical(
        menuAlignment = Alignment.Bottom,
        anchorAlignment = Alignment.Top,
        offset = offset
    )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the center of the menu to the top of the
     * anchor.
     */
    fun centerToAnchorTop(offset: Int = 0): Vertical = AnchorAlignmentOffsetPosition.Vertical(
        menuAlignment = Alignment.CenterVertically,
        anchorAlignment = Alignment.Top,
        offset = offset
    )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the top of the
     * window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun topToWindowTop(margin: Int = 0): Vertical = WindowAlignmentMarginPosition.Vertical(
        alignment = Alignment.Top,
        margin = margin
    )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the bottom of the
     * window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun bottomToWindowBottom(margin: Int = 0): Vertical = WindowAlignmentMarginPosition.Vertical(
        alignment = Alignment.Bottom,
        margin = margin
    )
}

@Immutable
internal object AnchorAlignmentOffsetPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the given [menuAlignment] with the
     * given [anchorAlignment].
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    @Immutable
    data class Horizontal(
        private val menuAlignment: Alignment.Horizontal,
        private val anchorAlignment: Alignment.Horizontal,
        private val offset: Int
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection
        ): Int {
            val anchorAlignmentOffset = anchorAlignment.align(
                size = 0,
                space = anchorBounds.width,
                layoutDirection = layoutDirection
            )
            val menuAlignmentOffset = -menuAlignment.align(
                size = 0,
                space = menuWidth,
                layoutDirection
            )
            val resolvedOffset = if (layoutDirection == LayoutDirection.Ltr) offset else -offset
            return anchorBounds.left + anchorAlignmentOffset + menuAlignmentOffset + resolvedOffset
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the given [menuAlignment] with the given
     * [anchorAlignment].
     */
    @Immutable
    data class Vertical(
        private val menuAlignment: Alignment.Vertical,
        private val anchorAlignment: Alignment.Vertical,
        private val offset: Int
    ) : MenuPosition.Vertical {
        override fun position(anchorBounds: IntRect, windowSize: IntSize, menuHeight: Int): Int {
            val anchorAlignmentOffset = anchorAlignment.align(
                size = 0,
                space = anchorBounds.height
            )
            val menuAlignmentOffset = -menuAlignment.align(
                size = 0,
                space = menuHeight
            )
            return anchorBounds.top + anchorAlignmentOffset + menuAlignmentOffset + offset
        }
    }
}

@Immutable
internal object WindowAlignmentMarginPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the menu within the window according
     * to the given [alignment].
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window. If this is not possible, i.e.,
     * the menu is too wide, then it is centered horizontally instead.
     */
    @Immutable
    data class Horizontal(
        private val alignment: Alignment.Horizontal,
        private val margin: Int
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection
        ): Int {
            if (menuWidth >= windowSize.width - 2 * margin) {
                return Alignment.CenterHorizontally.align(
                    size = menuWidth,
                    space = windowSize.width,
                    layoutDirection = layoutDirection
                )
            }
            val x = alignment.align(
                size = menuWidth,
                space = windowSize.width,
                layoutDirection = layoutDirection
            )
            return x.coerceIn(margin, windowSize.width - margin - menuWidth)
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the menu within the window according to
     * the given [alignment].
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window. If this is not possible, i.e.,
     * the menu is too tall, then it is centered vertically instead.
     */
    @Immutable
    data class Vertical(
        private val alignment: Alignment.Vertical,
        private val margin: Int
    ) : MenuPosition.Vertical {
        override fun position(anchorBounds: IntRect, windowSize: IntSize, menuHeight: Int): Int {
            if (menuHeight >= windowSize.height - 2 * margin) {
                return Alignment.CenterVertically.align(
                    size = menuHeight,
                    space = windowSize.height
                )
            }
            val y = alignment.align(
                size = menuHeight,
                space = windowSize.height
            )
            return y.coerceIn(margin, windowSize.height - margin - menuHeight)
        }
    }
}