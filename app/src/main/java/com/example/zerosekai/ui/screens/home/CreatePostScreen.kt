package com.example.zerosekai.ui.screens.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.ZeroElevatedPanel
import com.example.zerosekai.ui.components.ZeroGradientButton
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.zeroTextFieldColors
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.viewmodel.CreatePostViewModel

@Composable
fun CreatePostScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: CreatePostViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var caption by remember {
        mutableStateOf("")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        imageUri = it
    }

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(
                    currentRoute = "create_post",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo("home") {
                                saveState = true
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ZeroTopBar(
                        title = "Criar post",
                        subtitle = "Publique um momento no ZeroSekai"
                    )

                    ZeroElevatedPanel(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        if (imageUri == null) {
                            EmptyImagePicker(
                                onClick = {
                                    launcher.launch("image/*")
                                }
                            )
                        } else {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 5f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        launcher.launch("image/*")
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    OutlinedTextField(
                        value = caption,
                        onValueChange = { newText ->
                            caption = newText
                        },
                        label = {
                            Text("Legenda")
                        },
                        placeholder = {
                            Text("Conte o que esta acontecendo...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(18.dp),
                        colors = zeroTextFieldColors()
                    )

                    ZeroGradientButton(
                        text = if (isLoading) {
                            "Publicando..."
                        } else {
                            "Publicar"
                        },
                        onClick = {
                            if (imageUri == null) {
                                Toast.makeText(
                                    context,
                                    "Escolha uma imagem",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@ZeroGradientButton
                            }

                            if (caption.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Digite uma legenda",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@ZeroGradientButton
                            }

                            viewModel.uploadPost(
                                context = context,
                                imageUri = imageUri!!,
                                caption = caption,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Post publicado",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    imageUri = null
                                    caption = ""
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        leadingIcon = Icons.Default.Send
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.42f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ZText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyImagePicker(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ZSurface,
                        ZPrimary.copy(alpha = 0.18f),
                        ZSecondary.copy(alpha = 0.12f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = ZBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ZBorderSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = ZText,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Escolher imagem",
                color = ZText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Toque para selecionar uma arte, foto ou cena.",
                color = ZTextMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
