package com.example.zerosekai.data.repository

import android.content.Context
import android.net.Uri

import com.example.zerosekai.data.model.User
import com.example.zerosekai.supabase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import io.github.jan.supabase.storage.storage

import kotlinx.coroutines.tasks.await

import java.util.UUID

class ProfileRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    suspend fun getUser(): User? {

        val currentUser =
            auth.currentUser ?: return null

        val uid =
            currentUser.uid

        return try {

            val document =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

            val fallbackUsername =
                currentUser.displayName
                    ?: currentUser.email
                        ?.substringBefore("@")
                    ?: "Zero User"

            val user = User(

                uid = uid,

                username =
                document.getString("username")
                    ?: fallbackUsername,

                email =
                document.getString("email")
                    ?: (currentUser.email ?: ""),

                bio =
                document.getString("bio")
                    ?: "Usuário oficial do ZeroSekai",

                photoUrl =
                document.getString("photoUrl")
                    ?: (
                            currentUser.photoUrl
                                ?.toString()
                                ?: ""
                            ),

                followers =
                document.get("followers")
                        as? List<String>
                    ?: emptyList(),

                following =
                document.get("following")
                        as? List<String>
                    ?: emptyList()
            )

            if (!document.exists()) {

                firestore
                    .collection("users")
                    .document(uid)
                    .set(user)
                    .await()
            }

            user

        } catch (e: Exception) {

            e.printStackTrace()

            User(

                uid = uid,

                username =
                currentUser.displayName
                    ?: "Zero User",

                email =
                currentUser.email ?: "",

                bio =
                "Usuário oficial do ZeroSekai",

                photoUrl =
                currentUser.photoUrl
                    ?.toString()
                    ?: "",

                followers = emptyList(),

                following = emptyList()

            )
        }
    }

    suspend fun updateProfile(

        username: String,
        bio: String,
        photoUrl: String? = null

    ) {

        val currentUser =
            auth.currentUser
                ?: throw Exception(
                    "Usuário não autenticado"
                )

        val uid =
            currentUser.uid

        val currentData =
            getUser()

        val finalPhotoUrl =

            if (!photoUrl.isNullOrBlank()) {

                photoUrl

            } else {

                currentData?.photoUrl ?: ""
            }

        val updates =
            hashMapOf(

                "uid" to uid,

                "username" to username,

                "email" to (
                        currentUser.email ?: ""
                        ),

                "bio" to bio,

                "photoUrl" to finalPhotoUrl
            )

        firestore
            .collection("users")
            .document(uid)
            .set(
                updates,
                SetOptions.merge()
            )
            .await()

        val profileUpdates =
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .apply {

                    if (finalPhotoUrl.isNotBlank()) {

                        setPhotoUri(
                            Uri.parse(finalPhotoUrl)
                        )
                    }
                }
                .build()

        currentUser
            .updateProfile(profileUpdates)
            .await()

        syncUserPostSnapshots(
            uid = uid,
            username = username,
            photoUrl = finalPhotoUrl
        )
    }

    private suspend fun syncUserPostSnapshots(
        uid: String,
        username: String,
        photoUrl: String
    ) {
        val postsSnapshot =
            firestore
                .collection("posts")
                .whereEqualTo(
                    "userId",
                    uid
                )
                .get()
                .await()

        postsSnapshot.documents
            .chunked(450)
            .forEach { documents ->
                val batch =
                    firestore.batch()

                documents.forEach { document ->
                    batch.update(
                        document.reference,
                        mapOf(
                            "userName" to username,
                            "userPhoto" to photoUrl
                        )
                    )
                }

                batch
                    .commit()
                    .await()
            }
    }

    suspend fun uploadProfilePhoto(

        context: Context,
        imageUri: Uri

    ): String {

        val currentUser =
            auth.currentUser
                ?: throw Exception(
                    "Usuário não autenticado"
                )

        try {

            val inputStream =
                context.contentResolver
                    .openInputStream(imageUri)
                    ?: throw Exception(
                        "Erro ao abrir imagem"
                    )

            val bytes =
                inputStream.readBytes()

            inputStream.close()

            val fileName =
                "${currentUser.uid}_${UUID.randomUUID()}.jpg"

            val bucket =
                supabase.storage
                    .from("zerosekai-storage")

            bucket.upload(
                path = fileName,
                data = bytes
            )

            return bucket.publicUrl(fileName)

        } catch (e: Exception) {

            e.printStackTrace()

            throw Exception(
                e.message
                    ?: "Erro ao enviar imagem"
            )
        }
    }
}
