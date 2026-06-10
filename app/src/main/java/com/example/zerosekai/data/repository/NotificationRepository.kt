package com.example.zerosekai.data.repository

import com.example.zerosekai.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun listenNotifications(
        onChange: (List<Notification>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration? {

        val uid =
            auth.currentUser?.uid ?: return null

        return firestore
            .collection("notifications")
            .whereEqualTo("recipientId", uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val notifications =
                    snapshot
                        ?.toObjects(Notification::class.java)
                        ?.sortedByDescending {
                            it.createdAt
                        }
                        ?: emptyList()

                onChange(notifications)
            }
    }

    suspend fun createNotification(
        recipientId: String,
        type: String,
        message: String,
        postId: String = "",
        chatId: String = "",
        stableId: String? = null
    ) {

        val actorId =
            auth.currentUser?.uid ?: return

        if (
            recipientId.isBlank() ||
            recipientId == actorId
        ) {
            return
        }

        val actorSnapshot =
            firestore
                .collection("users")
                .document(actorId)
                .get()
                .await()

        val actorName =
            actorSnapshot.getString("username")
                ?: auth.currentUser?.email
                ?: "Zero User"

        val actorPhoto =
            actorSnapshot.getString("photoUrl")
                ?: ""

        val notificationId =
            stableId
                ?: firestore
                    .collection("notifications")
                    .document()
                    .id

        val notification =
            Notification(
                id = notificationId,
                recipientId = recipientId,
                actorId = actorId,
                actorName = actorName,
                actorPhoto = actorPhoto,
                type = type,
                postId = postId,
                chatId = chatId,
                message = message,
                read = false,
                createdAt = System.currentTimeMillis()
            )

        firestore
            .collection("notifications")
            .document(notificationId)
            .set(notification)
            .await()
    }

    suspend fun markAsRead(
        notificationId: String
    ) {

        if (notificationId.isBlank()) {
            return
        }

        firestore
            .collection("notifications")
            .document(notificationId)
            .update("read", true)
            .await()
    }

    suspend fun markAllAsRead() {

        val uid =
            auth.currentUser?.uid ?: return

        val snapshot =
            firestore
                .collection("notifications")
                .whereEqualTo("recipientId", uid)
                .whereEqualTo("read", false)
                .get()
                .await()

        val batch =
            firestore.batch()

        snapshot.documents.forEach { document ->
            batch.update(
                document.reference,
                "read",
                true
            )
        }

        batch.commit().await()
    }
}
