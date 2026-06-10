package com.example.zerosekai.data.repository

import com.example.zerosekai.data.model.SavedPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class SavedRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun listenSavedPostIds(
        onChange: (Set<String>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration? {

        val uid =
            auth.currentUser?.uid ?: return null

        return firestore
            .collection("saved_posts")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val postIds =
                    snapshot
                        ?.toObjects(SavedPost::class.java)
                        ?.map {
                            it.postId
                        }
                        ?.toSet()
                        ?: emptySet()

                onChange(postIds)
            }
    }

    suspend fun toggleSaved(
        postId: String
    ) {

        val uid =
            auth.currentUser?.uid ?: return

        if (postId.isBlank()) {
            return
        }

        val savedId =
            "${uid}_$postId"

        val savedRef =
            firestore
                .collection("saved_posts")
                .document(savedId)

        val snapshot =
            savedRef
                .get()
                .await()

        if (snapshot.exists()) {
            savedRef
                .delete()
                .await()
        } else {
            savedRef
                .set(
                    SavedPost(
                        id = savedId,
                        userId = uid,
                        postId = postId,
                        savedAt = System.currentTimeMillis()
                    )
                )
                .await()
        }
    }
}
