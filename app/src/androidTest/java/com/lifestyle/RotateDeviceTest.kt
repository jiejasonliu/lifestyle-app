package com.lifestyle

import android.R.attr
import android.app.UiAutomation
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.models.LoginSession
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.content.res.Configuration


@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class RotateDeviceTest {

    companion object {
        fun profileActivityInitializer(): ActivityScenario<SignupActivity> =
            ActivityScenario.launch(SignupActivity::class.java)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, LifestyleTestHelper.TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
        InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(UiAutomation.ROTATION_UNFREEZE)
    }

    @Test
    fun `data should persist in SignupActivity on screen rotation`() {
        val device = InstrumentationRegistry.getInstrumentation()

        with(profileActivityInitializer()) {
            onActivity { activity ->
                // fill fields and check they exist
                var frag = activity.signupFragment
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                assertEquals(TEST_FULL_NAME, frag.getText(frag.textLayoutFullName))

                // wait for screen rotation
                activity.requestedOrientation = Configuration.ORIENTATION_LANDSCAPE
                device.uiAutomation.setRotation(UiAutomation.ROTATION_FREEZE_90)

                // check if fields still exist after rotation
                frag = activity.signupFragment // ref could have changed
                assertEquals(TEST_FULL_NAME, frag.getText(frag.textLayoutFullName))
            }
        }
    }
}