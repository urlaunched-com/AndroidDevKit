package com.urlaunched.android.design.ui.exposedDropdown

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
fun BaseExposedDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset = DpOffset(Dimens.zeroDp, Dimens.zeroDp),
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable () -> Unit
) {
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = expanded

    if (expandedState.currentState || expandedState.targetState) {
        val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
        val density = LocalDensity.current
        val popupPositionProvider = remember(offset, density) {
            DropdownMenuPositionProvider(
                offset,
                density
            ) { parentBounds, menuBounds ->
                transformOriginState.value =
                    calculateTransformOrigin(parentBounds, menuBounds)
            }
        }

        val expandedStates = remember { MutableTransitionState(false) }
        expandedStates.targetState = expanded

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = properties
        ) {
            BaseDropdownMenuContent(
                modifier = modifier,
                expandedStates = expandedStates,
                content = content
            )
        }
    }
}