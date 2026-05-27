package com.example.zerosekai.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private val ZeroBackground = Color(0xFF050505)
private val ZeroSurface = Color(0xFF111111)
private val ZeroMuted = Color(0xFF9B9B9B)

@Composable
fun SearchScreen(
    navController: NavHostController
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var users by remember {
        mutableStateOf<List<User>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(searchText) {

        if (searchText.isBlank()) {

            users = emptyList()
            return@LaunchedEffect
        }

        loading = true

        try {

            val snapshot =
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .get()
                    .await()

            users =
                snapshot
                    .toObjects(User::class.java)
                    .filter {

                        it.username.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }

        } catch (e: Exception) {

            e.printStackTrace()

        } finally {

            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
    ) {

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Pesquisar",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 20.dp
            )
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            placeholder = {
                Text(
                    text = "Pesquisar usuários...",
                    color = ZeroMuted
                )
            },
            leadingIcon = {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ZeroMuted
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color(0xFF333333),
                cursorColor = Color.White
            )
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (loading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Carregando...",
                    color = Color.White
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(users) { user ->

                    UserItem(
                        user = user,
                        onClick = {

                            navController.navigate(
                                "user_profile/${user.uid}"
                            )
                        }
                    )

                    HorizontalDivider(
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = ZeroBackground
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!user.photoUrl.isNullOrBlank()) {

                Image(
                    painter =
                    rememberAsyncImagePainter(
                        user.photoUrl
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(ZeroSurface),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = ZeroMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = user.username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = user.bio,
                    color = ZeroMuted,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}