package com.lifestyle.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.interfaces.IUserProfile
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.PartialUserProfile.Companion.shouldIncludeInUpdate
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.repositories.LoginRepository
import com.lifestyle.repositories.UserProfileRepository

// extend from AndroidViewModel() instead of ViewModel() since we need a reference to Context
class UserViewModel(application: Application) : AndroidViewModel(application) {

    // won't actually leak since `applicationContext` lives until the entire app is destroyed
    @SuppressLint("StaticFieldLeak")
    private var appContext = application.applicationContext
    private var loginRepository: LoginRepository = LoginRepository.getInstance(appContext)
    private var userProfileRepository = UserProfileRepository.getInstance(appContext)

    // if there is no logged in user, user.value will be null
    var loggedInUser = MutableLiveData<UserProfileEntity?>()

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
        return userProfileRepository.doesUserExistBlocking(username)
    }

    fun login(username: String) {
        loginRepository.login(username)
    }

    fun logout() {
        loginRepository.logout()
    }

    fun isLoggedIn(): Boolean {
        return this.loggedInUser.value != null
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
        val loggedInUser: IUserProfile? = loggedInUser.value
        if (loggedInUser != null) {
            val userProfile = UserProfileEntity(
                username = profile.username ?: loggedInUser.username ?: "",
                fullName = if (profile.fullName.shouldIncludeInUpdate()) profile.fullName else loggedInUser.fullName,
                age = if (profile.age.shouldIncludeInUpdate()) profile.age else loggedInUser.age,
                city = if (profile.city.shouldIncludeInUpdate()) profile.city else loggedInUser.city,
                state = if (profile.state.shouldIncludeInUpdate()) profile.state else loggedInUser.state,
                country = if (profile.country.shouldIncludeInUpdate()) profile.country else loggedInUser.country,
                sex = if (profile.sex.shouldIncludeInUpdate()) profile.sex else loggedInUser.sex,
                weight = if (profile.weight.shouldIncludeInUpdate()) profile.weight else loggedInUser.weight,
                height = if (profile.height.shouldIncludeInUpdate()) profile.height else loggedInUser.height,
                pictureURI = if (profile.pictureURI.shouldIncludeInUpdate()) profile.pictureURI else loggedInUser.pictureURI,
                weightChange = if (profile.weightChange.shouldIncludeInUpdate())  profile.weightChange else loggedInUser.weightChange,
            )

            // update and notify changes
            userProfileRepository.updateUser(userProfile)
            loginRepository.fetchLoggedInUser()
        }
    }

    fun addNewUser(username: String) {
        userProfileRepository.insertUser(UserProfileEntity(
            username = username,
            fullName = null,
            age = null,
            city = null,
            state = null,
            country = null,
            height = null,
            weight = null,
            sex = null,
            pictureURI = null,
            weightChange = null,
        ))

        // notify changes
        loginRepository.fetchLoggedInUser()
    }
}