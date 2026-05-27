package com.example.zerosekai.data.model

data class Post(

    val id: String = "",

    val userId: String = "",

    val userName: String = "",

    val userPhoto: String = "",

    val imageUrl: String = "",

    val caption: String = "",

    val likes: List<String> = emptyList(),

    val timestamp: Long = System.currentTimeMillis()
)