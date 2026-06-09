package com.example.zerosekai

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.zerosekai.navigation.NavGraph
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBackground
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.ui.theme.ZeroSekaiTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            try {
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

    var isLogin by remember {
        mutableStateOf(true)
    }

    var isLoggedIn by remember {
        mutableStateOf(false)
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    fun submitAuth() {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(
                context,
                "Preencha todos os campos.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                context,
                "Email invalido.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(
                context,
                "Use um email Gmail.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (password.length < 6) {
            Toast.makeText(
                context,
                "A senha precisa ter pelo menos 6 caracteres.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

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

                    isLoggedIn = true
                } else {
                    val errorMessage = when {
                        it.exception?.message?.contains(
                            "badly formatted"
                        ) == true -> {
                            "Email invalido."
                        }

                        it.exception?.message?.contains(
                            "INVALID_LOGIN_CREDENTIALS"
                        ) == true -> {
                            "Email ou senha incorretos."
                        }

                        it.exception?.message?.contains(
                            "There is no user record"
                        ) == true -> {
                            "Voce precisa criar uma conta primeiro."
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

                    isLoggedIn = true
                } else {
                    val errorMessage = when {
                        it.exception?.message?.contains(
                            "already in use"
                        ) == true -> {
                            "Esse email ja esta cadastrado."
                        }

                        it.exception?.message?.contains(
                            "Password should be at least 6 characters"
                        ) == true -> {
                            "A senha precisa ter pelo menos 6 caracteres."
                        }

                        it.exception?.message?.contains(
                            "badly formatted"
                        ) == true -> {
                            "Email invalido."
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
    }

    if (isLoggedIn) {
        NavGraph()
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fundo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ZBackground.copy(alpha = 0.58f),
                                ZPrimary.copy(alpha = 0.20f),
                                ZBackground.copy(alpha = 0.98f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ZAccent.copy(alpha = 0.26f),
                                Color.Transparent,
                                ZBackground.copy(alpha = 0.34f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo ZeroSekai",
                    modifier = Modifier.size(190.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = if (isLogin) {
                        "Bem-vindo de volta"
                    } else {
                        "Crie sua conta"
                    },
                    color = ZText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isLogin) {
                        "Entre na sua conta e continue sua jornada"
                    } else {
                        "Comece sua jornada no universo ZeroSekai"
                    },
                    color = ZTextMuted,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        placeholder = "Email",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LoginTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        placeholder = "Senha",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Ocultar senha"
                                    } else {
                                        "Mostrar senha"
                                    }
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                submitAuth()
                            }
                        )
                    )

                    if (isLogin) {
                        TextButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Recuperacao de senha em breve.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Esqueceu sua senha?",
                                color = ZAccent,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    LoginActionButton(
                        text = if (isLogin) {
                            "Entrar"
                        } else {
                            "Criar conta"
                        },
                        onClick = {
                            submitAuth()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    LoginFooter(
                        isLogin = isLogin,
                        onToggleMode = {
                            isLogin = !isLogin
                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp),
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(24.dp),
        colors = loginTextFieldColors()
    )
}

@Composable
private fun LoginActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ZPrimary,
                        ZAccent,
                        ZSecondary
                    )
                )
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ZText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.22f)
        ),
        contentPadding = PaddingValues(horizontal = 22.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LoginFooter(
    isLogin: Boolean,
    onToggleMode: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isLogin) {
                "Ainda nao tem conta?"
            } else {
                "Ja tem conta?"
            },
            color = ZTextMuted,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(6.dp))

        TextButton(
            onClick = onToggleMode,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Text(
                text = if (isLogin) {
                    "Cadastre-se >"
                } else {
                    "Entrar >"
                },
                color = ZAccent,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun loginTextFieldColors(): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = ZText,
        unfocusedTextColor = ZText,
        focusedPlaceholderColor = ZTextMuted,
        unfocusedPlaceholderColor = ZTextMuted,
        focusedLeadingIconColor = ZAccent,
        unfocusedLeadingIconColor = ZAccent.copy(alpha = 0.88f),
        focusedTrailingIconColor = ZText,
        unfocusedTrailingIconColor = ZTextMuted,
        cursorColor = ZAccent,
        focusedBorderColor = ZAccent.copy(alpha = 0.88f),
        unfocusedBorderColor = ZBorder.copy(alpha = 0.88f),
        focusedContainerColor = ZSurface.copy(alpha = 0.62f),
        unfocusedContainerColor = ZSurface.copy(alpha = 0.52f)
    )
