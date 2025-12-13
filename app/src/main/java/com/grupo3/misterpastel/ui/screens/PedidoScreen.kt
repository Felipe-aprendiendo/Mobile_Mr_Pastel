package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.model.EstadoPedido
import com.grupo3.misterpastel.model.precioDouble
import com.grupo3.misterpastel.repository.UsuarioRepository
import com.grupo3.misterpastel.repository.remote.RetrofitInstance
import com.grupo3.misterpastel.viewmodel.PedidoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(
    navController: NavController,
    pedidoViewModel: PedidoViewModel = viewModel()
) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepository.getInstance(context, RetrofitInstance.api) }
    val usuarioActual by usuarioRepo.usuarioActual.collectAsState()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            pedidoViewModel.setUserId(usuarioActual!!.id)
        }
    }

    val pedidos by pedidoViewModel.pedidos.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos", fontWeight = FontWeight.Bold) },
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
            when {
                usuarioActual == null -> {
                    Text(
                        text = "Inicia sesión para ver tus pedidos.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                pedidos.isEmpty() -> {
                    Text(
                        text = "Aún no has realizado pedidos.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(pedidos, key = { it.id }) { pedido ->
                            PedidoCard(pedido)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaFormateada = formatoFecha.format(Date(pedido.fecha))

    val nf = remember { NumberFormat.getNumberInstance(Locale("es", "CL")) }

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
            Text(
                text = "Pedido Nº ${pedido.id}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text("Fecha: $fechaFormateada")

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

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                pedido.items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${item.producto.nombre} x${item.cantidad}")
                        Text("${nf.format(item.producto.precioDouble())} CLP")
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Text(
                text = "Total: ${nf.format(pedido.total)} CLP",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
