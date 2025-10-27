package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.ui.components.ProductoCard
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel
import com.grupo3.misterpastel.viewmodel.SessionViewModel
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

    // Productos fuente
    val productos by catalogoViewModel.productos.collectAsState()

    // Estado de UI: buscador + categoría seleccionada
    var query by remember { mutableStateOf("") }
    var categoriaSel: Categoria? by remember { mutableStateOf(null) } // null = Todos

    // Filtrado combinado
    val productosFiltrados = remember(query, categoriaSel, productos) {
        productos.filter { p ->
            val okCategoria = categoriaSel?.let { p.categoria == it } ?: true
            val okTexto = query.isBlank() ||
                    p.nombre.contains(query, ignoreCase = true) ||
                    p.descripcion.contains(query, ignoreCase = true)
            okCategoria && okTexto
        }
    }

    var showLoginDialog by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Inicio de Sesión Requerido") },
            text = { Text("Para acceder al carrito y continuar con la compra, necesitas iniciar sesión.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginDialog = false
                        navController.navigate("login")
                    }
                ) {
                    Text("Iniciar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                    onClick = {
                        if (isGuest) showLoginDialog = true
                        else navController.navigate("carrito")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Ir al carrito")
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (isGuest) "Catálogo de Productos 🍰" else "Bienvenido, $nombreUsuario 🍰",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                // Buscar
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar productos") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                // Chips de categorías (incluye "Todos")
                CategoriaChipsRow(
                    categoriaSeleccionada = categoriaSel,
                    onSelect = { categoriaSel = it }
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = if (categoriaSel == null) "Todos (${productosFiltrados.size})"
                    else "${categoriaSel!!.titulo()} (${productosFiltrados.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                // Grid de productos
                if (productosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("No hay productos que coincidan con tu búsqueda.") }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = productosFiltrados,
                            key = { it.id }
                        ) { producto ->
                            ProductoCard(
                                producto = producto,
                                onVerDetalles = { p -> navController.navigate("detalle/${p.id}") },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ================= Drawer ================= */

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

/* =============== UI helpers =============== */

@Composable
private fun CategoriaChipsRow(
    categoriaSeleccionada: Categoria?,
    onSelect: (Categoria?) -> Unit
) {
    val categorias = listOf<Categoria?>(null) + Categoria.values().toList()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { cat ->
            val selected = categoriaSeleccionada == cat
            FilterChip(
                selected = selected,
                onClick = { onSelect(cat) },
                label = { Text(if (cat == null) "Todos" else cat.titulo()) },
                leadingIcon = if (selected) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

/** Si no lo tienes, puedes poner esta extensión junto a tu enum Categoria. */
private fun Categoria.titulo(): String = when (this) {
    Categoria.TORTA_CUADRADA -> "Torta cuadrada"
    Categoria.TORTA_CIRCULAR -> "Torta circular"
    Categoria.TORTA_ESPECIAL -> "Torta especial"
    Categoria.POSTRE_INDIVIDUAL -> "Postre individual"
    Categoria.PASTELERIA_TRADICIONAL -> "Pastelería tradicional"
    Categoria.PRODUCTO_SIN_AZUCAR -> "Sin azúcar"
    Categoria.PRODUCTO_SIN_GLUTEN -> "Sin gluten"
    Categoria.PRODUCTO_VEGANO -> "Vegano"
}
