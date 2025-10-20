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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
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
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.ui.components.ProductoCard
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal cuando el usuario ya inició sesión.
 * Muestra catálogo, Drawer lateral y FAB para ir al carrito.
 * - Toma los productos desde CatalogoViewModel (StateFlow).
 * - Reutiliza el componente ProductoCard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSesionIniciada(navController: NavController) {
    // Estado del Drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // (Opcional) Nombre de usuario. Si tienes sesión real, léelo desde UsuarioRepository.usuarioActual
    val nombreUsuario = "Cliente"

    // === Productos desde ViewModel, no hardcodeados ===
    val catalogoVM: CatalogoViewModel = viewModel()
    val catalogoState by catalogoVM.uiState.collectAsState()
    val productos = catalogoState.productos

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {
                DrawerContent(navController, nombreUsuario)
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
                    title = { Text("Bienvenido, $nombreUsuario 🍰") },
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
                // Grid adaptable por ancho disponible
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productos) { producto ->
                        ProductoCard(producto = producto) { p ->
                            navController.navigate("detalle/${p.id}")
                        }
                    }
                }
            }
        }
    }
}

/* ========================
   Componentes del Drawer
   ======================== */

@Composable
private fun DrawerContent(navController: NavController, nombreUsuario: String) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DrawerHeader(nombreUsuario)
        HorizontalDivider(
            thickness = 10.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        DrawerItem("👤 Mi cuenta") { navController.navigate("perfil") }
        DrawerItem("🧾 Mis pedidos") { navController.navigate("pedidos") }
        Spacer(modifier = Modifier.weight(1f))
        DrawerItem("🚪 Cerrar sesión") {
            navController.navigate("home") {
                popUpTo("home_iniciada") { inclusive = true }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        fontSize = 20.sp,
        color = color
    )
}

@Composable
private fun DrawerHeader(nombreUsuario: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo1_sf),
            contentDescription = "Logo Mr. Pastel",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bienvenido, $nombreUsuario",
            fontSize = 35.sp,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center
        )
    }
}
