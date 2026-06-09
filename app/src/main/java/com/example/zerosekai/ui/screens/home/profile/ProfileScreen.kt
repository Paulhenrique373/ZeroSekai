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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroGradientButton
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroStatBlock
import com.example.zerosekai.ui.components.ZeroSubtleGradient
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val homeViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val posts by homeViewModel.posts.collectAsState()
    val user by profileViewModel.user.collectAsState()

    val currentUid =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val userPosts =
        posts.filter {
            it.userId == currentUid
        }

    val totalLikes =
        userPosts.sumOf {
            it.likes.size
        }

    val username =
        user?.username?.ifBlank { "Zero User" } ?: "Zero User"

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(
                    currentRoute = "profile",
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 18.dp),
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
                        subtitle = "Perfil",
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
                    ProfileHeaderCard(
                        username = username,
                        email = user?.email.orEmpty(),
                        bio = user?.bio.orEmpty(),
                        photoUrl = user?.photoUrl,
                        postsCount = userPosts.size,
                        likesCount = totalLikes,
                        followersCount = user?.followers?.size ?: 0,
                        followingCount = user?.following?.size ?: 0,
                        onEditClick = {
                            navController.navigate("edit_profile")
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
                        subtitle = "${userPosts.size} publicacoes no perfil"
                    )
                }

                if (userPosts.isEmpty()) {
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        ZeroEmptyState(
                            icon = Icons.Default.GridOn,
                            title = "Nenhuma publicacao ainda",
                            message = "Quando voce postar uma imagem, ela aparece aqui.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        )
                    }
                } else {
                    items(
                        items = userPosts,
                        key = { post -> post.id }
                    ) { post ->
                        ProfilePostTile(
                            post = post,
                            onClick = {
                                navController.navigate("post_detail/${post.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    username: String,
    email: String,
    bio: String,
    photoUrl: String?,
    postsCount: Int,
    likesCount: Int,
    followersCount: Int,
    followingCount: Int,
    onEditClick: () -> Unit
) {
    ZeroElevatedPanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ZeroSubtleGradient)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ZeroAvatar(
                        photoUrl = photoUrl,
                        size = 118.dp,
                        label = username
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = username,
                        color = ZText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    if (email.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = email,
                            color = ZTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = bio.ifBlank { "Usuario oficial do ZeroSekai" },
                color = ZText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ZSurfaceElevated.copy(alpha = 0.72f))
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ZeroStatBlock(
                    value = postsCount.toString(),
                    label = "posts",
                    modifier = Modifier.weight(1f)
                )

                ZeroStatBlock(
                    value = likesCount.toString(),
                    label = "curtidas",
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

            Spacer(modifier = Modifier.height(16.dp))

            ZeroGradientButton(
                text = "Editar perfil",
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit
            )
        }
    }
}

@Composable
private fun ProfilePostTile(
    post: Post,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(ZBorderSoft)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(post.imageUrl),
            contentDescription = post.caption,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
