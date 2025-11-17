package com.grupo3.misterpastel.ui


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.ui.screens.HomeSesionIniciada
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeSesionIniciadaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Datos falsos para el catálogo
     */
    private val fakeProducts = listOf(
        Producto(
            id = "1",
            nombre = "Torta Chocolate",
            categoria = "Tortas",
            precio = 15000,
            descripcion = "Deliciosa torta de chocolate",
            imagen = ""
        ),
        Producto(
            id = "2",
            nombre = "Cheesecake Frutilla",
            categoria = "Cheesecakes",
            precio = 18000,
            descripcion = "Fresco y cremoso",
            imagen = ""
        )
    )

    /**
     * Test 1:
     * La pantalla debe renderizar los productos del catálogo
     */
    @Test
    fun homeSesionIniciada_renderizaProductos() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeSesionIniciada(
                navController = navController,
                catalogoViewModel = FakeCatalogoViewModel(fakeProducts),
                sessionViewModel = FakeSessionViewModel()
            )
        }

        composeTestRule
            .onNodeWithText("Torta Chocolate")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cheesecake Frutilla")
            .assertIsDisplayed()
    }

    /**
     * Test 2:
     * Click en un producto → navegación hacia "detalle/{productoId}"
     */
    @Test
    fun homeSesionIniciada_clickProducto_navegaADetalle() {
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            navController = rememberNavController()
            HomeSesionIniciada(
                navController = navController,
                catalogoViewModel = FakeCatalogoViewModel(fakeProducts),
                sessionViewModel = FakeSessionViewModel()
            )
        }

        // Click en el primer producto
        composeTestRule
            .onNodeWithText("Torta Chocolate")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar destino
        val route = navController.currentDestination?.route
        assert(route?.startsWith("detalle") == true)
    }
}
