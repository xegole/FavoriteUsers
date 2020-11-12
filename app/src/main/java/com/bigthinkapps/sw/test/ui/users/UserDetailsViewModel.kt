package com.bigthinkapps.sw.test.ui.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bigthinkapps.sw.test.R
import com.bigthinkapps.sw.test.dtos.LoadingProgress
import com.bigthinkapps.sw.test.models.User
import com.bigthinkapps.sw.test.repositories.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserDetailsViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope {
    val savingToFavorites = MutableLiveData<Boolean>()
    val contactSaved = MutableLiveData<Boolean>()
    val loading = MutableLiveData<LoadingProgress>()
    private val repository = UsersRepository(application)
    val user = MutableLiveData<User>()

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    val addToContacts = MutableLiveData<Boolean>()

    fun getUserDetails(userId: Int) {
        loading.value = LoadingProgress(R.string.loading_user_details)
        launch {
            user.value = repository.getUserById(userId)
            loading.value = null
        }
    }

    fun saveToFavorites() {
        savingToFavorites.value = true
        launch {
            user.value?.let {
                repository.saveFavoriteUser(it.id)
                savingToFavorites.value = false
                contactSaved.value = true
            }
        }
    }

    fun addToContacts() {
        this.user.value?.let {
            addToContacts.value = true
//            getApplication<Application>().addContact(it.fullName, it.phone, it.email)
        }
    }
}