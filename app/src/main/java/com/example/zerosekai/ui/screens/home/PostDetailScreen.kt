package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.zeroTextFieldColors
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PostDetailScreen(
    navController: NavHostController,
    postId: String
) {
    val viewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val currentProfile by profileViewModel.user.collectAsState()
    val currentUid =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val post =
        posts.firstOrNull {
            it.id == postId
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
        if (post?.userId == currentUid) {
            post.copy(
                userName = profileName ?: post.userName,
                userPhoto = profilePhoto ?: post.userPhoto
            )
        } else {
            post
        }

    var commentText by remember(postId) {
        mutableStateOf("")
    }

    var comments by remember(postId) {
        mutableStateOf(emptyList<Comment>())
    }

    LaunchedEffect(postId) {
        if (postId.isNotBlank()) {
            viewModel.getComments(
                postId
            ) { list ->
                comments = list
            }
        }
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

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            ZeroTopBar(
                title = "Publicação",
                subtitle = "Detalhes do post",
                onBackClick = {
                    navController.popBackStack()
                }
            )

            if (displayPost == null) {
                ZeroEmptyState(
                    icon = Icons.Default.ImageNotSupported,
                    title = "Post não encontrado",
                    message = "Esta publicação não está mais disponível.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                )

                return@Column
            }

            ZeroElevatedPanel(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ZeroAvatar(
                            photoUrl = displayPost.userPhoto,
                            size = 46.dp,
                            label = displayPost.userName
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = displayPost.userName.ifBlank { "Zero User" },
                                color = ZText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "ZeroSekai",
                                color = ZTextMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    AsyncImage(
                        model = displayPost.imageUrl,
                        contentDescription = displayPost.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 5f),
                        contentScale = ContentScale.Crop
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            IconButton(
                                onClick = {
                                    viewModel.toggleLike(displayPost.id)
                                }
                            ) {
                                Icon(
                                    imageVector = if (displayPost.likes.contains(currentUid)) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Curtir",
                                    tint = if (displayPost.likes.contains(currentUid)) {
                                        ZAccent
                                    } else {
                                        ZText
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = ZText,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = ZText,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                navController.navigate("user_profile/${displayPost.userId}")
                            },
                        color = ZCard.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "${displayPost.likes.size} curtidas",
                                color = ZText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            color = ZText,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(displayPost.userName.ifBlank { "Zero User" })
                                    }

                                    append(" ")
                                    append(displayPost.caption)
                                },
                                color = ZText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        ZeroSectionHeader(
                            title = "Comentarios",
                            subtitle = "${displayComments.size} respostas"
                        )

                        if (displayComments.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = ZSurfaceElevated.copy(alpha = 0.64f),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ZBorder)
                            ) {
                                Text(
                                    text = "Seja a primeira pessoa a comentar.",
                                    color = ZTextMuted,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        } else {
                            displayComments.forEach { comment ->
                                CommentRow(
                                    comment = comment
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { newText ->
                                commentText = newText
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Adicionar comentario...")
                            },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (commentText.isNotBlank()) {
                                            viewModel.addComment(
                                                postId = displayPost.id,
                                                text = commentText.trim()
                                            )

                                            commentText = ""
                                        }
                                    },
                                    enabled = commentText.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Enviar comentario",
                                        tint = if (commentText.isNotBlank()) {
                                            ZPrimary
                                        } else {
                                            ZTextMuted
                                        }
                                    )
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = zeroTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: Comment
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        ZeroAvatar(
            photoUrl = comment.userPhoto,
            size = 40.dp,
            label = comment.userName,
            showRing = false
        )

        Spacer(modifier = Modifier.width(10.dp))

        Surface(
            modifier = Modifier.weight(1f),
            color = ZSurfaceElevated.copy(alpha = 0.72f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZBorder)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 9.dp
                )
            ) {
                Text(
                    text = comment.userName.ifBlank { "Zero User" },
                    color = ZText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = comment.text,
                    color = ZTextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
