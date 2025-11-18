package com.grupo3.misterpastel.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * NavHost reutilizable para pruebas UI con Jetpack Compose.
 *
 * Se usa para inyectar fácilmente pantallas de prueba y permitir navegación real
 * con NavController durante los tests de interfaz.
 */
@Composable
fun TestNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    homeSesionIniciadaContent: @Composable () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // Pantalla bajo prueba
        composable("home_iniciada") {
            homeSesionIniciadaContent()
        }

        // Pantalla detalle real o fake
        composable(
            route = "detalle/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) {
            androidx.compose.material3.Text("DetalleScreenTest")
        }

        // Puedes agregar más pantallas de prueba si en el futuro las necesitas:
        // composable("carrito") { Text("CarritoTest") }
    }
}
