package com.example.zerosekai.data.model

data class Notification(
    val id: String = "",
    val recipientId: String = "",
    val actorId: String = "",
    val actorName: String = "",
    val actorPhoto: String = "",
    val type: String = "",
    val postId: String = "",
    val chatId: String = "",
    val message: String = "",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
