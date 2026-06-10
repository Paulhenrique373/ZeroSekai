package com.example.zerosekai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerosekai.data.model.Notification
import com.example.zerosekai.data.repository.NotificationRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private var listener: ListenerRegistration? = null

    private val _notifications =
        MutableStateFlow<List<Notification>>(emptyList())

    val notifications: StateFlow<List<Notification>> =
        _notifications.asStateFlow()

    private val _unreadCount =
        MutableStateFlow(0)

    val unreadCount: StateFlow<Int> =
        _unreadCount.asStateFlow()

    private val _loading =
        MutableStateFlow(true)

    val loading: StateFlow<Boolean> =
        _loading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    init {
        listenNotifications()
    }

    private fun listenNotifications() {

        listener?.remove()

        _loading.value = true

        listener =
            repository.listenNotifications(
                onChange = { list ->
                    _notifications.value = list
                    _unreadCount.value =
                        list.count {
                            !it.read
                        }
                    _loading.value = false
                },
                onError = {
                    _error.value =
                        it.message ?: "Erro ao carregar notificacoes"
                    _loading.value = false
                }
            )

        if (listener == null) {
            _loading.value = false
        }
    }

    fun markAsRead(
        notificationId: String
    ) {

        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Erro ao marcar notificacao"
            }
        }
    }

    fun markAllAsRead() {

        viewModelScope.launch {
            try {
                repository.markAllAsRead()
            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Erro ao marcar notificacoes"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        listener?.remove()
        listener = null
    }
}
