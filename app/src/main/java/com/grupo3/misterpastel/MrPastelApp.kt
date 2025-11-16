package com.grupo3.misterpastel


import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

class MrPastelApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)   // Usa hasta 25% de RAM para imágenes
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizeBytes(50L * 1024 * 1024) // 50 MB de cache en disco
                    .build()
            }
            .respectCacheHeaders(false) // Ignora headers y usa nuestra cache
            .crossfade(true) // Animación suave al cargar
            .build()
    }
}
