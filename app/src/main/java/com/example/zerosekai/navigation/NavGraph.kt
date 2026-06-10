package com.example.zerosekai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.zerosekai.ui.screens.home.CreatePostScreen
import com.example.zerosekai.ui.screens.home.HomeScreen
import com.example.zerosekai.ui.screens.home.NotificationsScreen
import com.example.zerosekai.ui.screens.home.PostDetailScreen
import com.example.zerosekai.ui.screens.home.SavedPostsScreen
import com.example.zerosekai.ui.screens.home.ChatScreen
import com.example.zerosekai.ui.screens.home.ChatListScreen

import com.example.zerosekai.ui.screens.profile.EditProfileScreen
import com.example.zerosekai.ui.screens.profile.ProfileScreen
import com.example.zerosekai.ui.screens.profile.UserProfileScreen

import com.example.zerosekai.ui.screens.search.SearchScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("create_post") {
            CreatePostScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("edit_profile") {
            EditProfileScreen(navController)
        }

        composable("search") {
            SearchScreen(navController)
        }

        composable("notifications") {
            NotificationsScreen(navController)
        }

        composable("saved_posts") {
            SavedPostsScreen(navController)
        }

        composable("post_detail/{postId}") { backStackEntry ->

            val postId = backStackEntry.arguments?.getString("postId") ?: ""

            PostDetailScreen(
                navController = navController,
                postId = postId
            )
        }

        composable("user_profile/{userId}") { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            UserProfileScreen(
                navController = navController,
                userId = userId
            )
        }

        composable("chat_list") {
            ChatListScreen(
                onNavigate = { route ->
                    if (route == "home") {
                        navController.navigate("home") {
                            launchSingleTop = true
                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    } else {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo("home") {
                                saveState = true
                            }
                        }
                    }
                },
                onOpenChat = { chatId ->
                    navController.navigate("chat/$chatId")
                }
            )
        }

        composable("chat/{chatId}") { backStackEntry ->

            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""

            ChatScreen(
                chatId = chatId,
                navController = navController
            )
        }
    }
}
