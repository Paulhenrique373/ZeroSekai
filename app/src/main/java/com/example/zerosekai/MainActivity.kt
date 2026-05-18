package com.example.zerosekai

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.lifecycleScope

import com.example.zerosekai.navigation.NavGraph
import com.example.zerosekai.ui.theme.ZeroSekaiTheme

import com.google.firebase.auth.FirebaseAuth

// 🔥 SUPABASE
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // 🔥 TESTE SUPABASE
        lifecycleScope.launch {

            try {

                // 🔥 TESTE SIMPLES
                Log.d(
                    "SUPABASE",
                    "Supabase conectado com sucesso!"
                )

            } catch (e: Exception) {

                Log.e(
                    "SUPABASE",
                    "Erro: ${e.message}"
                )
            }
        }

        setContent {

            ZeroSekaiTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    LoginScreen(auth)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    auth: FirebaseAuth
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    // 🔥 COMEÇA NO LOGIN
    var isLogin by remember {
        mutableStateOf(true)
    }

    // 🔥 CONTROLE LOGIN
    var isLoggedIn by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // 🔥 ABRIR HOME
    if (isLoggedIn) {

        NavGraph()

    } else {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🔥 IMAGEM FUNDO
            Image(
                painter = painterResource(
                    id = R.drawable.fundo
                ),

                contentDescription = null,

                contentScale = ContentScale.Crop,

                modifier = Modifier.fillMaxSize()
            )

            // 🔥 OVERLAY ESCURO
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // 🔥 CONTEÚDO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                verticalArrangement = Arrangement.Center,

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔥 LOGO
                Image(
                    painter = painterResource(
                        id = R.drawable.logo
                    ),

                    contentDescription = "Logo",

                    modifier = Modifier.size(390.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // 🔥 NOME APP
                Text(
                    text = "ZeroSekai",

                    color = Color.White,

                    fontSize = 34.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                // 🔥 SUBTÍTULO
                Text(
                    text = "Sua rede social anime 🚀",

                    color = Color.LightGray,

                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                // 🔥 EMAIL
                OutlinedTextField(
                    value = email,

                    onValueChange = {
                        email = it
                    },

                    label = {
                        Text("Email")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // 🔥 SENHA
                OutlinedTextField(
                    value = password,

                    onValueChange = {
                        password = it
                    },

                    label = {
                        Text("Senha")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    visualTransformation =
                    PasswordVisualTransformation()
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // 🔥 BOTÃO LOGIN/CADASTRO
                Button(
                    onClick = {

                        // 🔥 VALIDAR CAMPOS
                        if (
                            email.isBlank() ||
                            password.isBlank()
                        ) {

                            Toast.makeText(
                                context,
                                "Preencha todos os campos.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@Button
                        }

                        // 🔥 VALIDAR EMAIL
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                            Toast.makeText(
                                context,
                                "Email inválido.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@Button
                        }

                        // 🔥 ACEITAR APENAS GMAIL
                        if (!email.endsWith("@gmail.com")) {

                            Toast.makeText(
                                context,
                                "Use um email Gmail.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@Button
                        }

                        // 🔥 VALIDAR SENHA
                        if (password.length < 6) {

                            Toast.makeText(
                                context,
                                "A senha precisa ter pelo menos 6 caracteres.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@Button
                        }

                        // 🔥 LOGIN
                        if (isLogin) {

                            auth.signInWithEmailAndPassword(
                                email.trim(),
                                password.trim()
                            ).addOnCompleteListener {

                                if (it.isSuccessful) {

                                    Toast.makeText(
                                        context,
                                        "Login realizado!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 🔥 ENTRAR HOME
                                    isLoggedIn = true

                                } else {

                                    val errorMessage = when {

                                        it.exception?.message?.contains(
                                            "badly formatted"
                                        ) == true -> {

                                            "Email inválido."
                                        }

                                        it.exception?.message?.contains(
                                            "INVALID_LOGIN_CREDENTIALS"
                                        ) == true -> {

                                            "Email ou senha incorretos."
                                        }

                                        it.exception?.message?.contains(
                                            "There is no user record"
                                        ) == true -> {

                                            "Você precisa criar uma conta primeiro."
                                        }

                                        else -> {

                                            "Erro ao fazer login."
                                        }
                                    }

                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } else {

                            // 🔥 CADASTRO
                            auth.createUserWithEmailAndPassword(
                                email.trim(),
                                password.trim()
                            ).addOnCompleteListener {

                                if (it.isSuccessful) {

                                    Toast.makeText(
                                        context,
                                        "Conta criada!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 🔥 ENTRAR HOME
                                    isLoggedIn = true

                                } else {

                                    val errorMessage = when {

                                        it.exception?.message?.contains(
                                            "already in use"
                                        ) == true -> {

                                            "Esse email já está cadastrado."
                                        }

                                        it.exception?.message?.contains(
                                            "Password should be at least 6 characters"
                                        ) == true -> {

                                            "A senha precisa ter pelo menos 6 caracteres."
                                        }

                                        it.exception?.message?.contains(
                                            "badly formatted"
                                        ) == true -> {

                                            "Email inválido."
                                        }

                                        else -> {

                                            "Erro ao criar conta."
                                        }
                                    }

                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {

                    Text(
                        text =
                        if (isLogin)
                            "Entrar"
                        else
                            "Cadastrar",

                        fontSize = 16.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // 🔥 TROCAR LOGIN/CADASTRO
                TextButton(
                    onClick = {

                        isLogin = !isLogin
                    }
                ) {

                    Text(
                        text =
                        if (isLogin)
                            "Não tem conta? Cadastre-se"
                        else
                            "Já tem conta? Entrar",

                        color = Color.White
                    )
                }
            }
        }
    }
}