package com.example.zerosekai

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.zerosekai.data.model.User
import com.example.zerosekai.navigation.NavGraph
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBackground
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZError
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.example.zerosekai.ui.theme.ZeroSekaiTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
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
                    var showSplash by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        delay(1500)
                        showSplash = false
                    }

                    if (showSplash) {
                        ZeroSplashScreen()
                    } else {
                        LoginScreen(auth)
                    }
                }
            }
        }
    }
}

private fun ensureUserProfile(
    firestore: FirebaseFirestore,
    uid: String,
    userEmail: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val safeEmail =
        userEmail.trim()

    val defaultUsername =
        safeEmail
            .substringBefore("@")
            .ifBlank {
                "Zero User"
            }

    val userRef =
        firestore
            .collection("users")
            .document(uid)

    userRef
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                onSuccess()
            } else {
                userRef
                    .set(
                        User(
                            uid = uid,
                            username = defaultUsername,
                            email = safeEmail,
                            bio = "",
                            photoUrl = "",
                            followers = emptyList(),
                            following = emptyList()
                        )
                    )
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError(
                            it.message
                                ?: "Conta criada, mas o perfil nao foi salvo."
                        )
                    }
            }
        }
        .addOnFailureListener {
            onError(
                it.message
                    ?: "Nao foi possivel carregar o perfil."
            )
        }
}

@Composable
private fun ZeroSplashScreen() {
    val transition =
        rememberInfiniteTransition(
            label = "zeroSplash"
        )
    val logoScale by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zeroSplashLogoScale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.62f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zeroSplashGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ZBackground,
                        ZPrimary.copy(alpha = 0.28f),
                        ZBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ZAccent.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo ZeroSekai",
                modifier = Modifier
                    .size(210.dp)
                    .graphicsLayer(
                        scaleX = logoScale,
                        scaleY = logoScale
                    )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ZEROSEKAI",
                color = ZText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Seu universo social anime",
                color = ZTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
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

    var authMessage by remember {
        mutableStateOf<String?>(null)
    }

    var authMessageIsError by remember {
        mutableStateOf(true)
    }

    var isAuthenticating by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    fun showAuthMessage(
        message: String,
        isError: Boolean
    ) {
        authMessage = message
        authMessageIsError = isError

        Toast.makeText(
            context,
            message,
            if (isError) {
                Toast.LENGTH_LONG
            } else {
                Toast.LENGTH_SHORT
            }
        ).show()
    }

    fun submitAuth() {
        if (isAuthenticating) {
            return
        }

        if (email.isBlank() || password.isBlank()) {
            showAuthMessage(
                message = "Preencha todos os campos.",
                isError = true
            )

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAuthMessage(
                message = "Email inválido.",
                isError = true
            )

            return
        }

        if (!email.endsWith("@gmail.com")) {
            showAuthMessage(
                message = "Use um email Gmail.",
                isError = true
            )

            return
        }

        if (password.length < 6) {
            showAuthMessage(
                message = "A senha precisa ter pelo menos 6 caracteres.",
                isError = true
            )

            return
        }

        isAuthenticating = true

        if (isLogin) {
            auth.signInWithEmailAndPassword(
                email.trim(),
                password.trim()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser =
                        auth.currentUser

                    if (currentUser == null) {
                        isAuthenticating = false

                        showAuthMessage(
                            message = "Erro ao carregar usuário.",
                            isError = true
                        )
                    } else {
                        ensureUserProfile(
                            firestore = firestore,
                            uid = currentUser.uid,
                            userEmail = currentUser.email ?: email.trim(),
                            onSuccess = {
                                showAuthMessage(
                                    message = "Login realizado!",
                                    isError = false
                                )

                                isLoggedIn = true
                            },
                            onError = { error ->
                                isAuthenticating = false

                                showAuthMessage(
                                    message = error,
                                    isError = true
                                )
                            }
                        )
                    }
                } else {
                    isAuthenticating = false

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
                            "Não foi possível entrar. Confira seus dados."
                        }
                    }

                    showAuthMessage(
                        message = errorMessage,
                        isError = true
                    )
                }
            }
        } else {
            auth.createUserWithEmailAndPassword(
                email.trim(),
                password.trim()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser =
                        auth.currentUser

                    if (currentUser == null) {
                        isAuthenticating = false

                        showAuthMessage(
                            message = "Conta criada, mas não foi possível criar o perfil.",
                            isError = true
                        )
                    } else {
                        ensureUserProfile(
                            firestore = firestore,
                            uid = currentUser.uid,
                            userEmail = currentUser.email ?: email.trim(),
                            onSuccess = {
                                showAuthMessage(
                                    message = "Conta criada!",
                                    isError = false
                                )

                                isLoggedIn = true
                            },
                            onError = { error ->
                                isAuthenticating = false

                                showAuthMessage(
                                    message = error,
                                    isError = true
                                )
                            }
                        )
                    }
                } else {
                    isAuthenticating = false

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
                            "Não foi possível criar sua conta."
                        }
                    }

                    showAuthMessage(
                        message = errorMessage,
                        isError = true
                    )
                }
            }
        }
    }

    fun resetPassword() {
        if (email.isBlank()) {
            showAuthMessage(
                message = "Digite seu email para recuperar a senha.",
                isError = true
            )

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAuthMessage(
                message = "Email inválido.",
                isError = true
            )

            return
        }

        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showAuthMessage(
                        message = "Enviamos um email para redefinir sua senha.",
                        isError = false
                    )
                } else {
                    showAuthMessage(
                        message = "Não foi possível enviar o email de recuperação.",
                        isError = true
                    )
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
                painter = painterResource(id = R.drawable.bg_login),
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
                                ZBackground.copy(alpha = 0.44f),
                                ZBackground.copy(alpha = 0.52f),
                                ZBackground.copy(alpha = 0.94f)
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
                                ZAccent.copy(alpha = 0.18f),
                                Color.Transparent,
                                ZBackground.copy(alpha = 0.58f)
                            )
                        )
                    )
            )

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val compact =
                    maxHeight < 720.dp

                val horizontalPadding =
                    if (compact) 22.dp else 40.dp

                val topSpace =
                    if (compact) 26.dp else 58.dp

                val titleBottomSpace =
                    if (compact) 8.dp else 10.dp

                val formTopSpace =
                    if (compact) {
                        if (authMessage.isNullOrBlank()) 22.dp else 12.dp
                    } else {
                        if (authMessage.isNullOrBlank()) 38.dp else 20.dp
                    }

                val fieldHeight =
                    if (compact) 58.dp else 70.dp

                val fieldGap =
                    if (compact) 12.dp else 16.dp

                val buttonHeight =
                    if (compact) 54.dp else 62.dp

                val sectionGap =
                    if (compact) 14.dp else 22.dp

                val accountButtonHeight =
                    if (compact) 48.dp else 56.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(topSpace))

                    LoginWordmark(
                        compact = compact
                    )

                    Spacer(modifier = Modifier.height(if (compact) 16.dp else 24.dp))

                    Text(
                        text = buildAnnotatedString {
                            append(
                                if (isLogin) {
                                    "Bem-vindo ao\n"
                                } else {
                                    "Crie sua conta\n"
                                }
                            )

                            withStyle(
                                SpanStyle(
                                    color = ZAccent,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) {
                                append("ZEROSEKAI")
                            }
                        },
                        color = ZText,
                        style = if (compact) {
                            MaterialTheme.typography.headlineSmall
                        } else {
                            MaterialTheme.typography.headlineMedium
                        },
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = if (compact) {
                            MaterialTheme.typography.headlineSmall.lineHeight
                        } else {
                            MaterialTheme.typography.headlineMedium.lineHeight
                        }
                    )

                    Spacer(modifier = Modifier.height(titleBottomSpace))

                    Text(
                        text = if (isLogin) {
                            "Conecte-se ao seu universo"
                        } else {
                            "Comece sua jornada no seu universo"
                        },
                        color = ZTextMuted,
                        style = if (compact) {
                            MaterialTheme.typography.bodyMedium
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                        textAlign = TextAlign.Center
                    )

                    AnimatedVisibility(
                        visible = !authMessage.isNullOrBlank(),
                        enter = fadeIn() + slideInVertically {
                            -it / 2
                        },
                        exit = fadeOut() + slideOutVertically {
                            -it / 2
                        }
                    ) {
                        AuthStatusMessage(
                            message = authMessage.orEmpty(),
                            isError = authMessageIsError,
                            compact = compact,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 440.dp)
                                .padding(top = if (compact) 10.dp else 18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(formTopSpace))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 440.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginGlassPanel(
                            compact = compact
                        ) {
                            LoginTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    authMessage = null
                                },
                                placeholder = "Email",
                                leadingIcon = {
                                    LoginLeadingIcon(
                                        icon = Icons.Default.Email,
                                        compact = compact
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                enabled = !isAuthenticating,
                                height = fieldHeight,
                                compact = compact
                            )

                            Spacer(modifier = Modifier.height(fieldGap))

                            LoginTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    authMessage = null
                                },
                                placeholder = "Senha",
                                leadingIcon = {
                                    LoginLeadingIcon(
                                        icon = Icons.Default.Lock,
                                        compact = compact
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        enabled = !isAuthenticating,
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
                                ),
                                enabled = !isAuthenticating,
                                height = fieldHeight,
                                compact = compact
                            )

                            if (isLogin) {
                                TextButton(
                                    onClick = {
                                        resetPassword()
                                    },
                                    enabled = !isAuthenticating,
                                    modifier = Modifier.align(Alignment.End),
                                    contentPadding = PaddingValues(
                                        horizontal = 0.dp,
                                        vertical = if (compact) 4.dp else 8.dp
                                    )
                                ) {
                                    Text(
                                        text = "Esqueceu sua senha?",
                                        color = ZAccent,
                                        style = if (compact) {
                                            MaterialTheme.typography.labelMedium
                                        } else {
                                            MaterialTheme.typography.bodyMedium
                                        },
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(fieldGap))
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
                                modifier = Modifier.fillMaxWidth(),
                                loading = isAuthenticating,
                                enabled = !isAuthenticating,
                                height = buttonHeight,
                                compact = compact
                            )

                            if (isLogin) {
                                Spacer(modifier = Modifier.height(sectionGap))

                                LoginOrDivider(
                                    compact = compact
                                )

                                Spacer(modifier = Modifier.height(if (compact) 14.dp else 24.dp))

                                CreateAccountButton(
                                    enabled = !isAuthenticating,
                                    height = accountButtonHeight,
                                    compact = compact,
                                    onClick = {
                                        isLogin = false
                                        authMessage = null
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(if (compact) 10.dp else 20.dp))

                            LoginFooter(
                                isLogin = isLogin,
                                enabled = !isAuthenticating,
                                compact = compact,
                                onToggleMode = {
                                    isLogin = !isLogin
                                    authMessage = null
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LoginWordmark(
    compact: Boolean
) {
    Surface(
        color = ZSurface.copy(alpha = 0.34f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = ZAccent.copy(alpha = 0.56f)
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 14.dp else 18.dp,
                vertical = if (compact) 7.dp else 9.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 8.dp else 10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF2FA3),
                                Color(0xFFB84DFF)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "ZEROSEKAI",
                color = ZText,
                style = if (compact) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.titleSmall
                },
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun LoginGlassPanel(
    compact: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ZSurface.copy(alpha = 0.38f),
        shape = RoundedCornerShape(if (compact) 24.dp else 30.dp),
        border = BorderStroke(
            width = 1.dp,
            color = ZAccent.copy(alpha = 0.42f)
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            ZSurface.copy(alpha = 0.16f),
                            ZBackground.copy(alpha = 0.10f)
                        )
                    )
                )
                .padding(
                    horizontal = if (compact) 12.dp else 16.dp,
                    vertical = if (compact) 14.dp else 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
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
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true,
    height: androidx.compose.ui.unit.Dp,
    compact: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                style = if (compact) {
                    MaterialTheme.typography.bodyLarge
                } else {
                    MaterialTheme.typography.titleMedium
                }
            )
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
private fun LoginLeadingIcon(
    icon: ImageVector,
    compact: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ZAccent,
            modifier = Modifier.size(
                if (compact) 24.dp else 30.dp
            )
        )

        Spacer(
            modifier = Modifier.width(
                if (compact) 12.dp else 16.dp
            )
        )

        Box(
            modifier = Modifier
                .height(if (compact) 28.dp else 34.dp)
                .width(1.dp)
                .background(ZBorder.copy(alpha = 0.72f))
        )
    }
}

@Composable
private fun AuthStatusMessage(
    message: String,
    isError: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor =
        if (isError) {
            ZError
        } else {
            ZSecondary
        }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = ZSurface.copy(alpha = 0.68f),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.72f)
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Text(
            text = message,
            color = ZText,
            style = if (compact) {
                MaterialTheme.typography.labelMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = if (compact) 12.dp else 16.dp,
                vertical = if (compact) 8.dp else 12.dp
            )
        )
    }
}

@Composable
private fun LoginActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    height: androidx.compose.ui.unit.Dp,
    compact: Boolean
) {
    val shape = RoundedCornerShape(
        if (compact) 18.dp else 26.dp
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7C35FF),
                        Color(0xFFD93CFF),
                        Color(0xFFFF2FA3)
                    )
                )
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ZText,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ZTextMuted
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
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = ZText,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = if (compact) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun LoginOrDivider(
    compact: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ZBorder.copy(alpha = 0.70f))
        )

        Text(
            text = "ou",
            color = ZTextMuted,
            style = if (compact) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = if (compact) 12.dp else 18.dp
            )
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ZBorder.copy(alpha = 0.70f))
        )
    }
}

@Composable
private fun CreateAccountButton(
    enabled: Boolean,
    height: androidx.compose.ui.unit.Dp,
    compact: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(
            if (compact) 14.dp else 18.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZSurface.copy(alpha = 0.36f),
            contentColor = ZText,
            disabledContainerColor = ZSurface.copy(alpha = 0.24f),
            disabledContentColor = ZTextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        border = BorderStroke(
            width = 1.2.dp,
            color = ZAccent.copy(alpha = 0.86f)
        )
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = ZAccent,
            modifier = Modifier.size(if (compact) 22.dp else 26.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Criar uma conta",
            style = if (compact) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LoginFooter(
    isLogin: Boolean,
    enabled: Boolean,
    compact: Boolean,
    onToggleMode: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isLogin) {
                "Ainda não tem conta?"
            } else {
                "Já tem conta?"
            },
            color = ZTextMuted,
            style = if (compact) {
                MaterialTheme.typography.labelMedium
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        TextButton(
            onClick = onToggleMode,
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Text(
                text = if (isLogin) {
                    "Cadastre-se >"
                } else {
                    "Entrar >"
                },
                color = ZAccent,
                style = if (compact) {
                    MaterialTheme.typography.bodyMedium
                } else {
                    MaterialTheme.typography.bodyLarge
                },
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
        disabledTextColor = ZTextMuted,
        focusedPlaceholderColor = ZTextMuted,
        unfocusedPlaceholderColor = ZTextMuted,
        disabledPlaceholderColor = ZTextMuted.copy(alpha = 0.64f),
        focusedLeadingIconColor = Color(0xFFFF4FD8),
        unfocusedLeadingIconColor = Color(0xFFFF4FD8).copy(alpha = 0.88f),
        disabledLeadingIconColor = Color(0xFFFF4FD8).copy(alpha = 0.42f),
        focusedTrailingIconColor = ZText,
        unfocusedTrailingIconColor = ZTextMuted,
        disabledTrailingIconColor = ZTextMuted.copy(alpha = 0.42f),
        cursorColor = Color(0xFFFF4FD8),
        focusedBorderColor = Color(0xFFD36BFF).copy(alpha = 0.92f),
        unfocusedBorderColor = Color(0xFF9D65FF).copy(alpha = 0.54f),
        disabledBorderColor = ZBorder.copy(alpha = 0.52f),
        focusedContainerColor = ZSurface.copy(alpha = 0.52f),
        unfocusedContainerColor = ZSurface.copy(alpha = 0.42f),
        disabledContainerColor = ZSurface.copy(alpha = 0.28f)
    )
