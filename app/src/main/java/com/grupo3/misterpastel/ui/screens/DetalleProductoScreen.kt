package com.grupo3.misterpastel.ui.screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.Producto

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetalleProductoScreen(navController: NavController, producto: Producto) {

    // Este estado interno es el que controla la cantidad seleccionada
    // Comienza en 0, lo que significa que aún no se ha agregado al carrito.
    var cantidad by remember { mutableStateOf(0) }


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


        // verticalScroll permite hacer scroll si el contenido es largo, o al menos más largo que la pantalla del susuario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // soporte para scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(250.dp)
                    .padding(8.dp)
            )

            Text(
                text = producto.nombre,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // En esta fila está el rpecio y botón para agregar al carro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = producto.precio,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Aquí ocurre la animación del botón agregar
                // AnimatedContent cambia suavemente entre dos estados de UI:
                // (1) "Agrregar al carrrito" → (2) Selector de cantdiad
                AnimatedContent(targetState = cantidad) { cantidadActual ->
                    if (cantidadActual == 0) {
                        // === BOTÓN AGREGAR AL CARRITO ===
                        Button(
                            onClick = {
                                cantidad = 1
                                // TODO: En la versión final, aquí se debería agregar al carrito
                                // Ejemplo:
                                // viewModel.agregarAlCarrito(producto, cantidad)
                            }
                        ) {
                            Text("Agregar")
                        }
                    } else {
                        // === SELECTOR DE CANTIDAD ===
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón "-" para disminuir cantidad
                            OutlinedButton(
                                onClick = {
                                    if (cantidad > 0) cantidad--
                                    // TODO: En versión final, actualizar cantidad en el carrito
                                    // viewModel.actualizarCantidad(producto, cantidad)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("-", fontSize = 20.sp)
                            }

                            // Muestra la cantidad actual
                            Text(
                                text = cantidad.toString(),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(30.dp)
                            )

                            // Botón "+" para aumentar cantidad
                            OutlinedButton(
                                onClick = {
                                    cantidad++
                                    // TODO: En versión final, actualizar cantidad en el carrito
                                    // viewModel.actualizarCantidad(producto, cantidad)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("+", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === DESCRIPCIÓN DEL PRODUCTO ===
            Text(
                text = producto.descripcion,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
