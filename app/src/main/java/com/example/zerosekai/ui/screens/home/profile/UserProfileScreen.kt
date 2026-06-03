package com.example.zerosekai.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.data.model.User
import com.example.zerosekai.data.repository.FollowRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerosekai.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

private val ZeroBackground = Color(0xFF050505)
private val ZeroSurface = Color(0xFF111111)
private val ZeroAccent = Color(0xFFEDEDED)
private val ZeroMuted = Color(0xFF9B9B9B)

@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userId: String
) {

    val repository = remember {
        FollowRepository()
    }

    val chatViewModel: ChatViewModel = viewModel()

    val scope = rememberCoroutineScope()

    var user by remember {
        mutableStateOf<User?>(null)
    }

    var posts by remember {
        mutableStateOf<List<Post>>(emptyList())
    }

    var followersCount by remember {
        mutableStateOf(0)
    }

    var followingCount by remember {
        mutableStateOf(0)
    }

    var isFollowing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        try {

            val firestore =
                FirebaseFirestore.getInstance()

            val userDoc =
                firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

            user =
                userDoc.toObject(User::class.java)

            val postsSnapshot =
                firestore
                    .collection("posts")
                    .whereEqualTo(
                        "userId",
                        userId
                    )
                    .get()
                    .await()

            posts =
                postsSnapshot
                    .toObjects(Post::class.java)

            followersCount =
                repository.getFollowersCount(
                    userId
                )

            followingCount =
                repository.getFollowingCount(
                    userId
                )

            isFollowing =
                repository.isFollowing(
                    userId
                )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
    ) {

        TopBar(
            navController = navController
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (!user?.photoUrl.isNullOrBlank()) {

                    Image(
                        painter = rememberAsyncImagePainter(
                            user?.photoUrl
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(ZeroSurface),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ZeroMuted,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                    Arrangement.SpaceEvenly
                ) {

                    ProfileStat(
                        value = posts.size.toString(),
                        label = "Posts"
                    )

                    ProfileStat(
                        value = followersCount.toString(),
                        label = "Seguidores"
                    )

                    ProfileStat(
                        value = followingCount.toString(),
                        label = "Seguindo"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text =
                user?.username ?: "Usuário",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                user?.bio
                    ?: "Usuário do ZeroSekai",
                color = ZeroMuted,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {

                        scope.launch {

                            repository.toggleFollow(
                                userId
                            )

                            isFollowing =
                                !isFollowing

                            followersCount =
                                if (isFollowing) {
                                    followersCount + 1
                                } else {
                                    followersCount - 1
                                }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZeroAccent,
                        contentColor = Color.Black
                    )
                ) {

                    Text(
                        text =
                        if (isFollowing) {
                            "Seguindo"
                        } else {
                            "Seguir"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {

                        val currentUid =
                            FirebaseAuth
                                .getInstance()
                                .currentUser
                                ?.uid
                                ?: return@Button

                        chatViewModel.createChat(
                            currentUid = currentUid,
                            otherUid = userId
                        ) { chatId ->

                            navController.navigate(
                                "chat/$chatId"
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {

                    Text("Mensagem")
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            HorizontalDivider(
                color = Color(0xFF222222)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement =
                Arrangement.spacedBy(4.dp),
                horizontalArrangement =
                Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                items(posts) { post ->

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(ZeroSurface)
                            .clickable {

                                navController.navigate(
                                    "post_detail/${post.id}"
                                )
                            }
                    ) {

                        if (post.imageUrl.isNotBlank()) {

                            Image(
                                painter =
                                rememberAsyncImagePainter(
                                    post.imageUrl
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale =
                                ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    navController: NavHostController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            text = "Perfil",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String
) {

    Column(
        horizontalAlignment =
        Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            text = label,
            color = ZeroMuted,
            fontSize = 13.sp
        )
    }
}