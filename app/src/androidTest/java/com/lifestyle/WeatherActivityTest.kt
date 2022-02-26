package com.lifestyle

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_USERNAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_COUNTRY
import com.lifestyle.LifestyleTestHelper.Companion.TEST_CITY
import com.lifestyle.LifestyleTestHelper.Companion.TEST_INVALID_CITY
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class WeatherActivityTest {

    companion object {
        fun weatherActivityInitializer(): ActivityScenario<WeatherActivity> =
            ActivityScenario.launch(WeatherActivity::class.java)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should reject entry if not logged in`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LoginSession.getInstance(appContext).logout()   // logout

        // should reject entry
        val scenario = weatherActivityInitializer()
        assertTrue(scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `should reject entry if logged in but no location info`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLogin(appContext, TEST_USERNAME, TEST_FULL_NAME)

        // should reject entry
        val scenario = weatherActivityInitializer()
        assertTrue(scenario.state == Lifecycle.State.DESTROYED)
    }

    // Time sensitive test, not sure how to deal with. Temporary solution is to sleep for 5 seconds. Succeeds on most machines.
    @Test
    fun `should reject if logged in and has invalid location info`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLoginWithLocation(appContext, TEST_USERNAME, TEST_FULL_NAME, TEST_INVALID_CITY, TEST_COUNTRY)

        // should reject entry
        val scenario = weatherActivityInitializer()
        Thread.sleep(5000)
        assertTrue(scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `should work if logged in and has valid location info`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLoginWithLocation(appContext, TEST_USERNAME, TEST_FULL_NAME, TEST_CITY, TEST_COUNTRY)

        // should allow entry
        val scenario = weatherActivityInitializer()
        assertFalse(scenario.state == Lifecycle.State.DESTROYED)
    }
}