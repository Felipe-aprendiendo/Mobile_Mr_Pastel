package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.viewmodel.PerfilViewModel

// Opt-in porque TopAppBar es experimental en tu versión de Material3
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Edición simple del perfil del usuario en sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(navController: NavController, vm: PerfilViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    var nombre by remember { mutableStateOf(state.nombre) }
    var email by remember { mutableStateOf(state.email) }
    var edadText by remember { mutableStateOf(state.edad.toString()) }
    var fotoUrl by remember { mutableStateOf(state.fotoUrl ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edadText,
                onValueChange = { edadText = it },
                label = { Text("Edad") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fotoUrl,
                onValueChange = { fotoUrl = it },
                label = { Text("Foto URL (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
            if (state.guardado) {
                Text("Cambios guardados ✅", color = MaterialTheme.colorScheme.primary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text("Volver")
                }
                Button(
                    onClick = {
                        val edad = edadText.toIntOrNull() ?: 0
                        vm.guardar(nombre, email, edad, fotoUrl.ifBlank { null })
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
