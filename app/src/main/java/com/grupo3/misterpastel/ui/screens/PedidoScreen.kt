package com.grupo3.misterpastel.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.viewmodel.PedidoViewModel

// Opt-in porque TopAppBar es experimental en tu versión de Material3
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Pantalla de resultado del pedido (simula estados y muestra mensaje).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(navController: NavController, vm: PedidoViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val ctx: Context = LocalContext.current

    // Confirma apenas se entra (una sola vez)
    LaunchedEffect(Unit) {
        vm.confirmarPedido(ctx)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Estado del pedido") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Estado: ${state.estado}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            if (state.mensaje != null) {
                Text(state.mensaje!!)
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { navController.navigate("home_iniciada") }) {
                Text("Volver al inicio")
            }
        }
    }
}
