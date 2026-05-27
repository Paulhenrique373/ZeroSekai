package com.example.zerosekai.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.viewmodel.ProfileViewModel

private val ZeroBackground = Color(0xFF050505)
private val ZeroSurface = Color(0xFF111111)
private val ZeroTextMuted = Color(0xFF9B9B9B)
private val ZeroAccent = Color(0xFFEDEDED)

@Composable
fun EditProfileScreen(
    navController: NavHostController
) {

    val viewModel: ProfileViewModel = viewModel()

    val user by viewModel.user.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val error by viewModel.error.collectAsState()

    val context = LocalContext.current

    var username by rememberSaveable {
        mutableStateOf("")
    }

    var bio by rememberSaveable {
        mutableStateOf("")
    }

    var selectedPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPhotoUri = uri
    }

    LaunchedEffect(user) {

        user?.let { currentUser ->

            if (username.isBlank()) {
                username = currentUser.username
            }

            if (bio.isBlank()) {
                bio = currentUser.bio
            }
        }
    }

    LaunchedEffect(error) {

        error?.let { errorMessage ->

            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeroBackground)
    ) {

        TopBar(
            onBackClick = {
                navController.popBackStack()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            ProfilePhotoSection(
                photoUrl = user?.photoUrl,
                selectedPhotoUri = selectedPhotoUri,
                onPhotoClick = {
                    photoPicker.launch("image/*")
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { newValue ->
                    username = newValue
                },
                label = {
                    Text(text = "Nome")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { newValue ->
                    bio = newValue
                },
                label = {
                    Text(text = "Bio")
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    if (username.trim().isBlank()) {

                        Toast.makeText(
                            context,
                            "Digite seu nome",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    viewModel.updateProfile(
                        username = username.trim(),
                        bio = bio.trim(),
                        context = context,
                        imageUri = selectedPhotoUri
                    ) {

                        Toast.makeText(
                            context,
                            "Perfil atualizado",
                            Toast.LENGTH_SHORT
                        ).show()

                        navController.popBackStack()
                    }
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZeroAccent,
                    contentColor = Color.Black
                )
            ) {

                if (saving) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Salvar alterações",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBackClick
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        Text(
            text = "Editar perfil",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfilePhotoSection(
    photoUrl: String?,
    selectedPhotoUri: Uri?,
    onPhotoClick: () -> Unit
) {

    val imageModel = selectedPhotoUri ?: photoUrl

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .size(120.dp)
            .clickable {
                onPhotoClick()
            }
    ) {

        if (imageModel != null && imageModel.toString().isNotBlank()) {

            Image(
                painter = rememberAsyncImagePainter(model = imageModel),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(ZeroSurface),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ZeroTextMuted,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ZeroAccent),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Alterar foto",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun profileFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = ZeroTextMuted,
        cursorColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color(0xFF333333)
    )