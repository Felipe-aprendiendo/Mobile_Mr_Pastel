package com.grupo3.misterpastel.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.viewmodel.PagoViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun PagoProcesandoScreen(navController: NavController) {
    // Misma instancia de PagoViewModel anclada a "carrito"
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("carrito")
    }
    val pagoVM: PagoViewModel = viewModel(parentEntry)
    val comprobante by pagoVM.comprobante.collectAsState()

    // Validación de comprobante antes de continuar
    LaunchedEffect(comprobante) {
        if (comprobante == null) {
            // Si no hay comprobante válido, vuelve al carrito
            navController.popBackStack("carrito", inclusive = false)
        } else {
            delay(2000)
            navController.navigate("comprobante_pago") {
                popUpTo("procesando_pago") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Procesando el pago…",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
