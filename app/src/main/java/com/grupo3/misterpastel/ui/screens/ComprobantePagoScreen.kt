package com.grupo3.misterpastel.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grupo3.misterpastel.viewmodel.PagoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComprobantePagoScreen(
    navController: NavController,
    vm: PagoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val comprobante by vm.comprobante.collectAsState()

    if (comprobante == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("No hay comprobante disponible.")
                Button(onClick = {
                    navController.navigate("home_iniciada") {
                        popUpTo("home_iniciada") { inclusive = true }
                    }
                }) {
                    Text("Volver al inicio")
                }
            }
        }
        return
    }

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(80.dp))
                    Text("Pago realizado con éxito", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Text("Comprobante ${comprobante!!.idComprobante}")
                    Divider(Modifier.padding(vertical = 12.dp))
                }
            }

            item {
                Text("Cliente: ${comprobante!!.usuarioNombre}")
                Text("Correo: ${comprobante!!.usuarioEmail}")
                Text("Fecha: ${formatoFecha.format(Date(comprobante!!.fechaHoraMillis))}")
                Text("Método de pago: ${comprobante!!.metodoPago}")
                Text("Estado: ${comprobante!!.estado}")
                Divider(Modifier.padding(vertical = 12.dp))
            }

            items(comprobante!!.items) { item ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.producto.nombre} x${item.cantidad}")
                    Text(item.producto.precio)
                }
            }

            item {
                Divider(Modifier.padding(vertical = 12.dp))
                Text("Subtotal: ${String.format("$%.0f CLP", comprobante!!.subtotal)}")
                Text("Descuento: ${comprobante!!.descuentoEtiqueta} (-${String.format("$%.0f", comprobante!!.descuentoMonto)})")
                Text("Total: ${String.format("$%.0f CLP", comprobante!!.totalFinal)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Divider(Modifier.padding(vertical = 12.dp))
                Text("¡Gracias por preferir Pastelería Mr. Pastel! 🍰", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        vm.limpiarComprobante()
                        navController.navigate("home_iniciada") {
                            popUpTo("home_iniciada") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Salir")
                }
            }
        }
    }
}
