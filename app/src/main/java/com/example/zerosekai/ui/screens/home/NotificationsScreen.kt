package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.zerosekai.data.model.Notification
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroShimmerBox
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    navController: NavHostController
) {
    val viewModel: NotificationViewModel = viewModel()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val loading by viewModel.loading.collectAsState()

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ZeroTopBar(
                title = "Notificacoes",
                subtitle = if (unreadCount > 0) {
                    "$unreadCount novas atividades"
                } else {
                    "Tudo em dia"
                },
                onBackClick = {
                    navController.popBackStack()
                },
                actions = {
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = {
                                viewModel.markAllAsRead()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Marcar tudo como lido",
                                tint = ZSecondary
                            )
                        }
                    }
                }
            )

            NotificationSummaryCard(
                total = notifications.size,
                unread = unreadCount
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                loading -> {
                    NotificationLoadingList()
                }

                notifications.isEmpty() -> {
                    ZeroEmptyState(
                        icon = Icons.Default.Notifications,
                        title = "Nada por aqui ainda",
                        message = "Curtidas, comentarios, seguidores e mensagens vao aparecer aqui.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            ZeroSectionHeader(
                                title = "Recentes",
                                subtitle = "Atividade em tempo real"
                            )
                        }

                        items(
                            items = notifications,
                            key = { notification -> notification.id }
                        ) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    viewModel.markAsRead(notification.id)
                                    openNotificationTarget(
                                        notification = notification,
                                        navController = navController
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSummaryCard(
    total: Int,
    unread: Int
) {
    ZeroElevatedPanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ZPrimary.copy(alpha = 0.26f),
                            ZSurfaceElevated.copy(alpha = 0.86f),
                            ZSecondary.copy(alpha = 0.18f)
                        )
                    )
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NotificationMetric(
                title = unread.toString(),
                subtitle = "nao lidas",
                tint = ZAccent,
                modifier = Modifier.weight(1f)
            )

            NotificationMetric(
                title = total.toString(),
                subtitle = "total",
                tint = ZSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NotificationMetric(
    title: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = tint,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = subtitle,
                color = ZTextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val visual =
        notificationVisual(notification.type)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (notification.read) {
            ZSurfaceElevated.copy(alpha = 0.76f)
        } else {
            visual.tint.copy(alpha = 0.18f)
        },
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 3.dp,
        border = BorderStroke(
            1.dp,
            if (notification.read) {
                ZBorder
            } else {
                visual.tint.copy(alpha = 0.56f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                ZeroAvatar(
                    photoUrl = notification.actorPhoto,
                    size = 56.dp,
                    label = notification.actorName
                )

                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd),
                    color = visual.tint,
                    shape = CircleShape
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.actorName.ifBlank {
                        "Zero User"
                    },
                    color = ZText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = notification.message.ifBlank {
                        visual.fallback
                    },
                    color = ZTextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formatNotificationTime(notification.createdAt),
                    color = ZTextMuted.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (!notification.read) {
                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier.size(10.dp),
                    color = visual.tint,
                    shape = CircleShape
                ) {}
            } else {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = ZTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationLoadingList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(4) {
            ZeroElevatedPanel(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZeroShimmerBox(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        ZeroShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(0.56f)
                                .height(15.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ZeroShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(0.86f)
                                .height(12.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class NotificationVisual(
    val icon: ImageVector,
    val tint: Color,
    val fallback: String
)

private fun notificationVisual(
    type: String
): NotificationVisual {
    return when (type) {
        "like" -> NotificationVisual(
            icon = Icons.Default.Favorite,
            tint = ZAccent,
            fallback = "curtiu seu post"
        )

        "comment" -> NotificationVisual(
            icon = Icons.Default.ChatBubbleOutline,
            tint = ZPrimary,
            fallback = "comentou no seu post"
        )

        "follow" -> NotificationVisual(
            icon = Icons.Default.PersonAdd,
            tint = ZSecondary,
            fallback = "comecou a seguir voce"
        )

        "message" -> NotificationVisual(
            icon = Icons.Default.Send,
            tint = ZSecondary,
            fallback = "enviou uma mensagem"
        )

        else -> NotificationVisual(
            icon = Icons.Default.Notifications,
            tint = ZPrimary,
            fallback = "interagiu com voce"
        )
    }
}

private fun openNotificationTarget(
    notification: Notification,
    navController: NavHostController
) {
    when (notification.type) {
        "like",
        "comment" -> {
            if (notification.postId.isNotBlank()) {
                navController.navigate("post_detail/${notification.postId}")
            }
        }

        "message" -> {
            if (notification.chatId.isNotBlank()) {
                navController.navigate("chat/${notification.chatId}")
            }
        }

        "follow" -> {
            if (notification.actorId.isNotBlank()) {
                navController.navigate("user_profile/${notification.actorId}")
            }
        }
    }
}

private fun formatNotificationTime(
    timestamp: Long
): String {
    if (timestamp <= 0L) {
        return "Agora"
    }

    val elapsed =
        System.currentTimeMillis() - timestamp

    return when {
        elapsed < 60_000L -> "Agora"
        elapsed < 3_600_000L -> "${elapsed / 60_000L} min"
        elapsed < 86_400_000L -> "${elapsed / 3_600_000L} h"
        else -> SimpleDateFormat(
            "dd/MM HH:mm",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        )
    }
}
