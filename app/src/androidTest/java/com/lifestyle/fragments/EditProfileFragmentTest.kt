package com.lifestyle.fragments

import android.content.Context
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.R
import com.lifestyle.LifestyleTestHelper
import com.lifestyle.LifestyleTestHelper.Companion.TEST_USERNAME
import com.lifestyle.LifestyleTestHelper.Companion.TEST_FULL_NAME
import com.lifestyle.models.EditProfileResult
import com.lifestyle.models.LoginSession
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@Suppress("IllegalIdentifier")
@RunWith(AndroidJUnit4::class)
class EditProfileFragmentTest {

    companion object {
        // creates a new instance of the fragment
        // theme is needed here since this fragment uses Material Components
        fun editProfileFragmentInitializer() =
            launchFragment<EditProfileFragment>(themeResId = R.style.Theme_MaterialComponents)
    }

    @After
    fun afterEachCleanup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        LifestyleTestHelper.deleteUser(appContext, TEST_USERNAME)
        LoginSession.getInstance(appContext).logout()
    }

    @Test
    fun `should validate username field`() {
        with(editProfileFragmentInitializer()) {
            onFragment { frag ->
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                frag.textLayoutUsername.editText?.setText("a")
                val result = frag.aggregateFieldsAndWrite()
                assertHasErrors(result, "Validation failed: username length")
            }
        }
    }

    @Test
    fun `should validate full name field`() {
        // theme is needed here since TextLayoutInput doesn't use default style in the XML
        with(editProfileFragmentInitializer()) {
            onFragment { frag ->
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                frag.textLayoutFullName.editText?.setText("a")
                val result = frag.aggregateFieldsAndWrite()
                assertHasErrors(result, "Validation failed: full name length")
            }
        }
    }

    @Test
    fun `should validate height fields`() {
        // theme is needed here since TextLayoutInput doesn't use default style in the XML
        with(editProfileFragmentInitializer()) {
            onFragment { frag ->
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                frag.textLayoutHeightFt.editText?.setText("5")
                val result = frag.aggregateFieldsAndWrite()
                assertHasErrors(
                    result,
                    "Validation failed: if one height field is filled, the other one must be as well"
                )
            }
        }

        with(editProfileFragmentInitializer()) {
            onFragment { frag ->
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                frag.textLayoutHeightIn.editText?.setText("10")
                val result = frag.aggregateFieldsAndWrite()
                assertHasErrors(
                    result,
                    "Validation failed: if one height field is filled, the other one must be as well"
                )
            }
        }
    }

    @Test
    fun `should create new user if no errors`() {
        with(editProfileFragmentInitializer()) {
            onFragment { frag ->
                LifestyleTestHelper.fillRequiredSignupFields(frag)
                val result = frag.aggregateFieldsAndWrite()
                assertNoErrors(result)

                // clean up successful user creation
                assertTrue(LifestyleTestHelper.deleteUser(frag.requireContext(), TEST_USERNAME))
            }
        }
    }

    private fun assertHasErrors(result: EditProfileResult?, errMsg: String? = null) {
        if (result == null)
            fail("Aggregated result was be null")

        if (errMsg != null)
            assertFalse(errMsg, result!!.success)
        else
            assertFalse(result!!.success)
    }

    private fun assertNoErrors(result: EditProfileResult?, errMsg: String? = null) {
        if (result == null)
            fail("Aggregated result was be null")

        if (errMsg != null)
            assertTrue(errMsg, result!!.success)
        else
            assertTrue(result!!.success)
    }
}