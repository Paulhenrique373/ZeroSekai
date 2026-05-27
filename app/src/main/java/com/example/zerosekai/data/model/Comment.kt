package com.example.zerosekai.data.model

data class Comment(

    val id: String = "",

    val postId: String = "",

    val userId: String = "",

    val userName: String = "",

    val userPhoto: String = "",

    val text: String = "",

    val timestamp: Long = System.currentTimeMillis()
)