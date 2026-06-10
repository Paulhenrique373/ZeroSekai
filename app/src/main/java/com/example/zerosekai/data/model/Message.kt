package com.example.zerosekai.data.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val reactions: Map<String, String> = emptyMap()
)
