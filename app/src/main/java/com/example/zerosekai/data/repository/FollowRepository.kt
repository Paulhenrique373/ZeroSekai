package com.example.zerosekai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FollowRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun toggleFollow(
        targetUserId: String
    ) {

        try {

            val currentUser =
                auth.currentUser ?: return

            val currentUid =
                currentUser.uid

            if (currentUid == targetUserId) {
                return
            }

            val currentUserRef =
                firestore
                    .collection("users")
                    .document(currentUid)

            val targetUserRef =
                firestore
                    .collection("users")
                    .document(targetUserId)

            firestore.runTransaction { transaction ->

                val currentSnapshot =
                    transaction.get(currentUserRef)

                val targetSnapshot =
                    transaction.get(targetUserRef)

                val following =
                    currentSnapshot
                        .get("following")
                            as? List<String>
                        ?: emptyList()

                val followers =
                    targetSnapshot
                        .get("followers")
                            as? List<String>
                        ?: emptyList()

                val isFollowing =
                    following.contains(targetUserId)

                if (isFollowing) {

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

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    suspend fun isFollowing(
        targetUserId: String
    ): Boolean {

        return try {

            val currentUid =
                auth.currentUser?.uid
                    ?: return false

            val document =
                firestore
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()

            val following =
                document.get("following")
                        as? List<String>
                    ?: emptyList()

            following.contains(targetUserId)

        } catch (e: Exception) {

            false
        }
    }

    suspend fun getFollowersCount(
        userId: String
    ): Int {

        return try {

            val document =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            val followers =
                document.get("followers")
                        as? List<String>
                    ?: emptyList()

            followers.size

        } catch (e: Exception) {

            0
        }
    }

    suspend fun getFollowingCount(
        userId: String
    ): Int {

        return try {

            val document =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            val following =
                document.get("following")
                        as? List<String>
                    ?: emptyList()

            following.size

        } catch (e: Exception) {

            0
        }
    }
}