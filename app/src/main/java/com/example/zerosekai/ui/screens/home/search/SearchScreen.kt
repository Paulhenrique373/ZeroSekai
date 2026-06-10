package com.example.zerosekai.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.zerosekai.R
import com.example.zerosekai.data.model.User
import com.example.zerosekai.ui.components.BottomBar
import com.example.zerosekai.ui.components.SearchLoadingSkeleton
import com.example.zerosekai.ui.components.ZeroAvatar
import com.example.zerosekai.ui.components.ZeroEmptyState
import com.example.zerosekai.ui.components.ZeroSectionHeader
import com.example.zerosekai.ui.components.ZeroScreenBackground
import com.example.zerosekai.ui.components.ZeroTopBar
import com.example.zerosekai.ui.components.zeroTextFieldColors
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZBorder
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZText
import com.example.zerosekai.ui.theme.ZTextMuted
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun SearchScreen(
    navController: NavHostController
) {
    var searchText by remember {
        mutableStateOf("")
    }

    var users by remember {
        mutableStateOf<List<User>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(searchText) {
        if (searchText.isBlank()) {
            users = emptyList()
            return@LaunchedEffect
        }

        loading = true

        try {
            val snapshot =
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .get()
                    .await()

            users =
                snapshot
                    .toObjects(User::class.java)
                    .filter {
                        it.username.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
        }
    }

    ZeroScreenBackground(
        modifier = Modifier.fillMaxSize(),
        backgroundRes = R.drawable.bg_search
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(
                    currentRoute = "search",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                ZeroTopBar(
                    title = "Buscar",
                    subtitle = "Encontre perfis e novas conexões",
                    actions = {
                        Surface(
                            color = ZCard.copy(alpha = 0.58f),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.78f))
                        ) {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = ZText
                                )
                            }
                        }
                    }
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    placeholder = {
                        Text("Pesquisar usuários...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    searchText = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpar busca"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = zeroTextFieldColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    loading -> {
                        ZeroSectionHeader(
                            title = "Buscando",
                            subtitle = "Preparando resultados para voce"
                        )

                        SearchLoadingSkeleton(
                            modifier = Modifier.padding(top = 4.dp),
                            itemCount = 5
                        )
                    }

                    searchText.isBlank() -> {
                        ZeroEmptyState(
                            icon = Icons.Default.PersonSearch,
                            title = "Procure por um usuario",
                            message = "Digite um nome para descobrir perfis no ZeroSekai.",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    users.isEmpty() -> {
                        ZeroEmptyState(
                            icon = Icons.Default.Search,
                            title = "Nada encontrado",
                            message = "Tente outro nome ou uma busca mais curta.",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            item {
                                ZeroSectionHeader(
                                    title = "Resultados",
                                    subtitle = "${users.size} perfis encontrados"
                                )
                            }

                            items(
                                items = users,
                                key = { user -> user.uid }
                            ) { user ->
                                UserItem(
                                    user = user,
                                    onClick = {
                                        navController.navigate(
                                            "user_profile/${user.uid}"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = ZCard.copy(alpha = 0.92f),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ZBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZeroAvatar(
                photoUrl = user.photoUrl,
                size = 58.dp,
                label = user.username
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.username.ifBlank { "Zero User" },
                    color = ZText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = user.bio.ifBlank { "Usuario do ZeroSekai" },
                    color = ZTextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = ZPrimary.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ZAccent.copy(alpha = 0.72f))
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = ZAccent,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
