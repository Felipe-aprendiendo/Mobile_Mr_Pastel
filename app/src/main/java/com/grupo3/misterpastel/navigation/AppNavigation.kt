package com.grupo3.misterpastel.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grupo3.misterpastel.ui.screens.CarritoScreen
import com.grupo3.misterpastel.ui.screens.ComprobantePagoScreen
import com.grupo3.misterpastel.ui.screens.DetalleProductoScreen
import com.grupo3.misterpastel.ui.screens.HomeScreen
import com.grupo3.misterpastel.ui.screens.HomeSesionIniciada
import com.grupo3.misterpastel.ui.screens.LoginScreen
import com.grupo3.misterpastel.ui.screens.PagoProcesandoScreen
import com.grupo3.misterpastel.ui.screens.PedidoScreen
import com.grupo3.misterpastel.ui.screens.PerfilUsuarioScreen
import com.grupo3.misterpastel.ui.screens.RegistroScreen
import com.grupo3.misterpastel.ui.screens.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("registro") {
            RegistroScreen(navController)
        }

        composable("home_iniciada") {
            HomeSesionIniciada(navController)
        }

        composable(
            route = "detalle/{productoId}",
            arguments = listOf(
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            DetalleProductoScreen(navController, productoId)
        }

        composable("carrito") {
            CarritoScreen(navController)
        }

        composable("pedidos") {
            PedidoScreen(navController)
        }

        composable("perfil") {
            PerfilUsuarioScreen(navController)
        }

        composable("procesando_pago") {
            PagoProcesandoScreen(navController)
        }

        composable("comprobante_pago") {
            ComprobantePagoScreen(navController)
        }
    }
}
