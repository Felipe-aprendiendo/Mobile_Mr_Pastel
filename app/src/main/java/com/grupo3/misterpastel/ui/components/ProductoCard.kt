package com.grupo3.misterpastel.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .height(260.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
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
            OutlinedButton(onClick = { onVerDetalles(producto) }) {
                Text("Ver Detalles")
            }
        }
    }
}
