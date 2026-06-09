package com.example.zerosekai.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.data.model.User
import com.example.zerosekai.data.repository.FollowRepository
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroGradientButton
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroSoftButton
import com.example.zerosekai.ui.components.ZeroStatBlock
import com.example.zerosekai.ui.components.ZeroSubtleGradient
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userId: String
) {
    val repository = remember {
        FollowRepository()
    }

    val chatViewModel: ChatViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var user by remember {
        mutableStateOf<User?>(null)
    }

    var posts by remember {
        mutableStateOf<List<Post>>(emptyList())
    }

    var followersCount by remember {
        mutableStateOf(0)
    }

    var followingCount by remember {
        mutableStateOf(0)
    }

    var isFollowing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(userId) {
        try {
            val firestore =
                FirebaseFirestore.getInstance()

            val userDoc =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            user =
                userDoc.toObject(User::class.java)

            val postsSnapshot =
                firestore
                    .collection("posts")
                    .whereEqualTo(
                        "userId",
                        userId
                    )
                    .get()
                    .await()

            posts =
                postsSnapshot
                    .toObjects(Post::class.java)

            followersCount =
                repository.getFollowersCount(
                    userId
                )

            followingCount =
                repository.getFollowingCount(
                    userId
                )

            isFollowing =
                repository.isFollowing(
                    userId
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val username =
        user?.username?.ifBlank { "Usuario" } ?: "Usuario"

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                ZeroTopBar(
                    title = username,
                    subtitle = "Perfil publico",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                PublicProfileHeaderCard(
                    username = username,
                    bio = user?.bio.orEmpty(),
                    photoUrl = user?.photoUrl,
                    postsCount = posts.size,
                    followersCount = followersCount,
                    followingCount = followingCount,
                    isFollowing = isFollowing,
                    onFollowClick = {
                        scope.launch {
                            repository.toggleFollow(
                                userId
                            )

                            isFollowing =
                                !isFollowing

                            followersCount =
                                if (isFollowing) {
                                    followersCount + 1
                                } else {
                                    maxOf(
                                        0,
                                        followersCount - 1
                                    )
                                }
                        }
                    },
                    onMessageClick = onMessageClick@{
                        val currentUid =
                            FirebaseAuth
                                .getInstance()
                                .currentUser
                                ?.uid
                                ?: return@onMessageClick

                        chatViewModel.createChat(
                            currentUid = currentUid,
                            otherUid = userId
                        ) { chatId ->
                            navController.navigate(
                                "chat/$chatId"
                            )
                        }
                    }
                )
            }

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                ZeroSectionHeader(
                    title = "Posts",
                    subtitle = "${posts.size} publicacoes deste perfil"
                )
            }

            if (posts.isEmpty()) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    ZeroEmptyState(
                        icon = Icons.Default.GridOn,
                        title = "Nenhuma publicacao",
                        message = "As publicacoes deste perfil aparecerao aqui.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    )
                }
            } else {
                items(
                    items = posts,
                    key = { post -> post.id }
                ) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ZBorderSoft)
                            .clickable {
                                navController.navigate(
                                    "post_detail/${post.id}"
                                )
                            }
                    ) {
                        if (post.imageUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    post.imageUrl
                                ),
                                contentDescription = post.caption,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicProfileHeaderCard(
    username: String,
    bio: String,
    photoUrl: String?,
    postsCount: Int,
    followersCount: Int,
    followingCount: Int,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    ZeroElevatedPanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ZeroSubtleGradient)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZeroAvatar(
                        photoUrl = photoUrl,
                        size = 96.dp,
                        label = username
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ZeroStatBlock(
                                value = postsCount.toString(),
                                label = "posts",
                                modifier = Modifier.weight(1f)
                            )

                            ZeroStatBlock(
                                value = followersCount.toString(),
                                label = "seguidores",
                                modifier = Modifier.weight(1f)
                            )

                            ZeroStatBlock(
                                value = followingCount.toString(),
                                label = "seguindo",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = username,
                color = ZText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = bio.ifBlank { "Usuario do ZeroSekai" },
                color = ZTextMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZeroGradientButton(
                    text = if (isFollowing) {
                        "Seguindo"
                    } else {
                        "Seguir"
                    },
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.PersonAdd
                )

                ZeroSoftButton(
                    text = "Mensagem",
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.Chat
                )
            }
        }
    }
}
