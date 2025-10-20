package com.grupo3.misterpastel.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    producto: Producto,
    carritoVM: CarritoViewModel = viewModel()
) {
    var cantidad by remember { mutableStateOf(0) } // solo para UI del selector

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = producto.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
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

                AnimatedContent(targetState = cantidad) { c ->
                    if (c == 0) {
                        Button(
                            onClick = {
                                cantidad = 1
                                // Agrega 1 item al carrito
                                carritoVM.agregar(producto, 1)
                            }
                        ) { Text("Agregar") }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val nuevo = (cantidad - 1).coerceAtLeast(0)
                                    cantidad = nuevo
                                    carritoVM.actualizarCantidad(producto.id, nuevo)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("-", fontSize = 20.sp) }

                            Text(
                                text = cantidad.toString(),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(30.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    val nuevo = cantidad + 1
                                    cantidad = nuevo
                                    carritoVM.actualizarCantidad(producto.id, nuevo)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("+", fontSize = 20.sp) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = producto.descripcion,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("carrito") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir al carrito")
            }
        }
    }
}
