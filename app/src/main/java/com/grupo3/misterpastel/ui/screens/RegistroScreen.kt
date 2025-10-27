package com.grupo3.misterpastel.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.RegistroViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Añadimos OptIn para el DatePicker de Material 3
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroScreen(
    navController: NavController,
    registroViewModel: RegistroViewModel = viewModel()
) {
    // Leemos el estado del formulario (uiState) desde el ViewModel
    val uiState by registroViewModel.uiState.collectAsState()

    // El estado del proceso de registro (Loading, Error, Success) se mantiene
    val registrationState by registroViewModel.registrationState.collectAsState()

    // Estado local solo para controlar si el calendario se muestra ---
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Reacción a cambios del VM para navegar
    LaunchedEffect(registrationState) {
        if (registrationState is RegistroViewModel.RegistrationState.Success) {
            navController.navigate("home_iniciada") {
                popUpTo("registro") { inclusive = true }
            }
        }
    }

    val scrollState = rememberScrollState()

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
                painter = painterResource(id = R.drawable.logo_claro),
                contentDescription = "Logo Pastelería 1000 Sabores",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Todos los OutlinedTextField leen y escriben en el ViewModel
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = registroViewModel::onNombreChange,
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = registroViewModel::onEmailChange,
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Implementación del DatePickerDialog ---
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Convertimos los milisegundos a la fecha en formato "dd/MM/yyyy"
                                val fechaFormateada = convertMillisToDate(millis)
                                registroViewModel.onFechaNacimientoChange(fechaFormateada)
                            }
                            showDatePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Campo de fecha (no editable, abre el calendario) ---
            OutlinedTextField(
                value = uiState.fechaNacimiento,
                onValueChange = { /* No hace nada, es de solo lectura */ },
                label = { Text("Fecha de nacimiento") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                readOnly = true, // El usuario no puede escribir
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }, // Abre el calendario
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Abrir calendario",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                }
            )

            OutlinedTextField(
                value = uiState.direccion,
                onValueChange = registroViewModel::onDireccionChange,
                label = { Text("Dirección") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = registroViewModel::onTelefonoChange,
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = registroViewModel::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = registroViewModel::onConfirmPasswordChange,
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Captura el estado actual en una variable local
            val regState = registrationState

            if (regState is RegistroViewModel.RegistrationState.Error) {
                Text(
                    text = regState.message, // <-- Usa la variable local 'regState'
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val isLoading = registrationState is RegistroViewModel.RegistrationState.Loading

            Button(
                onClick = {
                    // Llamada simple sin parámetros ---
                    registroViewModel.register()
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Registrando..." else "Registrarse")
            }

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

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

//Función Helper para formatear la fecha del calendario ---
@RequiresApi(Build.VERSION_CODES.O)
private fun convertMillisToDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}