package com.lifestyle

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser
import com.lifestyle.LifestyleTestHelper.Companion.TEST_USERNAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class SignupActivityTest {

    companion object {
        fun signupActivityInitializer(): ActivityScenario<SignupActivity> =
            ActivityScenario.launch(SignupActivity::class.java)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should still validate signup fields`() {
        with(signupActivityInitializer()) {
            onActivity { activity ->
                val appContext = activity.applicationContext
                val frag = activity.signupFragment

                // should fail with invalid username
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                frag.textLayoutUsername.editText?.setText("a") // invalid username (too short)
                var result = frag.aggregateFieldsAndWrite()!!
                assertFalse(result.success)

                // should work with valid username
                LifestyleTestHelper.fillRequiredSignupFields(frag) // valid username
                result = frag.aggregateFieldsAndWrite()!!
                assertTrue(result.success)
            }
        }
    }
}