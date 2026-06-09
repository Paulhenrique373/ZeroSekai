package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.FeedLoadingSkeleton
import com.example.zerosekai.ui.components.PostCard
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val viewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val currentProfile by profileViewModel.user.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    profileViewModel.refreshUser()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUid =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: ""

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(
                    currentRoute = "home",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo("home") {
                                saveState = true
                            }
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 18.dp)
            ) {
                item {
                    ZeroTopBar(
                        title = "ZEROSEKAI",
                        subtitle = "Seu universo social anime",
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = ZTextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    navController.navigate("chat_list")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Mensagens",
                                    tint = ZText
                                )
                            }
                        }
                    )
                }

                if (posts.isEmpty() && loading) {
                    item {
                        FeedLoadingSkeleton(
                            modifier = Modifier
                                .padding(top = 4.dp),
                            itemCount = 2
                        )
                    }
                } else if (posts.isEmpty()) {
                    item {
                        ZeroEmptyState(
                            icon = Icons.Default.Home,
                            title = "Seu feed esta quieto",
                            message = "Quando novas publicacoes aparecerem, elas entram aqui com destaque.",
                            modifier = Modifier
                                .fillMaxSize()
                                .height(360.dp)
                        )
                    }
                } else {
                    items(
                        items = posts,
                        key = { post -> post.id }
                    ) { post ->
                        var commentText by remember(post.id) {
                            mutableStateOf("")
                        }

                        var comments by remember(post.id) {
                            mutableStateOf(emptyList<Comment>())
                        }

                        LaunchedEffect(post.id) {
                            viewModel.getComments(
                                post.id
                            ) { list ->
                                comments = list
                            }
                        }

                        val profileName =
                            currentProfile
                                ?.username
                                ?.takeIf {
                                    it.isNotBlank()
                                }

                        val profilePhoto =
                            currentProfile
                                ?.photoUrl
                                ?.takeIf {
                                    it.isNotBlank()
                                }

                        val displayPost =
                            if (post.userId == currentUid) {
                                post.copy(
                                    userName = profileName ?: post.userName,
                                    userPhoto = profilePhoto ?: post.userPhoto
                                )
                            } else {
                                post
                            }

                        val displayComments =
                            comments.map { comment ->
                                if (comment.userId == currentUid) {
                                    comment.copy(
                                        userName = profileName ?: comment.userName,
                                        userPhoto = profilePhoto ?: comment.userPhoto
                                    )
                                } else {
                                    comment
                                }
                            }

                        PostCard(
                            post = displayPost,
                            currentUid = currentUid,
                            comments = displayComments,
                            commentText = commentText,
                            onCommentTextChange = { newText ->
                                commentText = newText
                            },
                            onUserClick = {
                                navController.navigate(
                                    "user_profile/${post.userId}"
                                )
                            },
                            onPostClick = {
                                navController.navigate(
                                    "post_detail/${post.id}"
                                )
                            },
                            onToggleLike = {
                                viewModel.toggleLike(post.id)
                            },
                            onSubmitComment = {
                                if (commentText.isNotBlank()) {
                                    viewModel.addComment(
                                        postId = post.id,
                                        text = commentText.trim()
                                    )

                                    commentText = ""
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}
