package com.grupo3.misterpastel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Producto
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductoCard(
    producto: Producto,
    onVerDetalles: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // ============================
    // 🔥 Fallback local (si URL falla)
    // ============================
    val fallbackLocalImage = producto.imagenLocal?.let { localName ->
        val id = context.resources.getIdentifier(localName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.placeholder
    } ?: R.drawable.placeholder

    // ============================
    // 💵 Formato CLP
    // ============================
    val precioFormateado = try {
        NumberFormat.getNumberInstance(Locale("es", "CL")).format(producto.precio.toInt()) + " CLP"
    } catch (e: Exception) {
        producto.precio + " CLP"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {

            // ============================
            // 🖼 AsyncImage optimizada
            // ============================
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(producto.imagen)         // URL Cloudinary
                    .crossfade(true)
                    .placeholder(fallbackLocalImage)  // fallback
                    .error(fallbackLocalImage)        // error
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = precioFormateado,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onVerDetalles(producto) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalles")
            }
        }
    }
}
