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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.model.Categoria
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSesionIniciada(navController: NavController) {

    //El estado del drawer controla si el menú lateral está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // TODO: En la versión final el nombre del usuario debe venir desde viewModel
    // val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    //Pese a que el login no lo implementemos, de acuerdo a lo que dijo el profe, hay que buscar la forma de traer el nombre del usuario
    //del viewModel o dejar un mensaje genérico
    val nombreUsuario = "Felipe"

    // En la versión definitiva, los productos deberían venir desde el ViewModel. Por eso traje solo algunos productos del catálogo, luego ya se deben traer todos o al menos (creo) uno de cada categoria
    // val productos by viewModel.productos.collectAsState()
    val productos = listOf(
        Producto(
            1,
            "Torta Cuadrada de Chocolate",
            "$45.000 CLP",
            R.drawable.torta_chocolate,
            Categoria.TORTA_CUADRADA,
            "Bizcocho húmedo de cacao con relleno de ganache y cobertura de chocolate amargo. Ideal para celebraciones y amantes del sabor intenso."
        ),
        Producto(
            2,
            "Torta Circular de Frutas",
            "$50.000 CLP",
            R.drawable.torta_frutas,
            Categoria.TORTA_CIRCULAR,
            "Base esponjosa con crema pastelera y frutas frescas de temporada. Equilibrio perfecto entre dulzura y frescura natural."
        ),
        Producto(
            3,
            "Mousse de Chocolate",
            "$5.000 CLP",
            R.drawable.mousse_chocolate,
            Categoria.POSTRE_INDIVIDUAL,
            "Postre cremoso de textura ligera con cacao premium, decorado con virutas de chocolate. Perfecto para disfrutar después de una comida."
        ),
        Producto(
            4,
            "Torta Especial Cumpleaños",
            "$55.000 CLP",
            R.drawable.torta_cumple,
            Categoria.TORTA_ESPECIAL,
            "Diseño personalizado con crema y fondant. Sabor a vainilla o chocolate según preferencia. Ideal para cumpleaños y celebraciones familiares."
        ),
        Producto(
            5,
            "Empanada de Manzana",
            "$3.000 CLP",
            R.drawable.empanada_manzana,
            Categoria.PASTELERIA_TRADICIONAL,
            "Masa crujiente rellena con compota de manzana y canela. Dulce artesanal perfecto para acompañar con té o café."
        )
    )

    // ModalNavigationDrawer envuelve toda la UI (scaffold = barra superior + contenido + carrito) y provee el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxSize()
            ) {
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
                // Esto habilita el scroll vertical si el contenido es más largo que el largo de la pantalla
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productos) { producto ->
                        ProductoCard(producto, navController)
                    }
                }
            }
        }
    }
}






// Aquí se define el contenido del drawer (menú lateral)
// Contiene el encabezado (logo y saludo) y las opciones de navegación
@Composable
fun DrawerContent(navController: NavController, nombreUsuario: String) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background) // se asegura que el fondo siga el tema
    ) {
        DrawerHeader(nombreUsuario)
        HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
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


// Reutilizable para cada opción del Drawer (Mi cuenta, Mis pedidos, Cerrar sesión, etc.)
//Si queremos agregar algo más a ese menú es usando este composable, para ver cómo se usa, buscarlo dentro del drawercontent
@Composable
fun DrawerItem(
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



// Cabecera del menú lateral (drawerheader)
// Muestra el logo y el nombre del usuario en la parte superior del menú lateral
@Composable
fun DrawerHeader(nombreUsuario: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.logo_oscuro),
            contentDescription = "Logo Mr. Pastel",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Texto de bienvenida
        Text(
            text = "Bienvenido, $nombreUsuario",
            fontSize = 35.sp,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}





// Este composable arma las tarjetas de presentación de cada producto
// Muestra cada producto con su imagen, nombre, precio y botón de acción
@Composable
fun ProductoCard(producto: Producto, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // centra los hijos
            verticalArrangement = Arrangement.Center, // centra verticalmente
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
                .fillMaxSize() // asegura que la columna ocupe toda la tarjeta
        ) {
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            // Texto del nombre centrado horizontalmente
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            // Texto del precio centrado horizontalmente
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

            OutlinedButton(onClick = {
                // TODO: Aquí se debería navegar a la pantalla de detalle del producto
                navController.navigate("detalle/${producto.id}")
            }) {

                Text("Ver Detalles")
            }
        }
    }
}

