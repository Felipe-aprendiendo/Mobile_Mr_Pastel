package com.grupo3.misterpastel.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo3.misterpastel.ui.screens.*
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash
        composable("splash") { com.grupo3.misterpastel.ui.screens.splash.SplashScreen(navController) }

        // Home inicial
        composable("home") { HomeScreen(navController) }

        // Login y Registro
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }

        // Home con sesión iniciada (catálogo resumido + drawer + FAB carrito)
        composable("home_iniciada") { HomeSesionIniciada(navController) }

        // *** NUEVAS RUTAS ***
        composable("catalogo") { CatalogoScreen(navController) }
        composable("carrito") { CarritoScreen(navController) }
        composable("pedidos") { PedidoScreen(navController) }
        composable("perfil") { PerfilUsuarioScreen(navController) }

        // Detalle producto (usa CatalogoViewModel para buscar por ID)
        composable("detalle/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: 0
            val catalogoVM: CatalogoViewModel = viewModel()
            val producto = catalogoVM.getProductoById(productoId)
            if (producto != null) {
                DetalleProductoScreen(navController, producto) // ahora añade al carrito usando VM dentro
            } else {
                // Fallback: si no existe el producto, vuelve a catálogo
                navController.popBackStack()
            }
        }
    }
}
