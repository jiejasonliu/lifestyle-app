package com.lifestyle.models

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.LifestyleTestHelper
import com.lifestyle.LifestyleTestHelper.Companion.TEST_USERNAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class StoredUserTest {

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `adds username to SharedPreference on init`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)

        assertEquals(TEST_USERNAME, appliedUser?.username)
    }

    @Test
    fun `updates SharedPreferences on edit`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME

        assertEquals(TEST_FULL_NAME, appliedUser?.fullName)
    }

    @Test
    fun `persists data through StoredUser objects`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME

        val persistedUser = StoredUser(appContext, TEST_USERNAME) // new instance
        assertEquals(TEST_FULL_NAME, persistedUser?.fullName)
    }

    @Test
    fun `deletion clears StoredUser`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME

        // delete and create new instance
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        val persistedUser = StoredUser(appContext, TEST_USERNAME) // new instance
        assertNotEquals(TEST_FULL_NAME, persistedUser?.fullName)
    }
}