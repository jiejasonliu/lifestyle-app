package com.lifestyle

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class ProfileActivityTest {

    companion object {
        fun profileActivityInitializer(): ActivityScenario<ProfileActivity> =
            ActivityScenario.launch(ProfileActivity::class.java)
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
        val scenario = profileActivityInitializer()
        assertFalse(scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `should reject entry if not logged in`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LoginSession.getInstance(appContext).logout()   // logout

        // should reject entry
        val scenario = profileActivityInitializer()
        assertTrue(scenario.state == Lifecycle.State.DESTROYED)
    }

    // implies that other fields will be autofilled as well
    @Test
    fun `should autofill in fields`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLogin(appContext, TEST_USERNAME, TEST_FULL_NAME)

        with(profileActivityInitializer()) {
            onActivity { activity ->
                val frag = activity.editProfileFragment
                assertEquals(TEST_USERNAME, frag.getText(frag.textLayoutUsername))
                assertEquals(TEST_FULL_NAME, frag.getText(frag.textLayoutFullName))
            }
        }
    }

    @Test
    fun `should disallow username edit`() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // create test user and login with it
        val testUser = StoredUser(appContext, TEST_USERNAME)
        testUser.fullName = TEST_FULL_NAME
        LoginSession.getInstance(appContext).login(TEST_USERNAME)

        with(profileActivityInitializer()) {
            onActivity { activity ->
                val frag = activity.editProfileFragment
                assertFalse(frag.textLayoutUsername.isEnabled)
            }
        }
    }

    @Test
    fun `should still validate fields`() {
        // create test user and login with it
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.createUserAndLogin(appContext, TEST_USERNAME, TEST_FULL_NAME)

        with(profileActivityInitializer()) {
            onActivity { activity ->
                val appContext = activity.applicationContext
                val frag = activity.editProfileFragment

                // invariant: since we're logged in, username field should be filled already
                // also see: this::`should autofill in fields` for test coverage

                // should fail with invalid full name
                frag.textLayoutFullName.editText?.setText("a") // invalid full name (too short)
                var result = frag.aggregateFieldsAndWrite()!!
                assertFalse(result.success)

                // should work with valid full name
                frag.textLayoutFullName.editText?.setText(TEST_FULL_NAME)
                result = frag.aggregateFieldsAndWrite()!!
                assertTrue(result.success)
            }
        }
    }
}