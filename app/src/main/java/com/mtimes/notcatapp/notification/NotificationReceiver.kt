package com.mtimes.notcatapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mtimes.notcatapp.data.UserDB
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var reminderId = intent.getLongExtra("reminderId", -1L)
        val notificationId = intent.getIntExtra("notificationId", -1)
        if (reminderId == -1L) {

            val tempId = intent.getIntExtra("reminderId", -1)
            if (tempId != -1) {
                reminderId = tempId.toLong()
            }
        }



        when (intent.action) {

            "ACTION_DELETE" -> {

                if (notificationId != -1) {
                    NotificationManagerCompat.from(context).cancel(notificationId)
                }
                if (reminderId != -1L) {
                    try {
                        val db = UserDB(context, null)

                        db.deleteReminder(reminderId)
                        db.close()
                        android.util.Log.d("NotCatApp", "Reminder eliminado: $reminderId")
                    } catch (e: Exception) {
                        android.util.Log.e("NotCatApp", "Error al eliminar reminder", e)
                    }
                }
            }

            "ACTION_POSTPONE" -> {
                android.util.Log.d("NotCatApp", "Recibida acción de posponer")
                val userId = intent.getLongExtra("userId", -1)

                // Reagendar notificación en 1 minutos
                val data = Data.Builder()
                    .putString("titulo", "Recordatorio pospuesto")
                    .putString("mensaje", "Se ha pospuesto un minuto")
                    .putLong("userId", userId)
                    .build()

                val request = OneTimeWorkRequestBuilder<NotificacionWorker>()
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(context).enqueue(request)
                if (notificationId != -1) {
                    NotificationManagerCompat.from(context).cancel(notificationId)
                }
            }
        }
    }
}