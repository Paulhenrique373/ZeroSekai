package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import coil.compose.rememberAsyncImagePainter

import com.example.zerosekai.R

import com.example.zerosekai.data.model.Comment

import com.example.zerosekai.viewmodel.HomeViewModel

import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavHostController
) {

    val viewModel: HomeViewModel = viewModel()

    val posts by viewModel.posts.collectAsState()

    val currentUser =
        FirebaseAuth
            .getInstance()
            .currentUser

    val currentUid =
        currentUser?.uid ?: ""

    val stories = listOf(
        "Akira",
        "Yuki",
        "Sora",
        "Hinata",
        "Zero",
        "Kai"
    )

    Scaffold(

        bottomBar = {

            NavigationBar(
                containerColor = Color(0xFF050505)
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            null,
                            tint = Color.White
                        )
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = Color.White
                        )
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(
                            "create_post"
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Default.AddBox,
                            null,
                            tint = Color.White
                        )
                    }
                )

                // 🔥 PERFIL
                NavigationBarItem(
                    selected = false,
                    onClick = {

                        navController.navigate(
                            "profile"
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Color.White
                        )
                    }
                )
            }
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Color(0xFF050505)
                )
        ) {

            // TOP BAR
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    horizontalArrangement =
                    Arrangement.SpaceBetween,

                    verticalAlignment =
                    Alignment.CenterVertically
                ) {

                    Text(
                        text = "ZeroSekai",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {

                        Icon(
                            Icons.Default.Favorite,
                            null,
                            tint = Color.White
                        )

                        Spacer(
                            Modifier.width(12.dp)
                        )

                        Icon(
                            Icons.Default.Send,
                            null,
                            tint = Color.White
                        )
                    }
                }
            }

            // STORIES
            item {

                LazyRow {

                    items(stories) { story ->

                        Column(
                            horizontalAlignment =
                            Alignment.CenterHorizontally,

                            modifier =
                            Modifier.padding(8.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color.Magenta,
                                                Color.Cyan
                                            )
                                        )
                                    ),

                                contentAlignment =
                                Alignment.Center
                            ) {

                                Image(
                                    painter =
                                    painterResource(
                                        id = R.drawable.logo
                                    ),

                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(66.dp)
                                        .clip(CircleShape),

                                    contentScale =
                                    ContentScale.Crop
                                )
                            }

                            Spacer(
                                Modifier.height(6.dp)
                            )

                            Text(
                                text = story,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // POSTS
            items(posts) { post ->

                val isLiked =
                    post.likes.contains(currentUid)

                // 🔥 COMENTÁRIOS
                var commentText by remember {
                    mutableStateOf("")
                }

                var comments by remember {
                    mutableStateOf(
                        emptyList<Comment>()
                    )
                }

                LaunchedEffect(Unit) {

                    viewModel.getComments(
                        post.id
                    ) { list ->

                        comments = list
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),

                    shape =
                    RoundedCornerShape(0.dp),

                    colors =
                    CardDefaults.cardColors(
                        containerColor =
                        Color(0xFF050505)
                    )
                ) {

                    Column {

                        // HEADER
                        Row(
                            modifier =
                            Modifier.padding(12.dp),

                            verticalAlignment =
                            Alignment.CenterVertically
                        ) {

                            if (post.userPhoto.isNotBlank()) {

                                Image(
                                    painter =
                                    rememberAsyncImagePainter(
                                        post.userPhoto
                                    ),

                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),

                                    contentScale =
                                    ContentScale.Crop
                                )

                            } else {

                                Image(
                                    painter =
                                    painterResource(
                                        id = R.drawable.logo
                                    ),

                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),

                                    contentScale =
                                    ContentScale.Crop
                                )
                            }

                            Spacer(
                                Modifier.width(12.dp)
                            )

                            Column {

                                Text(
                                    text = post.userName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {

                                        navController.navigate(
                                            "user_profile/${post.userId}"
                                        )
                                    }
                                )

                                Text(
                                    text =
                                    "São Paulo, Brasil",

                                    color = Color.Gray,

                                    fontSize = 12.sp
                                )
                            }
                        }

                        // IMAGEM REAL
                        Image(
                            painter =
                            rememberAsyncImagePainter(
                                post.imageUrl
                            ),

                            contentDescription = null,

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(390.dp)
                                .clickable {
                                    navController.navigate(
                                        "post_detail/${post.id}"
                                    )
                                },

                            contentScale =
                            ContentScale.Crop
                        )

                        // ACTIONS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),

                            horizontalArrangement =
                            Arrangement.SpaceBetween
                        ) {

                            Row {

                                Icon(

                                    imageVector =

                                    if (isLiked)
                                        Icons.Default.Favorite
                                    else
                                        Icons.Default.FavoriteBorder,

                                    contentDescription = null,

                                    tint =

                                    if (isLiked)
                                        Color.Red
                                    else
                                        Color.White,

                                    modifier = Modifier.clickable {

                                        viewModel.toggleLike(
                                            post.id
                                        )
                                    }
                                )

                                Spacer(
                                    Modifier.width(14.dp)
                                )

                                Icon(
                                    Icons.Default.ChatBubbleOutline,
                                    null,
                                    tint = Color.White
                                )

                                Spacer(
                                    Modifier.width(14.dp)
                                )

                                Icon(
                                    Icons.Default.Send,
                                    null,
                                    tint = Color.White
                                )
                            }

                            Icon(
                                Icons.Default.BookmarkBorder,
                                null,
                                tint = Color.White
                            )
                        }

                        // CAPTION
                        Column(
                            modifier =
                            Modifier.padding(12.dp)
                        ) {

                            Text(
                                text =
                                "${post.likes.size} curtidas",

                                color = Color.White,

                                fontWeight =
                                FontWeight.Bold
                            )

                            Spacer(
                                Modifier.height(6.dp)
                            )

                            Text(
                                text =
                                "${post.userName} ${post.caption}",

                                color = Color.White
                            )

                            Spacer(
                                Modifier.height(12.dp)
                            )

                            // 🔥 LISTA COMENTÁRIOS
                            comments.forEach { comment ->

                                Text(
                                    text =
                                    "${comment.userName}: ${comment.text}",

                                    color = Color.White,

                                    fontSize = 13.sp
                                )

                                Spacer(
                                    Modifier.height(4.dp)
                                )
                            }

                            Spacer(
                                Modifier.height(12.dp)
                            )

                            // 🔥 CAMPO COMENTAR
                            OutlinedTextField(

                                value = commentText,

                                onValueChange = {
                                    commentText = it
                                },

                                placeholder = {
                                    Text("Comentar...")
                                },

                                modifier =
                                Modifier.fillMaxWidth(),

                                colors =
                                OutlinedTextFieldDefaults.colors(

                                    focusedTextColor =
                                    Color.White,

                                    unfocusedTextColor =
                                    Color.White,

                                    focusedBorderColor =
                                    Color.Magenta,

                                    unfocusedBorderColor =
                                    Color.Gray
                                )
                            )

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            // 🔥 BOTÃO COMENTAR
                            Button(

                                onClick = {

                                    if (
                                        commentText.isNotBlank()
                                    ) {

                                        viewModel.addComment(

                                            postId = post.id,

                                            text = commentText
                                        )

                                        commentText = ""
                                    }
                                },

                                modifier =
                                Modifier.fillMaxWidth()
                            ) {

                                Text("Comentar")
                            }
                        }
                    }
                }
            }
        }
    }
}
