package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerosekai.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    chatId: String,
    viewModel: ChatViewModel = viewModel()
) {

    var message by remember {
        mutableStateOf("")
    }

    val currentUid =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: ""

    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    LaunchedEffect(viewModel.messages.size) {

        if (viewModel.messages.isNotEmpty()) {

            listState.animateScrollToItem(
                viewModel.messages.lastIndex
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111111),
            tonalElevation = 4.dp
        ) {

            Text(
                text = "Conversa",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(18.dp)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 10.dp,
                vertical = 12.dp
            )
        ) {

            itemsIndexed(viewModel.messages) { _, msg ->

                val isMine =
                    msg.senderId == currentUid

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),

                    horizontalArrangement =
                    if (isMine)
                        Arrangement.End
                    else
                        Arrangement.Start
                ) {

                    Surface(

                        color =
                        if (isMine)
                            Color(0xFF25D366)
                        else
                            Color(0xFF1E1E1E),

                        shape =
                        if (isMine)
                            MaterialTheme.shapes.large
                        else
                            MaterialTheme.shapes.medium
                    ) {

                        Column(
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            )
                        ) {

                            Text(
                                text = msg.text,
                                color =
                                if (isMine)
                                    Color.Black
                                else
                                    Color.White
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp)
                            )

                            if (msg.timestamp > 0) {

                                Text(
                                    text = SimpleDateFormat(
                                        "HH:mm",
                                        Locale.getDefault()
                                    ).format(
                                        Date(msg.timestamp)
                                    ),

                                    color =
                                    if (isMine)
                                        Color.DarkGray
                                    else
                                        Color.Gray,

                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111111),
            tonalElevation = 6.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),

                verticalAlignment =
                Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = message,

                    onValueChange = {
                        message = it
                    },

                    modifier = Modifier.weight(1f),

                    shape =
                    MaterialTheme.shapes.extraLarge,

                    placeholder = {
                        Text("Digite uma mensagem")
                    },

                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                FloatingActionButton(

                    onClick = {

                        if (message.isBlank())
                            return@FloatingActionButton

                        viewModel.sendMessage(
                            chatId = chatId,
                            senderId = currentUid,
                            text = message.trim()
                        )

                        message = ""
                    }

                ) {

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                }
            }
        }
    }
}