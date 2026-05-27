package com.example.zerosekai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerosekai.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun uploadPost(
        context: Context,
        imageUri: Uri,
        caption: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val result = repository.uploadPost(
                context = context,
                imageUri = imageUri,
                caption = caption
            )

            _isLoading.value = false

            result.onSuccess {
                onSuccess()
            }

            result.onFailure {
                onError(it.message ?: "Erro ao publicar")
            }
        }
    }
}