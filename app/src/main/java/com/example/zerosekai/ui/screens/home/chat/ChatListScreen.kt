package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerosekai.R
import com.example.zerosekai.data.model.Chat
import com.example.zerosekai.data.model.User
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.ChatListLoadingSkeleton
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroShimmerBox
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSuccess
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.ChatListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onOpenChat: (String) -> Unit
) {
    val chats = viewModel.chats
    var initialLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (!uid.isNullOrEmpty()) {
            viewModel.loadChats(uid)
        }

        delay(700)
        initialLoading = false
    }

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize(),
        backgroundRes = R.drawable.bg_chat
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 92.dp)
                        .size(64.dp)
                        .clickable {
                            onNavigate("search")
                        },
                    color = ZPrimary.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.82f))
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "Nova conversa",
                            tint = ZText
                        )
                    }
                }
            },
            bottomBar = {
                BottomBar(
                    currentRoute = "chat_list",
                    onNavigate = onNavigate
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                ZeroTopBar(
                    title = "Conversas",
                    subtitle = "Mensagens privadas em tempo real"
                )

                if (chats.isEmpty() && initialLoading) {
                    ZeroSectionHeader(
                        title = "Conversas",
                        subtitle = "Sincronizando mensagens"
                    )

                    ChatListLoadingSkeleton(
                        itemCount = 5
                    )
                } else if (chats.isEmpty()) {
                    ZeroEmptyState(
                        icon = Icons.Default.Chat,
                        title = "Nenhuma conversa ainda",
                        message = "Quando você iniciar um chat, ele aparece aqui.",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            ZeroSectionHeader(
                                title = "Recentes",
                                subtitle = "${chats.size} conversas ativas"
                            )
                        }

                        items(
                            items = chats,
                            key = { chat -> chat.id }
                        ) { chat ->
                            ChatItem(
                                chat = chat,
                                onClick = {
                                    onOpenChat(chat.id)
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
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val currentUid =
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    val otherUserId =
        chat.participants.firstOrNull {
            it != currentUid
        }.orEmpty()

    var user by remember {
        mutableStateOf<User?>(null)
    }

    var userLoaded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(otherUserId) {
        if (otherUserId.isNotEmpty()) {
            userLoaded = false

            val snapshot = firestore
                .collection("users")
                .document(otherUserId)
                .get()
                .await()

            user = snapshot.toObject(User::class.java)
            userLoaded = true
        } else {
            userLoaded = true
        }
    }

    val time = remember(chat.lastTimestamp) {
        if (chat.lastTimestamp == 0L) {
            ""
        } else {
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(
                Date(chat.lastTimestamp)
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = ZCard.copy(alpha = 0.68f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp,
        border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.72f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                ZeroAvatar(
                    photoUrl = user?.photoUrl,
                    size = 62.dp,
                    label = user?.username.orEmpty()
                )

                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .align(Alignment.BottomEnd)
                        .background(ZCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(11.dp),
                        color = ZSuccess,
                        shape = CircleShape
                    ) {}
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (!userLoaded) {
                    ZeroShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.56f)
                            .height(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ZeroShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .height(12.dp)
                    )
                } else {
                    Text(
                        text = user?.username?.ifBlank {
                            "Conversa"
                        } ?: "Conversa",
                        color = ZText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (chat.lastMessage.isEmpty()) {
                            "Sem mensagens ainda"
                        } else {
                            chat.lastMessage
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ZTextMuted
                    )
                }
            }

            if (time.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = ZCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ZBorder)
                ) {
                    Text(
                        text = time,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = ZTextMuted
                    )
                }
            }
        }
    }
}
