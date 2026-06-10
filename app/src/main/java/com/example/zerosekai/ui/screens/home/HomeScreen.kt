package com.example.zerosekai.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.R
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.FeedLoadingSkeleton
import com.example.zerosekai.ui.components.PostCard
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroGlassCard
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.ZeroPinkGradient
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.NotificationViewModel
import com.example.zerosekai.viewmodel.ProfileViewModel
import com.example.zerosekai.viewmodel.SavedViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val viewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val savedViewModel: SavedViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val currentProfile by profileViewModel.user.collectAsState()
    val savedPostIds by savedViewModel.savedPostIds.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

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
        modifier = Modifier.fillMaxSize(),
        backgroundRes = R.drawable.bg_home
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 92.dp)
                        .size(64.dp)
                        .background(
                            brush = ZeroPinkGradient,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .clickable {
                            navController.navigate("create_post")
                        },
                    color = Color.Transparent,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.72f))
                ) {
                    androidx.compose.foundation.layout.Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Criar post",
                            tint = ZText
                        )
                    }
                }
            },
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
                            IconButton(
                                onClick = {
                                    navController.navigate("notifications")
                                }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge {
                                                Text(
                                                    text = unreadCount
                                                        .coerceAtMost(99)
                                                        .toString()
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = ZTextMuted
                                    )
                                }
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

                item {
                    HomeSpotlightCard(
                        username = currentProfile?.username,
                        postCount = posts.size,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
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
                            },
                            onSharePost = {
                                val shareIntent =
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${displayPost.userName}: ${displayPost.caption}\n${displayPost.imageUrl}"
                                        )
                                    }

                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Compartilhar post"
                                    )
                                )
                            },
                            isSaved = savedPostIds.contains(post.id),
                            onToggleSaved = {
                                savedViewModel.toggleSaved(post.id)
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

@Composable
private fun HomeSpotlightCard(
    username: String?,
    postCount: Int,
    modifier: Modifier = Modifier
) {
    ZeroGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ZPrimary.copy(alpha = 0.26f),
                            ZSurfaceElevated.copy(alpha = 0.72f),
                            ZSecondary.copy(alpha = 0.16f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Feed premium",
                color = ZAccent,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bem-vindo, ${username?.ifBlank { "Zero User" } ?: "Zero User"}",
                color = ZText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Acompanhe posts, curtidas e conversas do seu universo anime.",
                color = ZTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeInfoPill(
                    text = "$postCount posts"
                )

                HomeInfoPill(
                    text = "Tempo real"
                )

                HomeInfoPill(
                    text = "Dark anime"
                )
            }
        }
    }
}

@Composable
private fun HomeInfoPill(
    text: String
) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = ZText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}
