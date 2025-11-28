package com.mtimes.notcatapp.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun programarNotificacionBD(
    context: Context,
    fecha: String,
    hora: String,
    titulo: String,
    mensaje: String,
    reminderId : Int
) {
    try {

        val fechaPartes = fecha.split("/")
        val dia = fechaPartes[0].toInt()
        val mes = fechaPartes[1].toInt()
        val anio = fechaPartes[2].toInt()


        val horaPartes = hora.split(":")
        val hora = horaPartes[0].toInt()
        val minuto = horaPartes[1].toInt()


        val calendar = Calendar.getInstance().apply {
            set(anio, mes - 1, dia, hora, minuto, 0)
        }

        val diferenciaMs = calendar.timeInMillis - System.currentTimeMillis()
        if (diferenciaMs < 0) {
            Log.e("NOTIF", "La fecha ya pasó")
            return
        }


        val data = workDataOf(
            "titulo" to titulo,
            "mensaje" to mensaje,
            "reminderId" to reminderId
        )

        val request = OneTimeWorkRequestBuilder<NotificacionWorker>()
            .setInitialDelay(diferenciaMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("NOTIF", "Error al programar la notificación")
    }
}
