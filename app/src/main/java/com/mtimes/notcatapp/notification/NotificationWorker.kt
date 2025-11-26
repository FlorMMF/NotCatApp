package com.mtimes.notcatapp.notification

import android.Manifest
import android.R
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificacionWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {

        val titulo = inputData.getString("titulo") ?: "Recordatorio"
        val mensaje = inputData.getString("mensaje") ?: "Tienes un evento pendiente"

        val builder = NotificationCompat.Builder(applicationContext, "canal")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(System.currentTimeMillis().toInt(), builder.build())

        return Result.success()
    }
}
