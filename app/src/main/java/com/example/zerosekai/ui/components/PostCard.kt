package com.example.zerosekai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostCard(
    post: Post,
    currentUid: String,
    comments: List<Comment>,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onUserClick: () -> Unit,
    onPostClick: () -> Unit,
    onToggleLike: () -> Unit,
    onSubmitComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiked = post.likes.contains(currentUid)
    val likeTint by animateColorAsState(
        targetValue = if (isLiked) ZAccent else ZText,
        label = "postLikeTint"
    )

    ZeroElevatedPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZeroAvatar(
                    photoUrl = post.userPhoto,
                    size = 46.dp,
                    label = post.userName,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onUserClick)
                ) {
                    Text(
                        text = post.userName.ifBlank { "Zero User" },
                        color = ZText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = formatPostTimestamp(post.timestamp),
                        color = ZTextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = ZTextMuted
                )
            }

            AsyncImage(
                model = post.imageUrl,
                contentDescription = post.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .aspectRatio(4f / 5f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onPostClick),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleLike
                    ) {
                        Icon(
                            imageVector = if (isLiked) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Curtir",
                            tint = likeTint,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = onPostClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comentarios",
                            tint = ZText,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = ZText,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = ZText,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Text(
                    text = "${post.likes.size} curtidas",
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
                            append(post.userName.ifBlank { "Zero User" })
                        }

                        append(" ")
                        append(post.caption)
                    },
                    color = ZText,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    comments.take(3).forEach { comment ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = ZText,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(comment.userName.ifBlank { "Zero User" })
                                }

                                append(": ")
                                append(comment.text)
                            },
                            color = ZTextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (comments.size > 3) {
                        Text(
                            text = "Ver todos os ${comments.size} comentarios",
                            color = ZTextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable(onClick = onPostClick)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    color = ZSurface.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 0.dp
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = onCommentTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Comentar...")
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = onSubmitComment,
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

private fun formatPostTimestamp(timestamp: Long): String {
    if (timestamp <= 0L) {
        return "ZeroSekai"
    }

    val elapsed = System.currentTimeMillis() - timestamp

    return when {
        elapsed < 60_000L -> "Agora"
        elapsed < 3_600_000L -> "${elapsed / 60_000L} min"
        elapsed < 86_400_000L -> "${elapsed / 3_600_000L} h"
        else -> SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        )
    }
}
