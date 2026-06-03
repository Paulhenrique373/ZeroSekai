package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zerosekai.data.model.Chat
import com.example.zerosekai.data.model.User
import com.example.zerosekai.viewmodel.ChatListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = viewModel(),
    onOpenChat: (String) -> Unit
) {

    val chats = viewModel.chats

    LaunchedEffect(Unit) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (!uid.isNullOrEmpty()) {
            viewModel.loadChats(uid)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Conversas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 20.dp,
                bottom = 12.dp
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        ) {

            items(chats) { chat ->

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

@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit
) {

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val otherUserId = chat.participants.firstOrNull().orEmpty()

    var user by remember {
        mutableStateOf<User?>(null)
    }

    LaunchedEffect(otherUserId) {

        if (otherUserId.isNotEmpty()) {

            val snapshot = firestore
                .collection("users")
                .document(otherUserId)
                .get()
                .await()

            user = snapshot.toObject(User::class.java)
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {

                AsyncImage(
                    model = user?.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = user?.username ?: "Carregando...",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (
                        chat.lastMessage.isEmpty()
                    ) {
                        "Sem mensagens ainda"
                    } else {
                        chat.lastMessage
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}