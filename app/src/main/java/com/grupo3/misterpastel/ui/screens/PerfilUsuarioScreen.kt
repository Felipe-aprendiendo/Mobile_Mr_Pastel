package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grupo3.misterpastel.model.Pedido
import com.grupo3.misterpastel.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(navController: NavController, perfilViewModel: PerfilViewModel = viewModel()) {

    val usuario by perfilViewModel.usuario.observeAsState()
    val pedidos by perfilViewModel.pedidos.observeAsState(initial = emptyList())

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var edad by remember { mutableIntStateOf(0) }

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            email = it.email
            fechaNacimiento = it.fechaNacimiento
            direccion = it.direccion
            telefono = it.telefono
            edad = it.edad
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil 👤", fontWeight = FontWeight.Bold) },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            usuario?.let { user ->
                if (user.fotoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.fotoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombre.firstOrNull()?.toString()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { /* TODO: Implementar cambio de foto */ }
                ) {
                    Text("Cambiar foto")
                }

                Spacer(modifier = Modifier.height(24.dp))

                PerfilTextField("Nombre", nombre) { nombre = it }
                PerfilTextField("Correo electrónico", email) { email = it }
                PerfilTextField("Fecha de nacimiento", fechaNacimiento) { fechaNacimiento = it }
                PerfilTextField("Dirección", direccion) { direccion = it }
                PerfilTextField("Teléfono", telefono) { telefono = it }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        perfilViewModel.actualizarDatosUsuario(
                            nombre = nombre,
                            email = email,
                            edad = edad,
                            fechaNacimiento = fechaNacimiento,
                            direccion = direccion,
                            telefono = telefono,
                            password = user.password
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar cambios", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Historial de Pedidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                if (pedidos.isEmpty()) {
                    Text("No tienes pedidos anteriores.")
                } else {
                    pedidos.forEach { pedido ->
                        PedidoItem(pedido)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

            } ?: run {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun PedidoItem(pedido: Pedido) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(pedido.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold)
            Text("Fecha: $formattedDate")
            Text("Total: $${String.format("%.2f", pedido.total)}")
            Text("Estado: ${pedido.estado}")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Artículos:", fontWeight = FontWeight.SemiBold)
            pedido.items.forEach { item ->
                Text("- ${item.producto.nombre} (x${item.cantidad})")
            }
        }
    }
}

@Composable
fun PerfilTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray
        )
    )
}
