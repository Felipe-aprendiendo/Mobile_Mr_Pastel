package com.grupo3.misterpastel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.grupo3.misterpastel.ui.components.ProductoCard
import com.grupo3.misterpastel.viewmodel.CatalogoViewModel

/**
 * Grid de productos alimentado desde CatalogoViewModel.
 */
@Composable
fun CatalogoScreen(navController: NavController, vm: CatalogoViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(0.dp)
    ) {
        items(state.productos) { p ->
            ProductoCard(producto = p) {
                navController.navigate("detalle/${it.id}")
            }
        }
    }
}
