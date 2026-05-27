package com.example.zerosekai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.data.model.Post

import com.example.zerosekai.data.repository.PostRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _posts =
        MutableStateFlow<List<Post>>(emptyList())

    val posts: StateFlow<List<Post>> =
        _posts.asStateFlow()

    private val _loading =
        MutableStateFlow(false)

    val loading: StateFlow<Boolean> =
        _loading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    init {

        loadPosts()
    }

    fun loadPosts() {

        viewModelScope.launch {

            _loading.value = true

            _error.value = null

            try {

                repository.getPostsRealtime { list ->

                    _posts.value = list
                }

            } catch (e: Exception) {

                _error.value =
                    "Erro ao carregar posts"

            } finally {

                _loading.value = false
            }
        }
    }

    // 🔥 ATUALIZAR POSTS
    fun refreshPosts() {

        loadPosts()
    }

    // 🔥 CURTIR POST
    fun toggleLike(
        postId: String
    ) {

        viewModelScope.launch {

            try {

                repository.toggleLike(
                    postId
                )

            } catch (e: Exception) {

                _error.value =
                    "Erro ao curtir post"
            }
        }
    }

    // 🔥 ADICIONAR COMENTÁRIO
    fun addComment(
        postId: String,
        text: String
    ) {

        viewModelScope.launch {

            try {

                repository.addComment(
                    postId = postId,
                    text = text
                )

            } catch (e: Exception) {

                _error.value =
                    "Erro ao comentar"
            }
        }
    }

    // 🔥 PEGAR COMENTÁRIOS
    fun getComments(
        postId: String,
        onChange: (List<Comment>) -> Unit
    ) {

        repository.getCommentsRealtime(
            postId = postId,
            onChange = onChange
        )
    }
}