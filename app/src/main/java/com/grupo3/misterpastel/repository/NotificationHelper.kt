package com.grupo3.misterpastel.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.grupo3.misterpastel.R

/**
 * Utilidad simple para mostrar notificaciones locales sin pedir permisos explícitos.
 * - Crea el canal si no existe (Android 8+)
 * - Muestra una notificación básica (icono, título, texto)
 * - No requiere POST_NOTIFICATIONS (por simplicidad del proyecto)
 */
object NotificationHelper {

    private const val CHANNEL_ID = "mr_pastel_pedidos"
    private const val CHANNEL_NAME = "Pedidos Mr. Pastel"
    private const val CHANNEL_DESC = "Notificaciones locales de la app"

    /** Crea el canal de notificación si no existe (solo Android 8.0+) */
    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /** Muestra una notificación básica en la barra del sistema */
    fun showSimpleNotification(context: Context, title: String, message: String) {
        ensureChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // asegúrate de tener este ícono en res/drawable
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
