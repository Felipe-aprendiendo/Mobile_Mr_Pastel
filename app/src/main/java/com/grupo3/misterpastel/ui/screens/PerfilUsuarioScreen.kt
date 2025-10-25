package com.grupo3.misterpastel.ui.screens

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grupo3.misterpastel.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(navController: NavController) {

    // TODO: En el futuro, estos datos deben venir del ViewModel del usuario autenticado
    // Ejemplo:
    // val viewModel: PerfilViewModel = viewModel()
    // val usuario by viewModel.usuario.collectAsState()
    // Por ahora, usamos datos simulados.
    var usuario by remember {
        mutableStateOf(
            Usuario(
                id = "U001",
                nombre = "Felipe Hernández",
                email = "felipe.hernandez@gmail.com",
                edad = 29,
                fechaNacimiento = "25/10/1996",
                direccion = "Av. Providencia 1234, Santiago",
                telefono = "+56 9 8888 7777",
                password = "secreto",
                fotoUrl = null // null para mostrar placeholder
            )
        )
    }

    // Variables temporales para los campos editables
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var email by remember { mutableStateOf(usuario.email) }
    var fechaNacimiento by remember { mutableStateOf(usuario.fechaNacimiento) }
    var direccion by remember { mutableStateOf(usuario.direccion) }
    var telefono by remember { mutableStateOf(usuario.telefono) }

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
            // === FOTO DE PERFIL ===
            if (usuario.fotoUrl != null) {
                // Carga la imagen remota usando Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(usuario.fotoUrl)
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
                // Placeholder circular con inicial del nombre
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

            // === BOTÓN CAMBIAR FOTO ===
            OutlinedButton(
                onClick = {
                    // TODO: Implementar cambio de foto usando PhotoPicker o GetContent()
                    // Ejemplo futuro:
                    // val launcher = rememberLauncherForActivityResult(GetContent()) { uri ->
                    //     viewModel.actualizarFotoUsuario(uri)
                    // }
                }
            ) {
                Text("Cambiar foto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === CAMPOS EDITABLES ===
            PerfilTextField("Nombre", nombre) { nombre = it }
            PerfilTextField("Correo electrónico", email) { email = it }
            PerfilTextField("Fecha de nacimiento", fechaNacimiento) { fechaNacimiento = it }
            PerfilTextField("Dirección", direccion) { direccion = it }
            PerfilTextField("Teléfono", telefono) { telefono = it }

            Spacer(modifier = Modifier.height(32.dp))

            // === BOTÓN GUARDAR CAMBIOS ===
            Button(
                onClick = {
                    // TODO: Actualizar usuario en ViewModel/Repositorio
                    // Ejemplo:
                    // viewModel.actualizarUsuario(
                    //     usuario.copy(nombre = nombre, email = email, direccion = direccion, ...)
                    // )
                    usuario = usuario.copy(
                        nombre = nombre,
                        email = email,
                        fechaNacimiento = fechaNacimiento,
                        direccion = direccion,
                        telefono = telefono
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar cambios", fontSize = 18.sp)
            }
        }
    }
}

/**
 * Campo reutilizable para los datos del perfil
 */
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
