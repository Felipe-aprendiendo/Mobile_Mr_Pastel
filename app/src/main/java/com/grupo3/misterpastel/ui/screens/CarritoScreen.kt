package com.grupo3.misterpastel.ui.screens



import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController) {

    // TODO: Conectar con ViewModel del carrito
    // En el futuro, esta lista de productos no será fija,
    // sino que debe obtener desde un ViewModel que mantenga el estado global del carrito.
    // val viewModel: CarritoViewModel = viewModel()
    // val productosCarrito by viewModel.productos.collectAsState()
    // Esto permite que si el usuario agrega o quita productos desde otra pantalla,
    // la lista se actualice automáticamente aquí.

    val productosCarrito = remember {
        mutableStateListOf(
            Producto(1, "Torta Cuadrada de Chocolate", "$45.000 CLP", com.grupo3.misterpastel.R.drawable.torta_chocolate, com.grupo3.misterpastel.model.Categoria.TORTA_CUADRADA, "Deliciosa torta de chocolate."),

            Producto(2, "Empanada de Manzana", "$3.000 CLP", com.grupo3.misterpastel.R.drawable.empanada_manzana, com.grupo3.misterpastel.model.Categoria.PASTELERIA_TRADICIONAL, "Crujiente y dulce empanada artesanal.")
        )
    }




    // TODO: Manejar cantidades desde el ViewModel
    // Ahora las cantidades se guardan localmente, pero deberían venir desde el ViewModel
    // que manejará un estado del tipo `MutableStateMap<Int, Int>` o lista de objetos ProductoCarrito.
    // Ejemplo:
    // val cantidades by viewModel.cantidades.collectAsState()
    // Donde el ViewModel podría tener funciones como:
    // fun aumentarCantidad(id: Int)
    // fun disminuirCantidad(id: Int)
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    productosCarrito.forEach { producto ->
        cantidades.putIfAbsent(producto.id, 1)
    }




    // TODO: Calcular descuentos y totales desde el ViewModel
    // Los descuentos también deberían manejarse desde el ViewModel, de forma reactiva.
    // Ejemplo:
    // val descuento by viewModel.descuento.collectAsState()
    // val total by viewModel.total.collectAsState()
    // val totalConDescuento by viewModel.totalConDescuento.collectAsState()
    // De esa manera, cada vez que el usuario cambie una cantidad,
    // el total y los descuentos se recalculan automáticamente.
    var descuentoBase = 0.1f // 10% de descuento de ejemplo


    var codigoPromo by remember { mutableStateOf("") }
    var promoValida by remember { mutableStateOf(false) }

    // Si el código es "FELICES50", aplica un descuento adicional del 5%
    val descuentoAdicional = if (promoValida) 0.1f else 0f
    val descuentoTotal = descuentoBase + descuentoAdicional

    // Cálculo manual del total
    val total = productosCarrito.sumOf { producto ->
        val precio = producto.precio.replace("$", "").replace(".", "").replace(" CLP", "").trim().toInt()
        precio * (cantidades[producto.id] ?: 1)
    }
    val totalConDescuento = (total * (1 - descuentoTotal)).toInt()

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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {




            // TODO: Cargar productos dinámicamente desde el ViewModel
            // Cuando uses ViewModel, esta sección debería observar la lista `productosCarrito`
            // y actualizarse automáticamente con `LazyColumn(items = productosCarrito)`.

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productosCarrito) { producto ->
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
                            Column {
                                Text(producto.nombre, fontWeight = FontWeight.Bold)
                                Text(producto.precio, color = MaterialTheme.colorScheme.primary)
                            }

                            // === SELECTOR DE CANTIDAD ===
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = {
                                    val actual = cantidades[producto.id] ?: 1
                                    if (actual > 1) cantidades[producto.id] = actual - 1

                                    // Ejemplo futuro:
                                    // viewModel.disminuirCantidad(producto.id)
                                }) {
                                    Text("-")
                                }

                                Text(
                                    text = "${cantidades[producto.id]}",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                OutlinedButton(onClick = {
                                    val actual = cantidades[producto.id] ?: 1
                                    cantidades[producto.id] = actual + 1

                                    // Ejemplo futuro:
                                    // viewModel.aumentarCantidad(producto.id)
                                }) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === CAMPO DE CÓDIGO PROMOCIONAL ===
            OutlinedTextField(
                value = codigoPromo,
                onValueChange = { codigoPromo = it },
                label = { Text("Código promocional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    promoValida = codigoPromo.equals("FELICES50", ignoreCase = true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(if (promoValida) "Código aplicado ✅" else "Aplicar código")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Descuento aplicado: ${descuentoTotal * 100}%",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total: $total CLP",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total con descuento: $totalConDescuento CLP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // TODO: Implementar acción de pago desde ViewModel
            // Cuando integres el flujo de pago, el ViewModel debe tener una función
            // que prepare los datos del pedido o boleta.
            //
            // Ejemplo:
            // viewModel.procesarPago()
            //TODO: Redirigir a una pantalla de confirmación del pedido luego de pagar

            Button(
                onClick = {
                    // TODO: Llamar al ViewModel para procesar el pago
                    // Ejemplo:
                    // viewModel.procesarPago()
                    //
                    // Y luego navegar:
                    // navController.navigate("confirmacionPago")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Proceder al pago", fontSize = 18.sp)
            }
        }
    }
}