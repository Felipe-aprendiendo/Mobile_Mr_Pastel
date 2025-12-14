package com.grupo3.misterpastel.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.SessionViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val sessionViewModel: SessionViewModel = viewModel()
    val usuario by sessionViewModel.usuarioActual.collectAsState()
    val sessionChecked by sessionViewModel.sessionChecked.collectAsState()

    LaunchedEffect(sessionChecked, usuario) {
        if (!sessionChecked) return@LaunchedEffect

        // Mantiene el splash visible un tiempo mínimo para evitar parpadeo
        delay(800)

        if (usuario != null) {
            navController.navigate("home_iniciada") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_oscuro),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp)
            )
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary,
                strokeWidth = 6.dp,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
