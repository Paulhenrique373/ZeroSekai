package com.example.zerosekai.ui.screens.profile

import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val viewModel: ProfileViewModel =
        viewModel()

    val user by viewModel.user.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val error by viewModel.error.collectAsState()

    val context =
        LocalContext.current

    var username by remember {
        mutableStateOf("")
    }

    var bio by remember {
        mutableStateOf("")
    }

    var selectedPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            selectedPhotoUri = uri
        }

    LaunchedEffect(user) {
        username = user?.username.orEmpty()
        bio = user?.bio.orEmpty()
    }

    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
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
                text = "Editar perfil",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(118.dp)
                    .clickable {
                        photoPicker.launch("image/*")
                    }
            ) {
                val preview =
                    selectedPhotoUri ?: user?.photoUrl

                if (preview != null && preview.toString().isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(preview),
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
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = ZeroTextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ZeroAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Alterar foto",
                        tint = Color.Black,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = {
                    Text("Nome")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = {
                    bio = it
                },
                label = {
                    Text("Bio")
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    if (username.isBlank()) {
                        Toast.makeText(
                            context,
                            "Digite seu nome.",
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
                        navController.popBackStack()
                    }
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZeroAccent,
                    contentColor = Color.Black
                )
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Salvar alteracoes",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
