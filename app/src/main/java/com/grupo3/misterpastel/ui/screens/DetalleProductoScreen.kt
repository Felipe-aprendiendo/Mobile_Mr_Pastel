package com.grupo3.misterpastel.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.CarritoViewModel
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: Int,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel()
) {
    // siempre obtener el producto desde el ViewModel
    val producto = catalogoViewModel.getProductoById(productoId)

    if (producto == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
        return
    }

    val items by carritoViewModel.items.collectAsState()
    val cantidad = items.firstOrNull { it.producto.id == productoId }?.cantidad ?: 0

    val context = LocalContext.current

    // ==========================================
    // Fallback local si falla URL
    // ==========================================
    val fallbackLocalImage = producto.imagenLocal?.let { localName ->
        val id = context.resources.getIdentifier(localName, "drawable", context.packageName)
        if (id != 0) id else R.drawable.placeholder
    } ?: R.drawable.placeholder

    // ==========================================
    // Formateo CLP
    // ==========================================
    val precioFormateado = try {
        NumberFormat.getNumberInstance(Locale("es", "CL"))
            .format(producto.precio.toInt()) + " CLP"
    } catch (e: Exception) {
        producto.precio + " CLP"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = producto.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ================================
            // Imagen Cloudinary con Coil
            // ================================
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(producto.imagen)     // URL Cloudinary
                    .crossfade(true)
                    .placeholder(fallbackLocalImage)
                    .error(fallbackLocalImage)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(260.dp)
                    .padding(8.dp)
            )

            Text(
                text = producto.nombre,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Precio formateado
                Text(
                    text = precioFormateado,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Botones de cantidad
                AnimatedContent(targetState = cantidad) { cantidadActual ->
                    if (cantidadActual == 0) {
                        Button(onClick = { carritoViewModel.agregar(producto, 1) }) {
                            Text("Agregar")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    carritoViewModel.actualizarCantidad(
                                        producto.id,
                                        cantidadActual - 1
                                    )
                                }
                            ) { Text("-", fontSize = 22.sp) }

                            Text(
                                text = cantidadActual.toString(),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(30.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    carritoViewModel.actualizarCantidad(
                                        producto.id,
                                        cantidadActual + 1
                                    )
                                }
                            ) { Text("+", fontSize = 22.sp) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = producto.descripcion,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                lineHeight = 26.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
