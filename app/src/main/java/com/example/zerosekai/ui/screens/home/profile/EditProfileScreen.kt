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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroGradientButton
import com.example.zerosekai.ui.components.ZeroPrimaryGradient
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.zeroTextFieldColors
import com.example.zerosekai.ui.theme.ZSurfaceElevated
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.ProfileViewModel

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

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            ZeroTopBar(
                title = "Editar perfil",
                subtitle = "Atualize sua identidade no ZeroSekai",
                onBackClick = {
                    navController.popBackStack()
                }
            )

            ZeroElevatedPanel(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfilePhotoSection(
                        photoUrl = user?.photoUrl,
                        selectedPhotoUri = selectedPhotoUri,
                        onPhotoClick = {
                            photoPicker.launch("image/*")
                        }
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { newValue ->
                            username = newValue
                        },
                        label = {
                            Text(text = "Nome")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = zeroTextFieldColors()
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
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = zeroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    ZeroGradientButton(
                        text = if (saving) {
                            "Salvando..."
                        } else {
                            "Salvar alterações"
                        },
                        onClick = {
                            if (username.trim().isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Digite seu nome",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@ZeroGradientButton
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
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !saving,
                        leadingIcon = Icons.Default.Save
                    )

                    if (saving) {
                        Spacer(modifier = Modifier.height(18.dp))

                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ZText,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
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
            .size(126.dp)
            .clickable(onClick = onPhotoClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(ZeroPrimaryGradient)
                .padding(4.dp),
            contentAlignment = Alignment.Center
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
                        .background(ZSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = ZTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ZText),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Alterar foto",
                tint = Color.Black,
                modifier = Modifier.size(21.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Toque na foto para alterar",
        color = ZTextMuted,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
}
