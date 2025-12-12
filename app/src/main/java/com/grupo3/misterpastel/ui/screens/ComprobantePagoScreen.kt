package com.grupo3.misterpastel.ui.screens

import android.annotation.SuppressLint
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
import com.grupo3.misterpastel.repository.DescuentoAplicado
import com.grupo3.misterpastel.viewmodel.PagoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ComprobantePagoScreen(navController: NavController) {
    // Obtener el mismo PagoViewModel compartido desde "carrito"
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("carrito")
    }
    val vm: PagoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
    val comprobante by vm.comprobante.collectAsState()
    val descuentosAplicados by vm.descuentosAplicados.collectAsState()


    // Validación temprana
    if (comprobante == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("No hay comprobante disponible.")
                Button(onClick = {
                    navController.navigate("home_iniciada") {
                        popUpTo("home_iniciada") { inclusive = false }
                        launchSingleTop = true
                    }
                }) {
                    Text("Volver al inicio")
                }
            }
        }
        return
    }

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val nf = remember { NumberFormat.getNumberInstance(Locale("es", "CL")) }
    val scope = rememberCoroutineScope()

    val itemsComprobante = comprobante?.items ?: emptyList()
    val montosDescuentos = remember(comprobante, descuentosAplicados) {
        calcularMontosDescuentosAcumulados(
            subtotal = comprobante!!.subtotal,
            descuentos = descuentosAplicados
        )
    }
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        "Pago realizado con éxito",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
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

            // Productos comprados (verificados)
            if (itemsComprobante.isEmpty()) {
                item {
                    Text(
                        "No se encontraron productos en este comprobante.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(itemsComprobante, key = { it.producto.id }) { item ->
                    val precioNumerico = try {
                        item.producto.precio.toDouble()
                    } catch (e: Exception) {
                        0.0
                    }

                    val subtotalProducto = precioNumerico * item.cantidad
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.producto.nombre} x${item.cantidad}")
                        Text("${nf.format(subtotalProducto)} CLP",
                            textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                }
            }

            item {
                Divider(Modifier.padding(vertical = 12.dp))

                // Subtotal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal:")
                    Text(
                        "${nf.format(comprobante!!.subtotal)} CLP",
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
                Divider(Modifier.padding(vertical = 12.dp))


                // Descuentos uno por uno (acumulativos)
                descuentosAplicados.forEachIndexed { index, descuento ->
                    val porcentajeStr = (descuento.porcentaje * 100).toInt()
                    val montoDesc = montosDescuentos.getOrNull(index) ?: 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Descuento: $porcentajeStr% (${descuento.etiqueta})",
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "-${nf.format(montoDesc)} CLP",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // Total Descuentos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Descuentos:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "-${nf.format(comprobante!!.descuentoMonto)} CLP",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // Total Final
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${nf.format(comprobante!!.totalFinal)} CLP",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                Divider(Modifier.padding(vertical = 12.dp))
                Text(
                    "¡Gracias por preferir Pastelería Mr. Pastel! 🍰",
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(20.dp))

                // Botón: volver al inicio
                Button(
                    onClick = {
                        val success = navController.popBackStack("home_iniciada", false)
                        if (!success) {
                            navController.navigate("home_iniciada") { launchSingleTop = true }
                        }
                        scope.launch {
                            delay(300)
                            vm.limpiarComprobante()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}
private fun calcularMontosDescuentosAcumulados(
    subtotal: Double,
    descuentos: List<DescuentoAplicado>
): List<Double> {
    var base = subtotal
    val montos = mutableListOf<Double>()

    for (d in descuentos) {
        val monto = base * d.porcentaje
        montos.add(monto)
        base -= monto
    }
    return montos
}