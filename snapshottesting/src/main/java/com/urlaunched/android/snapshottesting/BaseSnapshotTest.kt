package com.urlaunched.android.snapshottesting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.LayoutDirection
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.urlaunched.android.design.ui.paging.LocalPagingMode
import com.urlaunched.android.design.ui.paging.LocalPagingModeEnum
import org.junit.Rule
import java.util.Locale

abstract class BaseSnapshotTest(
    private val deviceConfig: DeviceConfig = DeviceConfig.PIXEL_5,
    renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL,
    private val supportsDarkMode: Boolean = false,
    private val supportsLandscape: Boolean = false,
    private val supportsRtl: Boolean = false
) {
    init {
        Locale.setDefault(Locale.ENGLISH)
    }

    @get:Rule
    open val paparazzi =
        Paparazzi(
            deviceConfig = deviceConfig,
            renderingMode = renderingMode,
            showSystemUi = false,
            maxPercentDifference = 0.1
        )

    fun snapshot(pagingMode: LocalPagingModeEnum? = null, composable: @Composable () -> Unit) {
        when {
            supportsDarkMode && supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", pagingMode, composable)

                lightPortraitRtlConfig()
                paparazziSnapshot("light_rtl", pagingMode, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", pagingMode, composable)

                darkLandscapeConfig()
                paparazziSnapshot("dark_landscape", pagingMode, composable)

                darkPortraitRtlConfig()
                paparazziSnapshot("dark_rtl", pagingMode, composable)
            }

            supportsDarkMode && supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", pagingMode, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", pagingMode, composable)

                darkLandscapeConfig()
                paparazziSnapshot("dark_landscape", pagingMode, composable)
            }

            supportsDarkMode && !supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                lightPortraitRtlConfig()
                paparazziSnapshot("light_rtl", pagingMode, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", pagingMode, composable)

                darkPortraitRtlConfig()
                paparazziSnapshot("dark_rtl", pagingMode, composable)
            }

            supportsDarkMode && !supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                darkPortraitConfig()
                paparazziSnapshot("dark", pagingMode, composable)
            }

            !supportsDarkMode && supportsLandscape && !supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                lightLandscapeConfig()
                paparazziSnapshot("light_landscape", pagingMode, composable)
            }

            !supportsDarkMode && !supportsLandscape && supportsRtl -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)

                lightPortraitRtlConfig()
                paparazziSnapshot("light_rtl", pagingMode, composable)
            }

            else -> {
                lightPortraitConfig()
                paparazziSnapshot("light", pagingMode, composable)
            }
        }
    }

    private fun paparazziSnapshot(
        name: String,
        pagingMode: LocalPagingModeEnum? = null,
        composable: @Composable () -> Unit
    ) {
        paparazzi.snapshot(name) {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalPagingMode provides pagingMode
            ) {
                composable()
            }
        }
    }

    private val lightPortraitConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                nightMode = NightMode.NOTNIGHT
            )
        )
    }

    private val lightLandscapeConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                orientation = ScreenOrientation.LANDSCAPE,
                screenHeight = deviceConfig.screenWidth,
                screenWidth = deviceConfig.screenHeight,
                nightMode = NightMode.NOTNIGHT
            )
        )
    }

    private val darkPortraitConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                nightMode = NightMode.NIGHT
            )
        )
    }

    private val darkLandscapeConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                orientation = ScreenOrientation.LANDSCAPE,
                screenHeight = deviceConfig.screenWidth,
                screenWidth = deviceConfig.screenHeight,
                nightMode = NightMode.NIGHT
            )
        )
    }

    private val lightPortraitRtlConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                layoutDirection = LayoutDirection.RTL
            )
        )
    }

    private val darkPortraitRtlConfig = {
        paparazzi.unsafeUpdateConfig(
            deviceConfig = deviceConfig.copy(
                nightMode = NightMode.NIGHT,
                layoutDirection = LayoutDirection.RTL
            )
        )
    }
}