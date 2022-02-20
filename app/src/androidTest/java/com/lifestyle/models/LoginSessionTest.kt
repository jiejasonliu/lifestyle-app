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
class LoginSessionTest {

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should login and persist user state`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME

        // login
        val loginSession = LoginSession.getInstance(appContext)
        loginSession.login(TEST_USERNAME)

        // check persisted user
        assertTrue(loginSession.isLoggedIn())
        val persistedUser = loginSession.getLoggedInUser()
        assertEquals(TEST_FULL_NAME, persistedUser?.fullName)
    }

    @Test
    fun `should logout correctly`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME

        // login and check state
        val loginSession = LoginSession.getInstance(appContext)
        loginSession.login(TEST_USERNAME)
        assertTrue(loginSession.isLoggedIn())

        // logout and check state
        loginSession.logout()
        assertFalse(loginSession.isLoggedIn())
    }

    @Test
    fun `doesUserExist works properly`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val loginSession = LoginSession.getInstance(appContext)

        // user shouldn't exist yet
        assertFalse(loginSession.doesUserExist(TEST_USERNAME))

        // create user and check that it exists now
        val appliedUser = StoredUser(appContext, TEST_USERNAME)
        appliedUser.fullName = TEST_FULL_NAME
        assertTrue(loginSession.doesUserExist(TEST_USERNAME))

        // cleanup and check that user no longer exists
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        assertFalse(loginSession.doesUserExist(TEST_USERNAME))
    }
}