package com.grupo3.misterpastel.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.CarritoItem
import com.grupo3.misterpastel.model.EstadoPedido
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(navController: NavController) {

    // TODO: En el futuro, esto debe venir desde un ViewModel (por ejemplo: viewModel.getPedidos(userId))
    // Por ahora, simulamos algunos pedidos pagados.
    val pedidos = remember {
        listOf(
            Pedido(
                id = "PED001",
                userId = "U001",
                fecha = System.currentTimeMillis() - 86400000L,
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            1,
                            "Torta Chocolate",
                            "$45.000 CLP",
                            com.grupo3.misterpastel.R.drawable.torta_chocolate,
                            com.grupo3.misterpastel.model.Categoria.TORTA_CUADRADA,
                            "Deliciosa torta de chocolate artesanal."
                        ),
                        cantidad = 1
                    ),
                    CarritoItem(
                        producto = Producto(
                            2,
                            "Mousse Frutilla",
                            "$3.000 CLP",
                            com.grupo3.misterpastel.R.drawable.mousse_chocolate,
                            com.grupo3.misterpastel.model.Categoria.POSTRE_INDIVIDUAL,
                            "Postre suave y cremoso de frutilla."
                        ),
                        cantidad = 2
                    )
                ),
                total = 51000.0,
                estado = EstadoPedido.ENTREGADO
            ),
            Pedido(
                id = "PED002",
                userId = "U001",
                fecha = System.currentTimeMillis() - 604800000L,
                items = listOf(
                    CarritoItem(
                        producto = Producto(
                            3,
                            "Torta Cumpleaños",
                            "$55.000 CLP",
                            com.grupo3.misterpastel.R.drawable.torta_cumple,
                            com.grupo3.misterpastel.model.Categoria.TORTA_ESPECIAL,
                            "Decoración personalizada y sabor a elección."
                        ),
                        cantidad = 1
                    )
                ),
                total = 55000.0,
                estado = EstadoPedido.EN_PREPARACION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos 🧾", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (pedidos.isEmpty()) {
                Text(
                    text = "Aún no has realizado pedidos 🍰",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pedidos) { pedido ->
                        PedidoCard(pedido)
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido) {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaFormateada = formato.format(Date(pedido.fecha))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cabecera con número de pedido y fecha
            Text(
                text = "Pedido Nº ${pedido.id}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text("Fecha: $fechaFormateada")

            // Estado del pedido — usa colores distintos según estado
            Text(
                text = "Estado: ${pedido.estado.name}",
                color = when (pedido.estado) {
                    EstadoPedido.PENDIENTE -> MaterialTheme.colorScheme.tertiary
                    EstadoPedido.EN_PREPARACION -> MaterialTheme.colorScheme.secondary
                    EstadoPedido.LISTO_PARA_RECOGER -> MaterialTheme.colorScheme.primary
                    EstadoPedido.ENTREGADO -> MaterialTheme.colorScheme.primary
                    EstadoPedido.FINALIZADO -> MaterialTheme.colorScheme.error
                }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Listado de productos dentro del pedido
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                pedido.items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${item.producto.nombre} x${item.cantidad}")
                        Text(item.producto.precio)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Total final
            Text(
                text = "Total: $${pedido.total}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}