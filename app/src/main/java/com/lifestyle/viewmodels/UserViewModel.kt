package com.lifestyle.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.interfaces.IUserProfile
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.PartialUserProfile.Companion.shouldIncludeInUpdate
import com.lifestyle.models.StoredUser
import com.lifestyle.repositories.LoginRepository

// extend from AndroidViewModel() instead of ViewModel() since we need a reference to Context
class UserViewModel(application: Application) : AndroidViewModel(application) {

    // won't actually leak since `applicationContext` lives until the entire app is destroyed
    @SuppressLint("StaticFieldLeak")
    private var appContext = application.applicationContext
    private var loginRepository: LoginRepository = LoginRepository.getInstance(appContext)

    // if there is no logged in user, user.value will be null
    var loggedInUser = MutableLiveData<StoredUser?>()

    init {
        fetchUser()
    }

    /**
     * Call this method to refresh/rebind live data
     * Since LoginRepository is a singleton, all UserViewModels will be notified when `loggedInUser` changes.
     */
    private fun fetchUser() {
        loginRepository.fetchLoggedInUser()
        loggedInUser = loginRepository.loggedInUser
    }


    fun doesUserExist(username: String): Boolean {
        return loginRepository.doesUserExist(username)
    }

    fun login(username: String) {
        loginRepository.login(username)
    }

    fun logout(username: String) {
        loginRepository.logout()
    }

    /**
     * Update the currently logged in user with partial data (non-null fields in IUserProfile)
     * Useful for when you only want to update a subset of user settings.
     *
     * (e.g.) If we want to just update the user's city and state and not touch anything else:
     *
     *    // updates user city and state settings ONLY
     *    updateUserProfilePartial(PartialUserProfile(username).apply {
     *       this.city = "Salt Lake City"
     *       this.state = "Utah"
     *    });
     */
    fun updateUserProfilePartial(profile: PartialUserProfile) {
        val loggedInUser: StoredUser? = loggedInUser.value

        // user must be logged in
        if (loggedInUser != null) {
            // only write to user profile if the data was actually touched (updated)
            if (profile.fullName.shouldIncludeInUpdate()) loggedInUser.fullName = profile.fullName
            if (profile.age.shouldIncludeInUpdate()) loggedInUser.age = profile.age
            if (profile.city.shouldIncludeInUpdate()) loggedInUser.city = profile.city
            if (profile.state.shouldIncludeInUpdate()) loggedInUser.state = profile.state
            if (profile.country.shouldIncludeInUpdate()) loggedInUser.country = profile.country
            if (profile.sex.shouldIncludeInUpdate()) loggedInUser.sex = profile.sex
            if (profile.weight.shouldIncludeInUpdate()) loggedInUser.weight = profile.weight
            if (profile.height.shouldIncludeInUpdate()) loggedInUser.height = profile.height
            if (profile.pictureURI.shouldIncludeInUpdate()) loggedInUser.pictureURI = profile.pictureURI
            if (profile.weightChange.shouldIncludeInUpdate()) loggedInUser.weightChange = profile.weightChange

            // notify changes
            this.loggedInUser.postValue(this.loggedInUser.value)
        }
    }

    /**
     * Completely replace stored user with the specified user profile.
     */
    fun updateUserProfileFull(profile: IUserProfile) {
        val loggedInUser: StoredUser? = loggedInUser.value

        // user must be logged in
        if (loggedInUser != null) {
            // write to user profile regardless if settings were touched
            loggedInUser.fullName = profile.fullName
            loggedInUser.age = profile.age
            loggedInUser.city = profile.city
            loggedInUser.state = profile.state
            loggedInUser.country = profile.country
            loggedInUser.sex = profile.sex
            loggedInUser.weight = profile.weight
            loggedInUser.height = profile.height
            loggedInUser.pictureURI = profile.pictureURI
            loggedInUser.weightChange = profile.weightChange

            // notify changes
            this.loggedInUser.postValue(this.loggedInUser.value)
        }
    }
}