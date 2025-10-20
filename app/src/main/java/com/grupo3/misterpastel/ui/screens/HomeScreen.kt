package com.grupo3.misterpastel.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import com.grupo3.misterpastel.R

@Composable
fun HomeScreen(navController: NavController) {
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
            // Logo principal
            Image(
                painter = painterResource(id = R.drawable.logo1_sf),
                contentDescription = "Logo Pastelería Mister Pastel",
                modifier = Modifier.size(120.dp)
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
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Catálogo")
            }
        }
    }
}

