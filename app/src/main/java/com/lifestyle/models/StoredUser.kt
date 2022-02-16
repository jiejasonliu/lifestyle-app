package com.lifestyle.models

import android.content.Context
import android.content.SharedPreferences
import com.lifestyle.interfaces.IUserProfile

/**
 * @param _username this must be unique for each user; used as a key to the user's SharedPreference
 */
class StoredUser(private val appContext: Context, private val _username: String) : IUserProfile {

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(_username, Context.MODE_PRIVATE)

    init {
        // this user does not have a username in SharedPreferences on the first instantiation
        if (!sharedPreferences.contains("username")) {
            val editor = sharedPreferences.edit()
            editor.putString("username", _username)
            editor.apply()
        }
    }

    // note: username should not be able to be modified
    override val username: String = sharedPreferences.getString("username", null) ?: "<error>"

    override var fullName: String
        get() = sharedPreferences.getString("fullName", null) ?: "<error>"
        set(v) = putStringAsync("fullName", v)

    override var age: Int?
        get() = getNullableInt(sharedPreferences.getInt("age", -1), -1)
        set(v) = putIntAsync("age", v)

    override var city: String?
        get() = sharedPreferences.getString("city", null)
        set(v) = putStringAsync("city", v)

    override var state: String?
        get() = sharedPreferences.getString("state", null)
        set(v) = putStringAsync("state", v)

    override var country: String?
        get() = sharedPreferences.getString("country", null)
        set(v) = putStringAsync("country", v)

    override var height: Int?
        get() = getNullableInt(sharedPreferences.getInt("height", -1), -1)
        set(v) = putIntAsync("height", v)

    override var weight: Int?
        get() = getNullableInt(sharedPreferences.getInt("weight", -1), -1)
        set(v) = putIntAsync("weight", v)

    override var sex: String?
        get() = sharedPreferences.getString("sex", null)
        set(v) = putStringAsync("sex", v)

    override var pictureURI: String?
        get() = sharedPreferences.getString("pictureURI", null)
        set(v) {
            if (v != null) putStringAsync("pictureURI", v)
        }

    override var weightChange: Int?
        get() = getNullableInt(sharedPreferences.getInt("weightChange", 0), 0)
        set(v) {
            if (v != null) putIntAsync("weightChange", v)
        }

    private fun getNullableInt(value: Int, sentinelValue: Int): Int? {
        if (value == sentinelValue)
            return null
        return value
    }

    private fun putIntSynchronous(key: String, value: Int?): Boolean {
        val editor = sharedPreferences.edit()
        if (value == null) {
            editor.remove(key)
            return editor.commit()
        }

        editor.putInt(key, value)
        return editor.commit() // immediately write to disk
    }

    private fun putStringSynchronous(key: String, value: String?): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)    // if value == null, same as remove(key)
        return editor.commit()          // immediately write to disk
    }

    /////////////////////////////////////////////////////////////////////////
    // calling these async functions will cause a write-defer (write-back) //
    // in other words, affects memory but not disk until OS has resources. //
    /////////////////////////////////////////////////////////////////////////

    private fun putIntAsync(key: String, value: Int?) {
        val editor = sharedPreferences.edit()
        if (value == null) {
            editor.remove(key)
            editor.apply()
            return
        }

        editor.putInt(key, value)
        editor.apply()
    }

    private fun putStringAsync(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)    // if value == null, same as remove(key)
        editor.apply()
    }
}


