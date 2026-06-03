package com.example.zerosekai.data.model

data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)