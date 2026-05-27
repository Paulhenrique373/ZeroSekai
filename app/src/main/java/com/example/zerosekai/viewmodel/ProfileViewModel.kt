package com.example.zerosekai.viewmodel

import android.content.Context
import android.net.Uri

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.zerosekai.data.model.User
import com.example.zerosekai.data.repository.ProfileRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository =
        ProfileRepository()

    private val _user =
        MutableStateFlow<User?>(null)

    val user: StateFlow<User?> =
        _user.asStateFlow()

    private val _saving =
        MutableStateFlow(false)

    val saving: StateFlow<Boolean> =
        _saving.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    init {

        loadUser()
    }

    fun loadUser() {

        viewModelScope.launch {

            try {

                _user.value =
                    repository.getUser()

            } catch (e: Exception) {

                _error.value =
                    "Erro ao carregar perfil"
            }
        }
    }

    fun updateProfile(

        username: String,
        bio: String,
        context: Context? = null,
        imageUri: Uri? = null,
        onFinished: () -> Unit = {}

    ) {

        viewModelScope.launch {

            _saving.value = true

            _error.value = null

            try {

                val photoUrl =
                    if (context != null && imageUri != null) {
                        repository.uploadProfilePhoto(
                            context,
                            imageUri
                        )
                    } else {
                        null
                    }

                repository.updateProfile(

                    username,
                    bio,
                    photoUrl
                )

                loadUser()

                onFinished()

            } catch (e: Exception) {

                _error.value =
                    "Erro ao salvar perfil"

            } finally {

                _saving.value = false
            }
        }
    }
}
