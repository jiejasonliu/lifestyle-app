package com.lifestyle

import android.content.Context
import com.lifestyle.fragments.EditProfileFragment
import com.lifestyle.fragments.EditProfileFragmentTest
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser

// dump of helper functions for tests
class LifestyleTestHelper {

    companion object {
        const val TEST_USERNAME = "TEST_TEMP_USERNAME"
        const val TEST_FULL_NAME = "TEST_TEMP_FULL_NAME"
        const val TEST_CITY = "Salt Lake City"
        const val TEST_COUNTRY = "United States"
        const val TEST_INVALID_CITY = "TEST_TEMP_INVALID_CITY"

        // todo: update as needed if 'required fields' change
        // fill all the 'required fields' so we can isolate errors on a different field
        fun fillRequiredSignupFields(fragment: EditProfileFragment) {
            fragment.textLayoutUsername.editText?.setText(TEST_USERNAME)
            fragment.textLayoutFullName.editText?.setText(TEST_FULL_NAME)
        }

        fun createUserAndLogin(context: Context, username: String, fullName: String) {
            val testUser = StoredUser(context, username)
            testUser.fullName = fullName
            LoginSession.getInstance(context).login(username)
        }

        fun createUserAndLoginWithLocation(context: Context, username: String, fullName: String, city: String, country: String) {
            val testUser = StoredUser(context, username)
            testUser.fullName = fullName
            testUser.city = city
            testUser.country = country
            LoginSession.getInstance(context).login(username)
        }

        fun deleteUser(context: Context, username: String): Boolean {
            val pref = context.getSharedPreferences(username, Context.MODE_PRIVATE)
            return pref.edit().clear().commit()
        }
    }
}