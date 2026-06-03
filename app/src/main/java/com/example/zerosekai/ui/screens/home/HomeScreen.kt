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

import coil.compose.AsyncImage

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

        containerColor = Color(0xFF050505),

        bottomBar = {

            NavigationBar(
                containerColor = Color(0xFF050505)
            ) {

                // HOME
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                // SEARCH
                NavigationBarItem(
                    selected = false,
                    onClick = {

                        navController.navigate(
                            "search"
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                // CREATE POST
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
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                // PROFILE
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
                            contentDescription = null,
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
                .background(Color(0xFF050505))
        ) {

            // TOP BAR
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),

                    horizontalArrangement =
                    Arrangement.SpaceBetween,

                    verticalAlignment =
                    Alignment.CenterVertically
                ) {

                    Text(
                        text = "ZEROSEKAI",
                        fontSize = 30.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {

                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        IconButton(
                            onClick = {

                                navController.navigate(
                                    "chat_list"
                                )
                            }
                        ) {

                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Mensagens",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // STORIES
            item {

                LazyRow(
                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                ) {

                    items(stories) { story ->

                        Column(
                            horizontalAlignment =
                            Alignment.CenterHorizontally,

                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(74.dp)
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
                                        .size(68.dp)
                                        .clip(CircleShape),

                                    contentScale =
                                    ContentScale.Crop
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(6.dp)
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

                var commentText by remember {
                    mutableStateOf("")
                }

                var comments by remember {
                    mutableStateOf(
                        emptyList<Comment>()
                    )
                }

                LaunchedEffect(post.id) {

                    viewModel.getComments(
                        post.id
                    ) { list ->

                        comments = list
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        ),

                    shape = RoundedCornerShape(18.dp),

                    colors =
                    CardDefaults.cardColors(
                        containerColor =
                        Color(0xFF050505)
                    )
                ) {

                    Column {

                        // HEADER
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),

                            verticalAlignment =
                            Alignment.CenterVertically
                        ) {

                            if (post.userPhoto.isNotBlank()) {

                                AsyncImage(
                                    model = post.userPhoto,
                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),

                                    contentScale = ContentScale.Crop
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
                                modifier = Modifier.width(12.dp)
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
                                    text = "São Paulo, Brasil",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // POST IMAGE
                        AsyncImage(
                            model = post.imageUrl,

                            contentDescription = null,

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(390.dp)
                                .clickable {

                                    navController.navigate(
                                        "post_detail/${post.id}"
                                    )
                                },

                            contentScale = ContentScale.Crop
                        )

                        // ACTIONS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 14.dp
                                ),

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
                                    modifier = Modifier.width(16.dp)
                                )

                                Icon(
                                    Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = Color.White
                                )

                                Spacer(
                                    modifier = Modifier.width(16.dp)
                                )

                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        // CAPTION + COMMENTS
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                        ) {

                            Text(
                                text =
                                "${post.likes.size} curtidas",

                                color = Color.White,

                                fontWeight =
                                FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                "${post.userName} ${post.caption}",

                                color = Color.White
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            // COMMENTS
                            comments.take(3).forEach { comment ->

                                Text(
                                    text =
                                    "${comment.userName}: ${comment.text}",

                                    color = Color.White,

                                    fontSize = 13.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(5.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            // COMMENT FIELD
                            // COMMENT FIELD
                            OutlinedTextField(

                                value = commentText,

                                onValueChange = {
                                    commentText = it
                                },

                                shape = RoundedCornerShape(20.dp),

                                singleLine = true,

                                placeholder = {

                                    Text(
                                        "Comentar..."
                                    )
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
                                    Color.Gray,

                                    focusedContainerColor =
                                    Color(0xFF111111),

                                    unfocusedContainerColor =
                                    Color(0xFF111111)
                                )
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            // COMMENT BUTTON
                            Button(

                                onClick = {

                                    if (
                                        commentText.isNotBlank()
                                    ) {

                                        viewModel.addComment(

                                            postId = post.id,

                                            text =
                                            commentText.trim()
                                        )

                                        commentText = ""
                                    }
                                },

                                modifier =
                                Modifier.fillMaxWidth(),

                                colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                    Color.White
                                )
                            ) {

                                Text(
                                    text = "Comentar",
                                    color = Color.Black
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}