package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.AutenticarViewModel

@Composable
fun HomeScreen(navController: NavController) {

    // === ViewModel de autenticación ===
    // Permite verificar si el usuario tiene sesión iniciada
    val autenticarViewModel: AutenticarViewModel = viewModel()
    val isUserLoggedIn by autenticarViewModel.isLoggedIn.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo principal
            Image(
                painter = painterResource(id = R.drawable.logo_claro),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(350.dp)
            )

            // Título de bienvenida
            Text(
                text = "¡Bienvenido a Pastelería 1000 Sabores!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Botón para iniciar sesión
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            // Botón para registrarse
            OutlinedButton(
                onClick = { navController.navigate("registro") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Cuenta")
            }

            // Botón para ver el catálogo sin iniciar sesión
            OutlinedButton(
                onClick = {
                    // Si el usuario tiene sesión activa lo enviamos al HomeSesionIniciada
                    // Caso contrario, debe iniciar sesión primero
                    if (isUserLoggedIn) {
                        navController.navigate("home_iniciada")
                    } else {
                        navController.navigate("login")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Catálogo")
            }
        }
    }
}
