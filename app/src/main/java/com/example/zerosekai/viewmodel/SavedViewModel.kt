package com.example.zerosekai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerosekai.data.repository.SavedRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {

    private val repository = SavedRepository()

    private var listener: ListenerRegistration? = null

    private val _savedPostIds =
        MutableStateFlow<Set<String>>(emptySet())

    val savedPostIds: StateFlow<Set<String>> =
        _savedPostIds.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    init {
        listenSavedPosts()
    }

    private fun listenSavedPosts() {

        listener?.remove()

        listener =
            repository.listenSavedPostIds(
                onChange = { ids ->
                    _savedPostIds.value = ids
                },
                onError = {
                    _error.value =
                        it.message ?: "Erro ao carregar posts salvos"
                }
            )
    }

    fun toggleSaved(
        postId: String
    ) {

        viewModelScope.launch {
            try {
                repository.toggleSaved(postId)
            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Erro ao salvar post"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        listener?.remove()
        listener = null
    }
}
