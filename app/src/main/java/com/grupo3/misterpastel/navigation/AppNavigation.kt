package com.grupo3.misterpastel.navigation



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.ui.screens.DetalleProductoScreen
import com.grupo3.misterpastel.ui.screens.HomeScreen
import com.grupo3.misterpastel.ui.screens.HomeSesionIniciada
import com.grupo3.misterpastel.ui.screens.LoginScreen
import com.grupo3.misterpastel.ui.screens.RegistroScreen
import com.grupo3.misterpastel.ui.screens.splash.SplashScreen
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.ui.screens.CarritoScreen
import com.grupo3.misterpastel.ui.screens.PedidoScreen
import com.grupo3.misterpastel.ui.screens.PerfilUsuarioScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable ("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }
        composable("home_iniciada") { HomeSesionIniciada(navController) }
        composable(
            route = "detalle/{productoId}"
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toInt() ?: 0

            // TODO: En el futuro, aquí se debe obtener el producto desde el ViewModel:
            // val producto = viewModel.obtenerProductoPorId(productoId)
            // Por ahora se genera un producto, pero esto debe desaparecer
            val producto = Producto(
                id = productoId,
                nombre = "Producto de prueba",
                precio = "$10.000 CLP",
                imagen = R.drawable.torta_chocolate,
                categoria = com.grupo3.misterpastel.model.Categoria.TORTA_CUADRADA,
                descripcion = "Descripción de ejemplo."
            )
            DetalleProductoScreen(navController, producto)
        }
        composable("carrito") { CarritoScreen(navController) }
        composable("pedidos") { PedidoScreen(navController) }
        composable("perfil") { PerfilUsuarioScreen(navController) }



    }
}

