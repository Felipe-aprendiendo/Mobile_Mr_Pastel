package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.subtotal
import com.grupo3.misterpastel.viewmodel.CarritoViewModel
import com.grupo3.misterpastel.viewmodel.PagoViewModel
import com.grupo3.misterpastel.viewmodel.PedidoViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    vm: CarritoViewModel = viewModel(),
    pagoVM: PagoViewModel = viewModel(),
    pedidoVM: PedidoViewModel = viewModel() // 🆕 se agrega aquí
) {
    val items by vm.items.collectAsState()
    val coupon by vm.coupon.collectAsState()

    var codigoPromo by remember { mutableStateOf(coupon ?: "") }
    val nf = remember { NumberFormat.getNumberInstance(Locale("es", "CL")) }

    val totalBruto = remember(items) { vm.totalBruto() }
    val totalConDesc = remember(items, coupon, vm.edadUsuario, vm.emailUsuario) { vm.totalConDescuento() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu carrito 🛒", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no agregas productos.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.producto.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.producto.nombre, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${nf.format(item.subtotal())} CLP",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(onClick = {
                                        vm.actualizarCantidad(item.producto.id, item.cantidad - 1)
                                    }) { Text("-") }

                                    Text(
                                        text = "${item.cantidad}",
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    OutlinedButton(onClick = {
                                        vm.actualizarCantidad(item.producto.id, item.cantidad + 1)
                                    }) { Text("+") }

                                    TextButton(onClick = { vm.eliminar(item.producto.id) }) {
                                        Text("Quitar")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Código promocional
                OutlinedTextField(
                    value = codigoPromo,
                    onValueChange = { codigoPromo = it },
                    label = { Text("Código promocional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { vm.setCupon(codigoPromo) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        if ((coupon ?: "").equals("FELICES50", true))
                            "Código aplicado ✅"
                        else
                            "Aplicar código"
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Totales
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total bruto: ${nf.format(totalBruto)} CLP",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    val etiquetaDesc = when {
                        (vm.emailUsuario ?: "").endsWith("@duocuc.cl", true) -> "Descuento aplicado: 100% (DUOC)"
                        (vm.edadUsuario ?: 0) >= 50 -> "Descuento aplicado: 50% (edad)"
                        (coupon ?: "").equals("FELICES50", true) -> "Descuento aplicado: 10% (cupón)"
                        else -> "Descuento aplicado: 0%"
                    }

                    Text(
                        text = etiquetaDesc,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Total a pagar: ${nf.format(totalConDesc)} CLP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ✅ Botón de pago actualizado con integración de PedidoViewModel
                Button(
                    onClick = {
                        val usuario = com.grupo3.misterpastel.repository.UsuarioRepository.usuarioActual.value
                        if (usuario != null && items.isNotEmpty()) {
                            // 1️⃣ Genera comprobante de pago
                            val comprobante = vm.confirmarPedidoYGuardarComprobante(
                                usuarioNombre = usuario.nombre,
                                usuarioEmail = usuario.email,
                                edadUsuario = usuario.edad
                            )

                            // 2️⃣ Registra el pedido en Room
                            pedidoVM.registrarPedidoDesdeComprobante(usuario.id, comprobante)

                            // 3️⃣ Notifica al PagoViewModel para mostrar comprobante
                            pagoVM.setComprobante(comprobante)

                            // 4️⃣ Navega a splash "procesando pago"
                            navController.navigate("procesando_pago")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    enabled = items.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Proceder al pago", fontSize = 18.sp)
                }
            }
        }
    }
}
