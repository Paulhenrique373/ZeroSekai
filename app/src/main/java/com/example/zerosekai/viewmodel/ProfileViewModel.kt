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

    private val repository = ProfileRepository()

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

        refreshUser()
    }

    fun refreshUser() {

        viewModelScope.launch {

            try {

                _error.value = null

                val updatedUser =
                    repository.getUser()

                _user.value =
                    updatedUser

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Erro ao carregar perfil"
            }
        }
    }

    fun loadUser() {

        refreshUser()
    }

    fun updateProfile(
        username: String,
        bio: String,
        context: Context? = null,
        imageUri: Uri? = null,
        onFinished: () -> Unit = {}
    ) {

        if (_saving.value) return

        viewModelScope.launch {

            _saving.value = true

            _error.value = null

            try {

                val currentUser =
                    _user.value

                var finalPhotoUrl =
                    currentUser?.photoUrl ?: ""

                if (
                    context != null &&
                    imageUri != null
                ) {

                    finalPhotoUrl =
                        repository.uploadProfilePhoto(
                            context = context,
                            imageUri = imageUri
                        )
                }

                repository.updateProfile(
                    username = username,
                    bio = bio,
                    photoUrl = finalPhotoUrl
                )

                val updatedUser =
                    repository.getUser()

                _user.value =
                    updatedUser

                refreshUser()

                onFinished()

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message
                        ?: "Erro ao salvar perfil"

            } finally {

                _saving.value = false
            }
        }
    }

    fun clearError() {

        _error.value = null
    }
}