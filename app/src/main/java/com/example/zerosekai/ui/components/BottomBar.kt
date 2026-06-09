package com.example.zerosekai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSurface
import com.example.zerosekai.ui.theme.ZTextMuted

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("search", "Busca", Icons.Default.Search),
        BottomNavItem("create_post", "Postar", Icons.Default.AddBox),
        BottomNavItem("chat_list", "Chat", Icons.Default.Chat),
        BottomNavItem("profile", "Perfil", Icons.Default.Person)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = ZSurface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, ZBorderSoft.copy(alpha = 0.8f))
    ) {
        NavigationBar(
            modifier = Modifier
                .height(64.dp)
                .clip(RoundedCornerShape(26.dp)),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->

                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            onNavigate(item.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(text = item.title)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZPrimary,
                        selectedTextColor = ZPrimary,
                        unselectedIconColor = ZTextMuted,
                        unselectedTextColor = ZTextMuted,
                        indicatorColor = ZCard.copy(alpha = 0.92f),
                        disabledIconColor = ZBorderSoft,
                        disabledTextColor = ZBorderSoft
                    ),
                    alwaysShowLabel = false
                )
            }
        }
    }
}
