package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.zerosekai.R
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
import com.example.zerosekai.ui.theme.ZAccent
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

    var reactingMessageId by remember {
        mutableStateOf<String?>(null)
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
        modifier = Modifier.fillMaxSize(),
        backgroundRes = R.drawable.bg_chat
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ZSurface.copy(alpha = 0.62f),
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
                        subtitle = "Online agora",
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
                        showRing = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    NeonRoundIcon(
                        icon = Icons.Default.Call
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    NeonRoundIcon(
                        icon = Icons.Default.MoreHoriz
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
                            isMine = msg.senderId == currentUid,
                            showReactionPicker = reactingMessageId == msg.id,
                            onLongPress = {
                                reactingMessageId =
                                    if (reactingMessageId == msg.id) {
                                        null
                                    } else {
                                        msg.id
                                    }
                            },
                            onReact = { reaction ->
                                viewModel.reactToMessage(
                                    chatId = chatId,
                                    messageId = msg.id,
                                    userId = currentUid,
                                    reaction = reaction
                                )

                                reactingMessageId = null
                            }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                color = ZSurface.copy(alpha = 0.72f),
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
                    NeonRoundIcon(
                        icon = Icons.Default.AddPhotoAlternate
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    NeonRoundIcon(
                        icon = Icons.Default.Mic
                    )

                    Spacer(modifier = Modifier.width(8.dp))

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
                        containerColor = ZAccent,
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
private fun NeonRoundIcon(
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.size(46.dp),
        color = ZSurfaceElevated.copy(alpha = 0.64f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.68f))
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ZText,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: Message,
    isMine: Boolean,
    showReactionPicker: Boolean,
    onLongPress: () -> Unit,
    onReact: (String) -> Unit
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
        Column(
            horizontalAlignment = if (isMine) {
                Alignment.End
            } else {
                Alignment.Start
            }
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onLongPress
                    ),
                color = if (isMine) {
                    Color(0xFF7B22FF).copy(alpha = 0.88f)
                } else {
                    ZSurfaceElevated.copy(alpha = 0.74f)
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
                        color = ZText,
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
                                ZText.copy(alpha = 0.62f)
                            } else {
                                ZTextMuted
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            MessageReactionSummary(
                reactions = message.reactions,
                isMine = isMine
            )

            if (showReactionPicker) {
                MessageReactionPicker(
                    onReact = onReact
                )
            }
        }
    }
}

@Composable
private fun MessageReactionSummary(
    reactions: Map<String, String>,
    isMine: Boolean
) {
    val visibleReactions =
        reactions
            .values
            .filter {
                it.isNotBlank()
            }

    if (visibleReactions.isEmpty()) {
        return
    }

    val grouped =
        visibleReactions
            .groupingBy {
                it
            }
            .eachCount()
            .entries
            .joinToString(" ") { entry ->
                if (entry.value > 1) {
                    "${entry.key} ${entry.value}"
                } else {
                    entry.key
                }
            }

    Surface(
        modifier = Modifier.padding(top = 4.dp),
        color = if (isMine) {
            Color(0xFF7B22FF).copy(alpha = 0.32f)
        } else {
            ZSurfaceElevated.copy(alpha = 0.86f)
        },
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, ZBorder.copy(alpha = 0.58f))
    ) {
        Text(
            text = grouped,
            color = ZText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
        )
    }
}

@Composable
private fun MessageReactionPicker(
    onReact: (String) -> Unit
) {
    val reactions =
        listOf("❤️", "😂", "🔥", "😍", "👍")

    Spacer(modifier = Modifier.height(6.dp))

    Surface(
        color = ZSurface.copy(alpha = 0.98f),
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, ZBorder)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 4.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            reactions.forEach { reaction ->
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            onReact(reaction)
                        },
                    color = ZSurfaceElevated.copy(alpha = 0.72f),
                    shape = CircleShape
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reaction,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
