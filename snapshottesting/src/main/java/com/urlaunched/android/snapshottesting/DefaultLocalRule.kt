package com.urlaunched.android.snapshottesting

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.ZoneOffset
import java.util.Locale
import java.util.TimeZone

class DefaultLocalRule : TestWatcher() {
    private val locale = Locale.getDefault()
    private val timeZone = TimeZone.getDefault()

    override fun starting(description: Description) {
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC))
    }

    override fun finished(description: Description) {
        Locale.setDefault(locale)
        TimeZone.setDefault(timeZone)
    }
}