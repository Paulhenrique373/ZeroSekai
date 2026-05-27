package com.example.zerosekai.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PostDetailScreen(
    navController: NavHostController,
    postId: String
) {

    val viewModel: HomeViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val currentUid =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val post =
        posts.firstOrNull {
            it.id == postId
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Text(
                text = "Publicacao",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (post == null) {
            Text(
                text = "Post nao encontrado.",
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            )
            return@Column
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.userPhoto.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(post.userPhoto),
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF9B9B9B),
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.size(12.dp))

            Text(
                text = post.userName,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            painter = rememberAsyncImagePainter(post.imageUrl),
            contentDescription = post.caption,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =
                    if (post.likes.contains(currentUid)) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = "Curtir",
                    tint =
                    if (post.likes.contains(currentUid)) {
                        Color(0xFFFF3040)
                    } else {
                        Color.White
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            viewModel.toggleLike(post.id)
                        }
                )
            }

            Icon(
                Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${post.likes.size} curtidas",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = post.caption,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}
