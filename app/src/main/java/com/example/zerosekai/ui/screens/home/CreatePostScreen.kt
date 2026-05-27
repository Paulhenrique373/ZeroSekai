package com.example.zerosekai.ui.screens.home

import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import coil.compose.rememberAsyncImagePainter

import com.example.zerosekai.viewmodel.CreatePostViewModel

@Composable
fun CreatePostScreen() {

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0B0B),
                        Color(0xFF1A001F),
                        Color(0xFF2B0033)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // BOTÃO ESCOLHER IMAGEM
            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Escolher imagem")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // PREVIEW IMAGEM
            imageUri?.let { uri ->

                Image(
                    painter = rememberAsyncImagePainter(uri),

                    contentDescription = null,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        ),

                    contentScale = ContentScale.Crop
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // LEGENDA
            OutlinedTextField(
                value = caption,

                onValueChange = { newText ->
                    caption = newText
                },

                label = {
                    Text("Legenda")
                },

                modifier = Modifier.fillMaxWidth(),

                maxLines = 4
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // BOTÃO PUBLICAR
            Button(
                onClick = {

                    // VALIDAR IMAGEM
                    if (imageUri == null) {

                        Toast.makeText(
                            context,
                            "Escolha uma imagem",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    // VALIDAR LEGENDA
                    if (caption.isBlank()) {

                        Toast.makeText(
                            context,
                            "Digite uma legenda",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    viewModel.uploadPost(
                        context = context,
                        imageUri = imageUri!!,
                        caption = caption,
                        onSuccess = {

                            Toast.makeText(
                                context,
                                "Post publicado 🚀",
                                Toast.LENGTH_LONG
                            ).show()

                            // LIMPAR CAMPOS
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

                enabled = !isLoading
            ) {

                Text(

                    if (isLoading)
                        "Publicando..."
                    else
                        "Publicar"
                )
            }
        }

        // LOADING
        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.4f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.White
                )
            }
        }
    }
}