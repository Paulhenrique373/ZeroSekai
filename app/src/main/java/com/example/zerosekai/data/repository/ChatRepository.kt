package com.example.zerosekai.data.repository

import com.example.zerosekai.data.model.Chat
import com.example.zerosekai.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class ChatRepository {


    private val firestore = FirebaseFirestore.getInstance()

    private var chatsListener: ListenerRegistration? = null
    private val messagesListeners = mutableMapOf<String, ListenerRegistration>()

    suspend fun createChat(
        currentUid: String,
        otherUid: String
    ): String {

        val chatId = listOf(currentUid, otherUid)
            .sorted()
            .joinToString("_")

        val chatRef = firestore
            .collection("chats")
            .document(chatId)

        val snapshot = chatRef.get().await()

        if (!snapshot.exists()) {

            val chat = Chat(
                id = chatId,
                participants = listOf(currentUid, otherUid)
            )

            chatRef.set(chat).await()
        }

        return chatId
    }

    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        text: String
    ) {

        val now = System.currentTimeMillis()

        val messageRef = firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        messageRef.set(
            Message(
                id = messageRef.id,
                senderId = senderId,
                text = text,
                timestamp = now
            )
        ).await()

        firestore.collection("chats")
            .document(chatId)
            .update(
                mapOf(
                    "lastMessage" to text,
                    "lastTimestamp" to now
                )
            )
            .await()
    }

    fun listenMessages(
        chatId: String,
        onMessagesChanged: (List<Message>) -> Unit
    ): ListenerRegistration {

        messagesListeners[chatId]?.remove()

        val listener = firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->

                val messages = snapshot
                    ?.toObjects(Message::class.java)
                    ?: emptyList()

                onMessagesChanged(messages)
            }

        messagesListeners[chatId] = listener

        return listener
    }

    fun listenChats(
        uid: String,
        onChatsChanged: (List<Chat>) -> Unit
    ): ListenerRegistration {

        chatsListener?.remove()
        chatsListener = null

        val listener = firestore
            .collection("chats")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, _ ->

                val chats = snapshot
                    ?.toObjects(Chat::class.java)
                    ?.sortedByDescending {
                        it.lastTimestamp
                    }
                    ?: emptyList()

                onChatsChanged(chats)
            }

        chatsListener = listener

        return listener
    }

    fun clearListeners() {

        chatsListener?.remove()
        chatsListener = null

        messagesListeners.values.forEach {
            it.remove()
        }

        messagesListeners.clear()
    }
}