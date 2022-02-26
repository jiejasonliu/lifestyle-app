package com.lifestyle

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_USERNAME
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    companion object {
        fun homeActivityInitializer(): ActivityScenario<HomeActivity> =
            ActivityScenario.launch(HomeActivity::class.java)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should work if user is logged in`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLogin(appContext, TEST_USERNAME, TEST_FULL_NAME)

        // should allow entry
        val scenario = homeActivityInitializer()
        assertFalse(scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `dashboard should use the full name of the logged in user`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLogin(appContext, TEST_USERNAME, TEST_FULL_NAME)

        with(HomeActivityTest.homeActivityInitializer()) {
            onActivity { activity ->
                assertEquals(TEST_FULL_NAME, activity.textViewMyDashboard.text.split(",")[1].trim())
            }
        }
    }
}