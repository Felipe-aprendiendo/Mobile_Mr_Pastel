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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.Period

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroScreen(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") } // Nuevo campo

    // Estas variables son para validación de los campos del formularro
    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) } // Nuevo error
    var generalError by remember { mutableStateOf<String?>(null) }


    // Esto permite que el contenido sea desplazable y no se oculte cuando aparece el teclado
    //o cuando se apilan muchos elementos, como lso mensajes de error
    val scrollState = rememberScrollState()
    val view = LocalView.current
    val density = LocalDensity.current
    SideEffect {
        // Este ajuste asegura que la pantalla se recalcule cuando el teclado aparece
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            insets
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        //Se agrega scroll vertical para evitar que los elementos desaparezcan al crecer el contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_claro),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Nombre y validación del nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = { Text("Nombre completo") },
                isError = nombreError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError) {
                Text(
                    text = "El nombre no puede estar vacío",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Correo y validación del correo
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError) {
                Text(
                    text = "Formato de correo no válido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Fecha de nacimiento y su validación
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {
                    fechaNacimiento = it
                    fechaError = false
                },
                label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = fechaError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (fechaError) {
                Text(
                    text = "Ingresa una fecha válida (edad mínima 13 años)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Contraseña y validación de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError) {
                Text(
                    text = "La contraseña debe tener al menos 6 caracteres",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Lo mismo pero para la confirmación de contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmError = false
                },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Si hay más de un error, se muestra solo uno
            if (generalError != null) {
                Text(
                    text = generalError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Aquí sucede la magia, se ejecutan todas las validaciones
            Button(
                onClick = {
                    var valido = true

                    // --- Validaciones básicas ---
                    if (nombre.isBlank()) {
                        nombreError = true
                        valido = false
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = true
                        valido = false
                    }

                    // --- Validar fecha de nacimiento ---
                    try {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val fecha = LocalDate.parse(fechaNacimiento, formatter)
                        val edad = Period.between(fecha, LocalDate.now()).years
                        if (edad < 13) {
                            fechaError = true
                            valido = false
                        }
                    } catch (e: DateTimeParseException) {
                        fechaError = true
                        valido = false
                    }

                    // --- Validar contraseñas ---
                    if (password.length < 6) {
                        passwordError = true
                        valido = false
                    }
                    if (password != confirmPassword) {
                        confirmError = true
                        valido = false
                    }

                    // --- Resultado final ---
                    if (valido) {
                        //* TODO: implementar la invocación del viewModel *//

                        // viewModel.registrarUsuario(nombre, email, password)
                        // Por ahora, simplemente navega a HomeSesionIniciada

                        navController.navigate("home_iniciada") {
                            popUpTo("registro") { inclusive = true }
                        }
                    } else {
                        generalError = "Por favor corrige los errores antes de continuar"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            // ===== ENLACE A LOGIN =====
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            // ===== BOTÓN VOLVER =====
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }

            // Espacio adicional para que el último botón no quede pegado al borde inferior
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

