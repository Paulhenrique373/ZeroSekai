package com.example.zerosekai.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.viewmodel.HomeViewModel
import com.example.zerosekai.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

private val ZeroBackground = Color(0xFF050505)
private val ZeroSurface = Color(0xFF111111)
private val ZeroStroke = Color(0xFF252525)
private val ZeroTextMuted = Color(0xFF9B9B9B)
private val ZeroAccent = Color(0xFFEDEDED)

@Composable
fun ProfileScreen(
    navController: NavHostController
) {

    val homeViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val posts by homeViewModel.posts.collectAsState()
    val user by profileViewModel.user.collectAsState()

    val currentUid =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val userPosts =
        posts.filter {
            it.userId == currentUid
        }

    val totalLikes =
        userPosts.sumOf {
            it.likes.size
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
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
                text = user?.username?.ifBlank { "Zero User" } ?: "Zero User",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileAvatar(
                photoUrl = user?.photoUrl.orEmpty(),
                size = 116
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = user?.username?.ifBlank { "Zero User" } ?: "Zero User",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (!user?.email.isNullOrBlank()) {

                Spacer(Modifier.height(4.dp))

                Text(
                    text = user?.email.orEmpty(),
                    color = ZeroTextMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = user?.bio?.ifBlank { "Usuario oficial do ZeroSekai" }
                    ?: "Usuario oficial do ZeroSekai",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZeroSurface)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    value = userPosts.size.toString(),
                    label = "posts"
                )

                ProfileStat(
                    value = user?.followers?.size?.toString() ?: "0",
                    label = "seguidores"
                )

                ProfileStat(
                    value = user?.following?.size?.toString() ?: "0",
                    label = "seguindo"
                )
            }

            Spacer(Modifier.height(14.dp))

            ElevatedButton(
                onClick = {
                    navController.navigate("edit_profile")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ZeroAccent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Editar perfil",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


        if (userPosts.isEmpty()) {

            EmptyPosts()

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(userPosts) { post ->
                    ProfilePostTile(
                        post = post,
                        onClick = {
                            navController.navigate("post_detail/${post.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String,
    size: Int
) {
    if (photoUrl.isNotBlank()) {
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(ZeroSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = ZeroTextMuted,
                modifier = Modifier.size((size / 2).dp)
            )
        }
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )

        Text(
            text = label,
            color = ZeroTextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ProfilePostTile(
    post: Post,
    onClick: () -> Unit
) {
    Image(
        painter = rememberAsyncImagePainter(post.imageUrl),
        contentDescription = post.caption,
        modifier = Modifier
            .aspectRatio(1f)
            .background(ZeroStroke)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun EmptyPosts() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.GridOn,
                contentDescription = null,
                tint = ZeroTextMuted,
                modifier = Modifier.size(54.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Nenhuma publicacao ainda",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Quando voce postar uma imagem, ela aparece aqui.",
                color = ZeroTextMuted,
                textAlign = TextAlign.Center,
                fontSize = 13.sp
            )
        }
    }
}
