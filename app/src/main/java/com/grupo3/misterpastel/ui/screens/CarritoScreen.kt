package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.repository.CarritoItem
import com.grupo3.misterpastel.viewmodel.CarritoViewModel

// Opt-in porque TopAppBar es experimental en tu versión de Material3
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Muestra items del carrito, permite ajustar cantidades,
 * aplicar cupón y ver totales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, vm: CarritoViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var cuponText by remember { mutableStateOf(state.cupon ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Carrito de compras") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Lista de items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                items(state.items) { item ->
                    CarritoItemRow(
                        item = item,
                        onMinus = {
                            val nueva = (item.cantidad - 1).coerceAtLeast(0)
                            vm.actualizarCantidad(item.producto.id, nueva)
                        },
                        onPlus = { vm.actualizarCantidad(item.producto.id, item.cantidad + 1) },
                        onRemove = { vm.eliminar(item.producto.id) }
                    )
                }
            }

            // Campo cupón simple
            OutlinedTextField(
                value = cuponText,
                onValueChange = { cuponText = it },
                label = { Text("Cupón de descuento (FELICES50)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.setCupon(cuponText) }) { Text("Aplicar") }
                OutlinedButton(onClick = { cuponText = ""; vm.setCupon(null) }) { Text("Quitar cupón") }
            }

            // Totales
            Text(
                text = "Total bruto: ${"%.0f".format(state.totalBruto)}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Total con descuento: ${"%.0f".format(state.totalConDescuento)}",
                style = MaterialTheme.typography.bodyLarge
            )

            // Acciones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.vaciar() }, modifier = Modifier.weight(1f)) {
                    Text("Vaciar")
                }
                Button(onClick = { navController.navigate("pedidos") }, modifier = Modifier.weight(1f)) {
                    Text("Confirmar pedido")
                }
            }
        }
    }
}

/** Fila reutilizable para un item del carrito */
@Composable
private fun CarritoItemRow(
    item: CarritoItem,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(item.producto.nombre, fontWeight = FontWeight.Bold)
            Text("Precio: ${item.producto.precio}")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onMinus) { Text("-") }
                Text("Cantidad: ${item.cantidad}")
                OutlinedButton(onClick = onPlus) { Text("+") }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onRemove) { Text("Quitar") }
            }
        }
    }
}
