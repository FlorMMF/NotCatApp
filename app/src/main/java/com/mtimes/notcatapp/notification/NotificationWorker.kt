package com.mtimes.notcatapp.notification

import android.Manifest
import android.R
import android.R.attr.action
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mtimes.notcatapp.MainActivity
import kotlin.jvm.java

class NotificacionWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {

        val titulo = inputData.getString("titulo") ?: "Recordatorio"
        val mensaje = inputData.getString("mensaje") ?: "Tienes un evento pendiente"
        val userId = inputData.getLong("userId", -1L)
        val reminderId = inputData.getLong("reminderId", -1L)
        val notificationId = System.currentTimeMillis().toInt()



        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("navigateTo", "principal_screen/$userId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deleteIntent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            action = "ACTION_DELETE"
            putExtra("notificationId", notificationId)
            putExtra("reminderId", reminderId)
        }

        val deletePending = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // --- Acción: POSPONER ---
        val postponeIntent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            action = "ACTION_POSTPONE"
            putExtra("notificationId", notificationId)
            putExtra("userId", userId)
        }

        val postponePending = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            postponeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(applicationContext, "canal")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_delete, "Eliminar", deletePending)
            .addAction(R.drawable.ic_media_play, "Posponer", postponePending)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(notificationId, builder.build())

        return Result.success()
    }
}


