package com.example.zerosekai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FollowRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun toggleFollow(targetUserId: String) {

        val currentUser =
            auth.currentUser ?: return

        val currentUid =
            currentUser.uid

        if (currentUid == targetUserId) return

        val currentUserRef =
            firestore.collection("users")
                .document(currentUid)

        val targetUserRef =
            firestore.collection("users")
                .document(targetUserId)

        firestore.runTransaction { transaction ->

            val currentSnapshot =
                transaction.get(currentUserRef)

            val targetSnapshot =
                transaction.get(targetUserRef)

            val following =
                currentSnapshot.get("following")
                        as? List<String>
                    ?: emptyList()

            val followers =
                targetSnapshot.get("followers")
                        as? List<String>
                    ?: emptyList()

            if (following.contains(targetUserId)) {

                transaction.update(
                    currentUserRef,
                    "following",
                    following - targetUserId
                )

                transaction.update(
                    targetUserRef,
                    "followers",
                    followers - currentUid
                )

            } else {

                transaction.update(
                    currentUserRef,
                    "following",
                    following + targetUserId
                )

                transaction.update(
                    targetUserRef,
                    "followers",
                    followers + currentUid
                )
            }
        }.await()
    }
}