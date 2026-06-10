package com.example.zerosekai.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerosekai.data.model.Message
import com.example.zerosekai.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    val messages = mutableStateListOf<Message>()

    fun createChat(
        currentUid: String,
        otherUid: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {

            val chatId = repository.createChat(
                currentUid,
                otherUid
            )

            onSuccess(chatId)
        }
    }

    fun sendMessage(
        chatId: String,
        senderId: String,
        text: String
    ) {
        viewModelScope.launch {

            if (text.isBlank()) return@launch

            repository.sendMessage(
                chatId = chatId,
                senderId = senderId,
                text = text
            )
        }
    }

    fun loadMessages(
        chatId: String
    ) {

        repository.listenMessages(
            chatId
        ) { list ->

            messages.clear()

            messages.addAll(
                list
            )
        }
    }

    fun reactToMessage(
        chatId: String,
        messageId: String,
        userId: String,
        reaction: String
    ) {

        viewModelScope.launch {
            repository.reactToMessage(
                chatId = chatId,
                messageId = messageId,
                userId = userId,
                reaction = reaction
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        repository.clearListeners()
    }
}
