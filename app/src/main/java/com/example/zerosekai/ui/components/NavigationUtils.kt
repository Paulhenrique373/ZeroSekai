package com.example.zerosekai.ui.components

import androidx.navigation.NavHostController

fun NavHostController.navigateBottomBarRoute(route: String) {
    if (route == "home") {
        val returnedToHome =
            popBackStack(
                route = "home",
                inclusive = false
            )

        if (!returnedToHome) {
            navigate("home") {
                launchSingleTop = true
            }
        }

        return
    }

    navigate(route) {
        launchSingleTop = true

        popUpTo("home") {
            saveState = false
        }
    }
}
