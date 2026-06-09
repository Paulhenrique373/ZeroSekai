package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.zerosekai.data.model.Chat
import com.example.zerosekai.data.model.Message
import com.example.zerosekai.data.model.User
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.zeroTextFieldColors
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZChatMine
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    chatId: String,
    navController: NavHostController? = null,
    viewModel: ChatViewModel = viewModel()
) {
    var message by remember {
        mutableStateOf("")
    }

    var otherUser by remember {
        mutableStateOf<User?>(null)
    }

    val currentUid =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: ""

    val listState = rememberLazyListState()

    fun sendMessage() {
        if (message.isBlank()) {
            return
        }

        viewModel.sendMessage(
            chatId = chatId,
            senderId = currentUid,
            text = message.trim()
        )

        message = ""
    }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    LaunchedEffect(chatId, currentUid) {
        if (chatId.isBlank() || currentUid.isBlank()) {
            return@LaunchedEffect
        }

        try {
            val firestore =
                FirebaseFirestore.getInstance()

            val chat =
                firestore
                    .collection("chats")
                    .document(chatId)
                    .get()
                    .await()
                    .toObject(Chat::class.java)

            val otherUserId =
                chat
                    ?.participants
                    ?.firstOrNull {
                        it != currentUid
                    }
                    .orEmpty()

            if (otherUserId.isNotBlank()) {
                otherUser =
                    firestore
                        .collection("users")
                        .document(otherUserId)
                        .get()
                        .await()
                        .toObject(User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                viewModel.messages.lastIndex
            )
        }
    }

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ZSurface.copy(alpha = 0.96f),
                tonalElevation = 4.dp,
                border = BorderStroke(1.dp, ZBorder.copy(alpha = 0.72f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZeroTopBar(
                        title = otherUser?.username?.ifBlank { "Conversa" } ?: "Conversa",
                        subtitle = "Mensagens privadas",
                        onBackClick = navController?.let {
                            {
                                it.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ZeroAvatar(
                        photoUrl = otherUser?.photoUrl,
                        size = 42.dp,
                        label = otherUser?.username.orEmpty(),
                        showRing = false
                    )
                }
            }

            if (viewModel.messages.isEmpty()) {
                ZeroEmptyState(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "Nenhuma mensagem ainda",
                    message = "Envie a primeira mensagem para iniciar a conversa.",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 14.dp
                    )
                ) {
                    itemsIndexed(viewModel.messages) { _, msg ->
                        MessageBubble(
                            message = msg,
                            isMine = msg.senderId == currentUid
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                color = ZSurface.copy(alpha = 0.98f),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, ZBorder.copy(alpha = 0.72f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = {
                            message = it
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        placeholder = {
                            Text("Digite uma mensagem")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                sendMessage()
                            }
                        ),
                        colors = zeroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    FloatingActionButton(
                        onClick = {
                            sendMessage()
                        },
                        modifier = Modifier.size(52.dp),
                        containerColor = ZPrimary,
                        contentColor = ZText
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isMine: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            color = if (isMine) {
                ZChatMine
            } else {
                ZSurfaceElevated
            },
            shape = if (isMine) {
                RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp,
                    bottomEnd = 6.dp,
                    bottomStart = 22.dp
                )
            } else {
                RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp,
                    bottomEnd = 22.dp,
                    bottomStart = 6.dp
                )
            },
            border = if (isMine) {
                null
            } else {
                BorderStroke(1.dp, ZBorder)
            }
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 9.dp
                )
            ) {
                Text(
                    text = message.text,
                    color = if (isMine) {
                        Color.Black
                    } else {
                        ZText
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (message.timestamp > 0) {
                    Text(
                        text = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(
                            Date(message.timestamp)
                        ),
                        color = if (isMine) {
                            Color.Black.copy(alpha = 0.62f)
                        } else {
                            ZTextMuted
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
