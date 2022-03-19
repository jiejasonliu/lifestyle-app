package com.lifestyle.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.models.StoredUser
import com.lifestyle.repositories.LoginRepository

// extend from AndroidViewModel() instead of ViewModel() since we need a reference to Context
class UserViewModel(application: Application) : AndroidViewModel(application) {
    var userLiveData = MutableLiveData<StoredUser?>()

    init {
        fetchData(application.applicationContext)
    }

    /**
     * Call this method to refresh/rebind live data
     */
    fun fetchData(appContext: Context) {
        fetchUser(appContext)
    }

    /**
     * Since LoginRepository is a singleton, all UserViewModels will be notified when `loggedInUser` changes.
     */
    private fun fetchUser(appContext: Context) {
        val loginRepository = LoginRepository.getInstance(appContext)
        loginRepository.fetchLoggedInUser()
        userLiveData = loginRepository.loggedInUser
    }
}