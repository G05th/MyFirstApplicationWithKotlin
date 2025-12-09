package com.example.myfirstapplication.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }


    Surface(modifier =
        Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.Center,
) {
    Text(text = "Entrar", style = MaterialTheme.typography.headlineLarge)
    Spacer(modifier = Modifier.height(16.dp))


    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )


    Spacer(modifier = Modifier.height(8.dp))


    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Senha") },
        singleLine = true,
        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )


    error?.let { Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }


    Spacer(modifier = Modifier.height(20.dp))


    Button(onClick = {
// TODO: Delegate authentication to a ViewModel - placeholder behaviour for now
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = "Email inválido"
            return@Button
        }
        if (password.length < 6) {
            error = "Senha muito curta"
            return@Button
        }
        error = null
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Text(text = "Entrar", fontSize = 16.sp)
    }


    Spacer(modifier = Modifier.height(12.dp))


    TextButton(onClick = { navController.navigate("register") }) {
        Text(text = "Criar conta")
    }
}
}
}