package com.lifestyle.models

import android.content.Context
import android.content.SharedPreferences
import com.lifestyle.interfaces.IUserProfile

/**
 * @param sharedPreferences name used to create this instance should be equivalent to the username
 */
class StoredUser (private val appContext: Context, private val _username: String) : IUserProfile {

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(_username, Context.MODE_PRIVATE)

    init {
        val editor = sharedPreferences.edit()
        editor.putString("username", _username)
        editor.apply()
    }

    // username should not be able to be modified
    override val username: String = sharedPreferences.getString("username", null) ?: "<error>"

    override var fullName: String
        get() = sharedPreferences.getString("fullName", null) ?: ""
        set(v) = putStringAsync("fullName", v)

    override var age: Int
        get() = sharedPreferences.getInt("age", 0)
        set(v) = putIntAsync("age", v)

    override var city: String
        get() = sharedPreferences.getString("city", null) ?: ""
        set(v) = putStringAsync("city", v)

    override var state: String
        get() = sharedPreferences.getString("state", null) ?: ""
        set(v) = putStringAsync("state", v)

    override var country: String
        get() = sharedPreferences.getString("country", null) ?: ""
        set(v) = putStringAsync("country", v)

    override var height: Int
        get() = sharedPreferences.getInt("height", 0)
        set(v) = putIntAsync("height", v)

    override var weight: Int
        get() = sharedPreferences.getInt("weight", 0)
        set(v) = putIntAsync("weight", v)

    override var sex: String
        get() = sharedPreferences.getString("sex", null) ?: ""
        set(v) = putStringAsync("sex", v)

    override var pictureURI: String?
        get() = sharedPreferences.getString("pictureURI", null)
        set(v) {
            if (v != null) putStringAsync("pictureURI", v)
        }

    /////////////////////////////////////////////////////////////////////////
    // calling these async functions will cause a write-defer (write-back) //
    // in other words, affects memory but not disk until OS has resources. //
    /////////////////////////////////////////////////////////////////////////

    private fun putIntAsync(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun putStringAsync(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun putIntSynchronous(key: String, value: Int): Boolean {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        return editor.commit() // immediately write to disk
    }

    private fun putStringSynchronous(key: String, value: String): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        return editor.commit() // immediately write to disk
    }
}


