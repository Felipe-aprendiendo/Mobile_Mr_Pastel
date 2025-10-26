package com.grupo3.misterpastel.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grupo3.misterpastel.R
import com.grupo3.misterpastel.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val usuarioActual by sessionViewModel.usuarioActual.collectAsState()

    // Redirección si no hay sesión
    LaunchedEffect(usuarioActual) {
        if (usuarioActual == null) {
            navController.navigate("login") {
                popUpTo("perfil") { inclusive = true }
            }
        }
    }
    if (usuarioActual == null) return

    // Estado editable local (inicializado desde sesión)
    var nombre by remember(usuarioActual) { mutableStateOf(usuarioActual!!.nombre) }
    var email by remember(usuarioActual) { mutableStateOf(usuarioActual!!.email) }
    var fechaNacimiento by remember(usuarioActual) { mutableStateOf(usuarioActual!!.fechaNacimiento) }
    var direccion by remember(usuarioActual) { mutableStateOf(usuarioActual!!.direccion) }
    var telefono by remember(usuarioActual) { mutableStateOf(usuarioActual!!.telefono) }
    var fotoUrl by remember(usuarioActual) { mutableStateOf(usuarioActual!!.fotoUrl) }

    var showPicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Launchers: galería y cámara (preview)
    val pickFromFiles = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selected ->
            fotoUrl = selected.toString()
            sessionViewModel.actualizarFoto(fotoUrl) { msg -> error = msg }
        }
    }

    val takePhotoPreview = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            val saved = saveBitmapToGallery(
                context = context,
                bitmap = it,
                displayName = "mr_pastel_profile_${System.currentTimeMillis()}.jpg"
            )
            fotoUrl = saved?.toString()
            sessionViewModel.actualizarFoto(fotoUrl) { msg -> error = msg }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil \uD83E\uDDD1\u200D\uD83C\uDF73", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // === FOTO DE PERFIL ===
            if (fotoUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            } else {
                // Placeholder con logo
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_claro),
                        contentDescription = "Foto de perfil no establecida",
                        modifier = Modifier.size(76.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(onClick = { showPicker = true }) {
                Text("Cambiar foto")
            }

            Spacer(Modifier.height(18.dp))

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
            }

            PerfilField("Nombre", nombre) { nombre = it }
            PerfilField("Correo electrónico", email) { email = it }
            PerfilField("Fecha de nacimiento", fechaNacimiento) { fechaNacimiento = it }
            PerfilField("Dirección", direccion) { direccion = it }
            PerfilField("Teléfono", telefono) { telefono = it }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    val actualizado = usuarioActual!!.copy(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        fechaNacimiento = fechaNacimiento.trim(),
                        direccion = direccion.trim(),
                        telefono = telefono.trim(),
                        fotoUrl = fotoUrl
                    )
                    sessionViewModel.actualizarPerfil(actualizado) { msg -> error = msg }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Guardar cambios")
            }

            Spacer(Modifier.height(8.dp))


        }

        // BottomSheet para elegir cámara o archivos
        if (showPicker) {
            ModalBottomSheet(onDismissRequest = { showPicker = false }) {
                ListItem(
                    headlineContent = { Text("Usar cámara") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPicker = false
                            takePhotoPreview.launch(null)
                        }
                )
                ListItem(
                    headlineContent = { Text("Elegir desde archivos") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPicker = false
                            pickFromFiles.launch("image/*")
                        }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PerfilField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}

/** Guarda un Bitmap en MediaStore y devuelve su Uri pública (Pictures/MrPastel). */
private fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    displayName: String
): Uri? {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MrPastel")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val itemUri = resolver.insert(collection, values) ?: return null
    resolver.openOutputStream(itemUri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    values.clear()
    values.put(MediaStore.Images.Media.IS_PENDING, 0)
    resolver.update(itemUri, values, null, null)
    return itemUri
}
