package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.AutenticarViewModel

@Composable
fun LoginScreen(navController: NavController) {

    // === ViewModel de autenticación ===
    val autenticarViewModel: AutenticarViewModel = viewModel()
    val isUserLoggedIn by autenticarViewModel.isLoggedIn.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo principal
            Image(
                painter = painterResource(id = R.drawable.logo_claro),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Inicia Sesión",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón principal
            Button(
                onClick = {
                    /* TODO: esto está pendiente de implementar */
                    // Aquí más adelante se llamará al ViewModel
                    // por ahora solo navega de prueba

                    // === Implementación provisional ===
                    autenticarViewModel.iniciarSesion(email, password)

                    // Si el login fue exitoso, redirigir a HomeSesionIniciada
                    if (isUserLoggedIn) {
                        navController.navigate("home_iniciada") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            // Enlace a registro
            TextButton(
                onClick = { navController.navigate("registro") }
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            // Botón volver por si el cliente se equivcó y quiere solo volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
