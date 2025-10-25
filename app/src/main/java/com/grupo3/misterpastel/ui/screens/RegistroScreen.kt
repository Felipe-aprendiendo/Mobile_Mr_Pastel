package com.grupo3.misterpastel.ui.screens

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.RegistroViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Pantalla de registro conectada a RegistroViewModel.
 * Conserva la UI conocida (con fecha de nacimiento).
 * El VM valida y registra en repositorio en memoria.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroScreen(
    navController: NavController,
    vm: RegistroViewModel = viewModel()
) {
    // Estados de campos
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") } // dd/MM/yyyy

    // Estados de error locales (solo para ayudas visuales rápidas)
    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }

    val ui by vm.uiState.collectAsState()

    // Navega cuando el registro se completa
    LaunchedEffect(ui.success) {
        if (ui.success) {
            navController.navigate("home_iniciada") {
                popUpTo("registro") { inclusive = true }
            }
        }
    }

    // Scroll y ajuste de insets (para teclado)
    val scrollState = rememberScrollState()
    val view = LocalView.current
    SideEffect {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets -> insets }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo1_sf),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = false },
                label = { Text("Nombre completo") },
                isError = nombreError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError) {
                Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error)
            }

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = false },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError) {
                Text("Formato de correo no válido", color = MaterialTheme.colorScheme.error)
            }

            // Fecha de nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it; fechaError = false },
                label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = fechaError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (fechaError) {
                Text("Ingresa una fecha válida (edad mínima 13 años)", color = MaterialTheme.colorScheme.error)
            }

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = false },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError) {
                Text("La contraseña debe tener al menos 6 caracteres", color = MaterialTheme.colorScheme.error)
            }

            // Confirmación
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmError = false },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError) {
                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
            }

            // Botón: llama al ViewModel
            Button(
                onClick = {
                    // Validaciones rápidas locales (para feedback inmediato en campos)
                    var valido = true
                    if (nombre.isBlank()) { nombreError = true; valido = false }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = true; valido = false }
                    if (password.length < 6) { passwordError = true; valido = false }
                    if (password != confirmPassword) { confirmError = true; valido = false }

                    // Calcular edad a partir de la fecha
                    var edad = 0
                    try {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val fecha = LocalDate.parse(fechaNacimiento, formatter)
                        edad = Period.between(fecha, LocalDate.now()).years
                        if (edad < 13) { fechaError = true; valido = false }
                    } catch (_: DateTimeParseException) {
                        fechaError = true; valido = false
                    }

                    if (!valido) return@Button

                    // Delega validación final y registro al VM
                    vm.registrar(
                        nombre = nombre,
                        email = email,
                        password = password,
                        confirm = confirmPassword,
                        edad = edad
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.loading) "Validando..." else "Registrarse")
            }

            // Errores finales provenientes del VM
            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // Enlace a login
            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            // Volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
