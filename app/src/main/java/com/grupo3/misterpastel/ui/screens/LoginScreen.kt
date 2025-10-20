package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.LoginViewModel

/**
 * Pantalla de inicio de sesión conectada a LoginViewModel.
 * - Si el login es exitoso: navega a "home_iniciada".
 * - Muestra errores simples y estado de carga.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by vm.uiState.collectAsState()

    // Navega automáticamente cuando success = true
    LaunchedEffect(state.success) {
        if (state.success) {
            navController.navigate("home_iniciada") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Scroll y ajuste de insets (para teclado)
    val scrollState = rememberScrollState()
    val view = LocalView.current
    SideEffect {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets -> insets }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo1_sf),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Inicia Sesión",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón Ingresar
            Button(
                onClick = { vm.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.loading) "Ingresando..." else "Ingresar")
            }

            // Error (si existe)
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // Enlace a registro
            TextButton(onClick = { navController.navigate("registro") }) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            // Volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
