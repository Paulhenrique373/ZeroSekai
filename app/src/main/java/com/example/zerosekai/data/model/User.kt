package com.example.zerosekai.data.model

data class User(

    val uid: String = "",

    val username: String = "",

    val email: String = "",

    val bio: String = "",

    val photoUrl: String = "",

    val followers: List<String> = emptyList(),

    val following: List<String> = emptyList()
)
