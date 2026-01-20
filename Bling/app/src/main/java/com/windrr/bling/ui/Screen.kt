package com.windrr.bling.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object Player : Screen("player_screen")
}