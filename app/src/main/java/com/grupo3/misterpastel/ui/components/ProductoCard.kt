package com.grupo3.misterpastel.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale // <-- IMPORTANTE
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grupo3.misterpastel.model.Producto

/**
 * Tarjeta reutilizable para mostrar productos.
 * Navegación debe hacerse desde el contenedor padre (recibe onVerDetalles).
 */
@Composable
fun ProductoCard(
    producto: Producto,
    onVerDetalles: (Producto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp), // <-- Aumenté un poco la altura para el nuevo layout
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // verticalArrangement = Arrangement.Center, // <-- Quitamos esto
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
            // .padding(8.dp) // <-- Quitamos el padding global
        ) {
            // --- IMAGEN CORREGIDA ---
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // Altura fija para la imagen
                contentScale = ContentScale.Crop // Recorta para llenar
            )

            // Contenedor para el texto, con padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // Ajuste de padding
                    maxLines = 2 // Evita que texto muy largo rompa el diseño
                )
                Text(
                    text = producto.precio,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // --- LAYOUT MEJORADO ---
            // Este Spacer ocupa todo el espacio vertical sobrante,
            // empujando el botón hacia abajo.
            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { onVerDetalles(producto) },
                modifier = Modifier.padding(bottom = 12.dp) // Padding solo en el botón
            ) {
                Text("Ver Detalles")
            }
        }
    }
}