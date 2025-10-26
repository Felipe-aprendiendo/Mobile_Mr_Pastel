package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel
import com.grupo3.misterpastel.viewmodel.SessionViewModel
import com.grupo3.misterpastel.ui.components.ProductoCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSesionIniciada(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val usuarioActual by sessionViewModel.usuarioActual.collectAsState()
    val nombreUsuario = usuarioActual?.nombre ?: "Cliente"
    val isGuest = usuarioActual == null

    val productos by catalogoViewModel.productos.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxSize()
            ) {
                DrawerContent(navController, nombreUsuario, sessionViewModel, isGuest)
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("carrito") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Ir al carrito")
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text(if (isGuest) "Catálogo de Productos 🍰" else "Bienvenido, $nombreUsuario 🍰") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                if (productos.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = productos,
                            key = { it.id } // clave estable para mejor performance
                        ) { producto ->
                            ProductoCard(
                                producto = producto,
                                onVerDetalles = { p -> navController.navigate("detalle/${p.id}") },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavController,
    nombreUsuario: String,
    sessionViewModel: SessionViewModel,
    isGuest: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DrawerHeader(nombreUsuario, isGuest)
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        if (isGuest) {
            DrawerItem("👤 Iniciar Sesión") { navController.navigate("login") }
            DrawerItem("✍️ Registrarse") { navController.navigate("registro") }
        } else {
            DrawerItem("👤 Mi cuenta") { navController.navigate("perfil") }
            DrawerItem("🧾 Mis pedidos") { navController.navigate("pedidos") }
            Spacer(modifier = Modifier.weight(1f))
            DrawerItem("🚪 Cerrar sesión") {
                sessionViewModel.logout()
                navController.navigate("home") {
                    popUpTo("home_iniciada") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun DrawerHeader(nombreUsuario: String, isGuest: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_oscuro),
            contentDescription = "Logo Mr. Pastel",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isGuest) "Modo Invitado" else "Bienvenido, $nombreUsuario",
            fontSize = 35.sp,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
