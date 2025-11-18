package com.grupo3.misterpastel.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grupo3.misterpastel.model.Categoria
import com.grupo3.misterpastel.model.Producto
import com.grupo3.misterpastel.navigation.TestNavHost
import com.grupo3.misterpastel.ui.screens.HomeSesionIniciada
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel
import com.grupo3.misterpastel.viewmodel.SessionViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeSesionIniciadaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeProducts = listOf(
        Producto(
            id = 1,
            nombre = "Torta Chocolate",
            precio = "15000",
            imagen = "",
            categoria = Categoria.TORTA_CIRCULAR,
            descripcion = "Deliciosa torta de chocolate"
        ),
        Producto(
            id = 2,
            nombre = "Cheesecake Frutilla",
            precio = "18000",
            imagen = "",
            categoria = Categoria.POSTRE_INDIVIDUAL,
            descripcion = "Fresco y cremoso"
        )
    )

    @Test
    fun homeSesionIniciada_renderizaProductos() {

        // MOCK DEL CATALOGO VIEWMODEL
        val catalogoVM = mockk<CatalogoViewModel>(relaxed = true)
        every { catalogoVM.productos } returns MutableStateFlow(fakeProducts)

        // MOCK DEL SESSION VIEWMODEL
        val sessionVM = mockk<SessionViewModel>(relaxed = true)
        every { sessionVM.usuarioActual } returns MutableStateFlow(null)

        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeSesionIniciada(
                navController = navController,
                catalogoViewModel = catalogoVM,
                sessionViewModel = sessionVM
            )
        }

        composeTestRule.onNodeWithText("Torta Chocolate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cheesecake Frutilla").assertIsDisplayed()
    }

    @Test
    fun homeSesionIniciada_clickProducto_navegaADetalle() {

        lateinit var navController: NavHostController

        val catalogoVM = mockk<CatalogoViewModel>(relaxed = true)
        every { catalogoVM.productos } returns MutableStateFlow(fakeProducts)

        val sessionVM = mockk<SessionViewModel>(relaxed = true)
        every { sessionVM.usuarioActual } returns MutableStateFlow(null)

        composeTestRule.setContent {
            navController = rememberNavController()

            TestNavHost(
                navController = navController,
                startDestination = "home_iniciada",
                homeSesionIniciadaContent = {
                    HomeSesionIniciada(
                        navController = navController,
                        catalogoViewModel = catalogoVM,
                        sessionViewModel = sessionVM
                    )
                }
            )
        }

        // Click real del botón en tu ProductoCard
        composeTestRule.onNodeWithTag("verDetalles_1").performClick()

        composeTestRule.waitForIdle()

        assert(navController.currentBackStackEntry?.destination?.route?.startsWith("detalle") == true)
    }


}
