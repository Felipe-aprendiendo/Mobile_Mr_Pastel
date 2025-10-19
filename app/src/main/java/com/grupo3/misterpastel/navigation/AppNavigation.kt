package com.grupo3.misterpastel.navigation



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo3.misterpastel.ui.screens.splash.SplashScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable ("splash") { SplashScreen(navController) }
        //composable("home") { HomeScreen(navController) }
        //composable("login") { LoginScreen(navController) }
        //composable("registro") { RegistroScreen(navController) }
        //composable("home_iniciada") { HomeSesionIniciada(navController) }
        //composable("catalogo") { /* TODO: pendiente again */ }
    }


}

