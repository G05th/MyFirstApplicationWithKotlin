package com.example.myfirstapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapplication.presentation.ToDoViewModel
import com.example.myfirstapplication.ui.login.LoginScreen
import com.example.myfirstapplication.ui.register.RegisterScreen
import com.example.myfirstapplication.ui.home.HomeScreen

@Composable
fun AppNavHost(viewModel: ToDoViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController, viewModel = viewModel) }
    }
}