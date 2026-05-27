package com.example.zerosekai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerosekai.data.model.User
import com.example.zerosekai.data.repository.FollowRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val followRepository =
        FollowRepository()

    private val _user =
        MutableStateFlow<User?>(null)

    val user: StateFlow<User?> =
        _user.asStateFlow()

    fun loadUser(userId: String) {

        viewModelScope.launch {

            try {

                val doc =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()

                if (doc.exists()) {

                    _user.value =
                        doc.toObject(User::class.java)
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    fun toggleFollow(userId: String) {

        viewModelScope.launch {

            try {

                followRepository.toggleFollow(userId)

                loadUser(userId)

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}