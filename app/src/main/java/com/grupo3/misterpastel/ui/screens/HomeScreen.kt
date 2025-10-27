package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// 1. Importa lo necesario para el scroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grupo3.misterpastel.R

@Composable
fun HomeScreen(navController: NavController) {

    // 2. Crea el estado para recordar la posición del scroll
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            // 3. Aplica el modificador verticalScroll a la Columna
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth(), // Es buena idea añadir fillMaxWidth también
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo principal
            Image(
                painter = painterResource(id = R.drawable.logo_claro),
                contentDescription = "Logo Pastelería Mister Pastel",
                modifier = Modifier.size(250.dp)
            )

            // Título de bienvenida
            Text(
                text = "¡Bienvenido a Pastelería Mister Pastel!",
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
                    navController.navigate("home_iniciada")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Catálogo")
            }
        }
    }
}