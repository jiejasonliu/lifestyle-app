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
class HikingActivityTest {

    companion object {
        fun hikingActivityInitializer(): ActivityScenario<HikingActivity> =
            ActivityScenario.launch(HikingActivity::class.java)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should allow entry if not logged in`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LoginSession.getInstance(appContext).logout()   // logout

        // should reject entry
        val scenario = hikingActivityInitializer()
        assertFalse(scenario.state == Lifecycle.State.DESTROYED)
    }
}