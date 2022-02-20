package com.lifestyle

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 * See [testing documentation](http://d.android.com/tools/testing).
 *
 * For ease of compatibility, copy this to the top your test files:
-------------------------------------------------------------------
    import androidx.test.ext.junit.runners.AndroidJUnit4
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.junit.Assert.*

    @Suppress("IllegalIdentifier")
    @RunWith(AndroidJUnit4::class)
-------------------------------------------------------------------
 */
@Suppress("IllegalIdentifier")  // allows naming functions with backticks (see below)
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun `uses correct app context`() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.lifestyle", appContext.packageName)
    }
}