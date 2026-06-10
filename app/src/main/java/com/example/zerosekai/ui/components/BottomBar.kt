package com.example.zerosekai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.zerosekai.ui.components.ZeroNeonBorderBrush
import com.example.zerosekai.ui.theme.ZBorderSoft
import com.example.zerosekai.ui.theme.ZCard
import com.example.zerosekai.ui.theme.ZAccent
import com.example.zerosekai.ui.theme.ZPrimary
import com.example.zerosekai.ui.theme.ZSecondary
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
            .padding(horizontal = 18.dp, vertical = 10.dp),
        color = ZSurface.copy(alpha = 0.74f),
        tonalElevation = 8.dp,
        shadowElevation = 14.dp,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(
            width = 1.4.dp,
            brush = ZeroNeonBorderBrush
        )
    ) {
        NavigationBar(
            modifier = Modifier
                .height(72.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ZCard.copy(alpha = 0.78f),
                            ZSurface.copy(alpha = 0.44f)
                        )
                    )
                ),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->

                val selected = currentRoute == item.route
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) {
                        1.12f
                    } else {
                        1f
                    },
                    label = "bottomBarIconScale"
                )

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
                            contentDescription = item.title,
                            modifier = Modifier.graphicsLayer(
                                scaleX = iconScale,
                                scaleY = iconScale
                            )
                        )
                    },
                    label = {
                        Text(text = item.title)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZAccent,
                        selectedTextColor = ZAccent,
                        unselectedIconColor = ZTextMuted,
                        unselectedTextColor = ZTextMuted,
                        indicatorColor = ZPrimary.copy(alpha = 0.22f),
                        disabledIconColor = ZBorderSoft,
                        disabledTextColor = ZBorderSoft
                    ),
                    alwaysShowLabel = false
                )
            }
        }
    }
}
