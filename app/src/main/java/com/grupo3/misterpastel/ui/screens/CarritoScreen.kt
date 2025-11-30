package com.grupo3.misterpastel.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.subtotal
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.viewmodel.CarritoViewModel
import com.grupo3.misterpastel.viewmodel.PagoViewModel
import com.grupo3.misterpastel.viewmodel.PedidoViewModel
import java.text.NumberFormat
import java.util.*

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    vm: CarritoViewModel = viewModel(),
    pedidoVM: PedidoViewModel = viewModel()
) {
    // Contexto necesario para acceder a Room
    val context = LocalContext.current

    // LÓGICA DE CARGA DE DATOS DEL USUARIO AL INICIO DE LA PANTALLA
    LaunchedEffect(Unit) {
        val usuarioRepo = UsuarioRepository.getInstance(context)
        val usuario = usuarioRepo.usuarioActual.value

        if (usuario != null) {
            vm.actualizarDatosUsuario(usuario.edad, usuario.email)
        } else {
            vm.actualizarDatosUsuario(null, null)
        }
    }

    // PagoViewModel compartido entre pantallas
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("carrito")
    }
    val pagoVM: PagoViewModel = viewModel(parentEntry)

    val items by vm.items.collectAsState()
    val coupon by vm.coupon.collectAsState()

    var codigoPromo by remember { mutableStateOf(coupon ?: "") }
    val nf = remember { NumberFormat.getNumberInstance(Locale("es", "CL")) }

    val totalBruto = remember(items) { vm.totalBruto() }
    val totalConDesc = remember(items, coupon, vm.edadUsuario, vm.emailUsuario) {
        vm.totalConDescuento()
    }

    // Obtenemos la lista de descuentos aplicados (DUOC, edad, cupón)
    val descuentosAplicados = remember(items, coupon, vm.edadUsuario, vm.emailUsuario) {
        vm.obtenerDescuentosAplicados()
    }

    // Calculamos el monto total de descuento para mostrar
    val descuentoMontoTotal = totalBruto - totalConDesc

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
                                        vm.actualizarCantidad(
                                            item.producto.id,
                                            item.cantidad - 1
                                        )
                                    }) { Text("-") }

                                    Text(
                                        "${item.cantidad}",
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    OutlinedButton(onClick = {
                                        vm.actualizarCantidad(
                                            item.producto.id,
                                            item.cantidad + 1
                                        )
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
                        else "Aplicar código"
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        "Total bruto: ${nf.format(totalBruto)} CLP",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // MOSTRAR TODOS LOS DESCUENTOS APLICADOS
                    descuentosAplicados.forEach { descuento ->
                        val porcentajeStr = (descuento.porcentaje * 100).toInt()
                        Text(
                            "Descuento aplicado: $porcentajeStr% (${descuento.etiqueta})",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Mostrar el monto total de descuento
                    if (descuentoMontoTotal > 0.0) {
                        Text(
                            "Descuento total: -${nf.format(descuentoMontoTotal)} CLP",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (descuentosAplicados.isEmpty()) {
                        Text(
                            "Descuento aplicado: 0%",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        "Total a pagar: ${nf.format(totalConDesc)} CLP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Botón de pago con validación de sesión persistente
                Button(
                    onClick = {
                        val usuarioRepo = UsuarioRepository.getInstance(context)
                        val usuario = usuarioRepo.usuarioActual.value

                        if (usuario != null && items.isNotEmpty()) {
                            // 1) Tomamos la lista de descuentos ANTES de vaciar carrito/cupón
                            val descuentos = vm.obtenerDescuentosAplicados()

                            // 2) Generamos el comprobante (CarritoRepository aquí hace vaciar())
                            val comprobante = vm.confirmarPedidoYGuardarComprobante(
                                usuarioNombre = usuario.nombre,
                                usuarioEmail = usuario.email,
                                edadUsuario = usuario.edad
                            )

                            // 3) Guardamos el pedido y pasamos datos al PagoViewModel
                            pedidoVM.registrarPedidoDesdeComprobante(usuario.id, comprobante)
                            pagoVM.setComprobanteYDescuentos(comprobante, descuentos)

                            navController.navigate("procesando_pago")
                        } else {
                            // Si no hay sesión activa, redirige al login
                            vm.vaciar()
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
