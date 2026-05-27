package com.example.zerosekai.data.repository

import android.content.Context
import android.net.Uri

import com.example.zerosekai.data.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    private val storage =
        FirebaseStorage.getInstance()

    suspend fun getUser(): User? {

        val currentUser =
            auth.currentUser ?: return null

        val uid =
            currentUser.uid

        val doc =
            firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

        val fallbackName =
            currentUser.displayName
                ?: currentUser.email?.substringBefore("@")
                ?: "Zero User"

        val fallbackUser = User(
            uid = uid,
            username = fallbackName,
            email = currentUser.email ?: "",
            bio = "Usuario oficial do ZeroSekai",
            photoUrl = currentUser.photoUrl?.toString() ?: ""
        )

        return doc.toObject(User::class.java)
            ?.copy(
                uid = uid,
                email = doc.getString("email") ?: fallbackUser.email,
                username = doc.getString("username") ?: fallbackUser.username,
                bio = doc.getString("bio") ?: fallbackUser.bio,
                photoUrl = doc.getString("photoUrl") ?: fallbackUser.photoUrl
            )
            ?: fallbackUser
    }

    suspend fun updateProfile(
        username: String,
        bio: String,
        photoUrl: String? = null
    ) {

        val currentUser =
            auth.currentUser ?: return

        val uid =
            currentUser.uid

        val updates = mutableMapOf<String, Any>(
            "uid" to uid,
            "username" to username,
            "email" to (currentUser.email ?: ""),
            "bio" to bio
        )

        if (!photoUrl.isNullOrBlank()) {
            updates["photoUrl"] = photoUrl
        }

        firestore
            .collection("users")
            .document(uid)
            .set(updates, SetOptions.merge())
            .await()

        val profileUpdates =
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .apply {
                    if (!photoUrl.isNullOrBlank()) {
                        setPhotoUri(Uri.parse(photoUrl))
                    }
                }
                .build()

        currentUser
            .updateProfile(profileUpdates)
            .await()
    }

    suspend fun uploadProfilePhoto(
        context: Context,
        imageUri: Uri
    ): String? {

        val currentUser =
            auth.currentUser ?: return null

        val ref =
            storage.reference
                .child("profile_photos")
                .child("${currentUser.uid}.jpg")

        ref.putFile(imageUri)
            .await()

        return ref.downloadUrl
            .await()
            .toString()
    }
}
