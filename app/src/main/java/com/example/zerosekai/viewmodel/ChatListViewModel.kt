package com.example.zerosekai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.zerosekai.data.model.Chat
import com.example.zerosekai.data.repository.ChatRepository

class ChatListViewModel : ViewModel() {

    private val repository = ChatRepository()

    var chats by mutableStateOf<List<Chat>>(emptyList())
        private set

    private var currentUid: String? = null

    fun loadChats(uid: String) {

        // Evita criar listener duplicado para o mesmo usuário
        if (currentUid == uid) return

        currentUid = uid

        repository.listenChats(uid) { list ->

            chats = list.distinctBy {
                it.id
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        repository.clearListeners()
    }
}