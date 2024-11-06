package com.urlaunched.android.common.navigation

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.application.FrameMetricsRecorder
import com.urlaunched.android.common.lifecycle.HandleLifecycleEvents

@Composable
internal fun LogScreenEvents(route: String) {
    val routeName = route.split("/").first()
    val activity = LocalContext.current as Activity

    HandleLifecycleEvents(
        lifecycleOwner = LocalLifecycleOwner.current,
        onStart = {
            Log.d("TESTT", "LogScreenEvents: ${ON_START.format(route)} ")
            Firebase.crashlytics.log(ON_START.format(route))
        },
        onStop = {
            Log.d("TESTT", "LogScreenEvents: ${ON_STOP.format(route)} ")
            Firebase.crashlytics.log(ON_STOP.format(route))
        }
    )

    DisposableEffect(route) {
        val trace = FirebasePerformance.getInstance().newTrace(routeName).apply { start() }
        val recorder = FrameMetricsRecorder(activity).apply { start() }

        onDispose {
            val metrics = recorder.stop()

            if (metrics.isAvailable) {
                val frameMetrics = metrics.get()
                trace.putMetric(FROZEN_FRAMES_TAG, frameMetrics.frozenFrames.toLong())
                trace.putMetric(SLOW_FRAMES_TAG, frameMetrics.slowFrames.toLong())
                trace.putMetric(TOTAL_FRAMES_TAG, frameMetrics.totalFrames.toLong())
            }

            trace.stop()
        }
    }
}

const val FROZEN_FRAMES_TAG = "frozen_frames"
const val SLOW_FRAMES_TAG = "slow_frames"
const val TOTAL_FRAMES_TAG = "total_frames"
const val ON_START = "Screen %s Started"
const val ON_STOP = "Screen %s Stopped"