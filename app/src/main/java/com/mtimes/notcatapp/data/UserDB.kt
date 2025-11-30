package com.mtimes.notcatapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mtimes.notcatapp.model.Reminder
import com.mtimes.notcatapp.model.UserVM
import com.mtimes.notcatapp.notification.programarNotificacionBD
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


open class UserDB(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    //para crear y mantener la bd
    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DB", "onCreate ejecutado: creando tabla...")
        try {
            Log.d("DB", "onCreate: creando tabla $TABLE_NAME")
            db.execSQL(
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$NAME_COL TEXT, " +
                        "$EMAIL_COL TEXT, " +
                        "$PASS_COL TEXT)"


            )

            db.execSQL(
                "CREATE TABLE $TABLE_RMND (" +
                        "$ID_RMND INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$USER_RMND TEXT, " +
                        "$TITLE_RMND TEXT, " +
                        "$DESCRIPTION_RMND TEXT," +
                        "$DATE_RMND TEXT,"
                        + "$TIME_RMND TEXT,"
                        + "$REPEAT_RMND INTEGER)"

            )
            Log.d("DB", "onCreate: tabla creada correctamente")
        } catch (e: Exception) {
            Log.e("DB", "onCreate: error creando la tabla", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            Log.d("DB", "onUpgrade: borrando tabla antigua y recreando")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_RMND")
            onCreate(db)
        } catch (e: Exception) {
            Log.e("DB", "onUpgrade: error durante upgrade", e)
        }
    }

    //para interactuar con la bd (insertar y recuperar)

    fun addUser (name: String, email: String, password: String): Long{
        //la fun insert deberia devolver el id (0...n) si no lo hace se manda -1 como error
        var id: Long = -1

        val databaseAccess : SQLiteDatabase = getWritableDatabase()
        val values = ContentValues().apply{
            put(NAME_COL, name)
            put(EMAIL_COL, email)
            put(PASS_COL, password)
        }

        try{
            id= databaseAccess.insert(TABLE_NAME, null, values)
        }catch(e: Exception){
            Log.e("DB", "Error al guardar el usuario", e)
        }
        return id
    }

    fun getUserById(id: Int): UserVM? {
        val db = this.readableDatabase
        var user: UserVM? = null

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ID_COL = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_COL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(PASS_COL))

            user = UserVM(
                id = userId,
                nomUs = name,
                email = email,
                pass = password
            )
        }

        cursor.close()


        return user
    }



    fun getReminderById (id: Int): Reminder?{
        val db = this.readableDatabase
        var reminder: Reminder? = null

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_RMND WHERE $ID_RMND = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            val reminderId = id
            val userId = cursor.getString(cursor.getColumnIndexOrThrow(USER_RMND))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(TIME_RMND))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DATE_RMND))
            val repeat = cursor.getString(cursor.getColumnIndexOrThrow(REPEAT_RMND))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_RMND))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_RMND))


            reminder = Reminder(
                user = userId,
                title = title,
                description = description,
                date = date,
                time = time,
                repeat = repeat,
                reminderId = reminderId
            )
        }

        cursor.close()


        return reminder
    }




    fun checkUser(user_name: String): Boolean {
        val sqLiteDatabase = this.readableDatabase

        val columns = arrayOf<String>(NAME_COL)
        val selection: String = NAME_COL + " LIKE ?" // WHERE nombre LIKE ?
        val selectionArgs = arrayOf(user_name)

        val c = sqLiteDatabase.query(
            TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val b = c.count > 0
        c.close()
        return b
    }

    fun checkUser(email: String, password: String): Long {
        val db = this.readableDatabase

        val columns = arrayOf(ID_COL)
        val selection = "$EMAIL_COL = ? AND $PASS_COL = ?"
        val selectionArgs = arrayOf(email, password)

        val cursor = db.query(
            TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var userId: Long = -1

        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL))
        }

        cursor.close()
        return userId
    }

    fun checkEmail(user_email: String): Boolean {
        val sqLiteDatabase = this.readableDatabase

        val columns = arrayOf<String>(EMAIL_COL)
        val selection: String = EMAIL_COL + " LIKE ?" // WHERE nombre LIKE ?
        val selectionArgs = arrayOf(user_email)

        val c = sqLiteDatabase.query(
            TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val b = c.count > 0
        c.close()
        return b
    }

    fun addReminder (
        user: String,
        title: String,
        description: String,
        date: String,
        time: String,
        repeat: String,
        context: Context): Long{
        //la fun insert deberia devolver el id (0...n) si no lo hace se manda -1 como error
        var id: Long = -1

        val databaseAccess : SQLiteDatabase = getWritableDatabase()
        val values = ContentValues().apply{
            put(USER_RMND, user)

            put(TITLE_RMND,title)
            put(DESCRIPTION_RMND, description)
            put(DATE_RMND, date)
            put(TIME_RMND, time)
            put(REPEAT_RMND, repeat)

        }

        try{
            id= databaseAccess.insert(TABLE_RMND, null, values)
            if (id != -1L) {

                programarNotificacionBD(
                    context = context,
                    fecha = date,
                    hora = time,
                    titulo = title,
                    mensaje = description,
                    reminderId = id.toInt()

                )
            }
        }catch(e: Exception){
            Log.e("DB", "Error al guardar el usuario", e)
        }


        return id
    }

    fun updateReminder (
        id: Int,
        title: String,
        description: String,
        date: String,
        time: String,
        repeat: String,
        context: Context
    ): Boolean {

        val db = this.writableDatabase
        val values = ContentValues().apply{

            put(TITLE_RMND,title)
            put(DESCRIPTION_RMND, description)
            put(DATE_RMND, date)
            put(TIME_RMND, time)
            put(REPEAT_RMND, repeat)

        }

        val rowsAffected = db.update(
            TABLE_RMND,
            values,
            "$ID_RMND = ?",
            arrayOf(id.toString()))

        db.close()

        programarNotificacionBD(
            context = context,
            fecha = date,
            hora = time,
            titulo = title,
            mensaje = description,
            reminderId = id.toInt()

        )


        return rowsAffected > 0
    }

    fun programarRecordatorio(context: Context, id: Int) {
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT $TITLE_RMND, $DESCRIPTION_RMND, $DATE_RMND, $TIME_RMND FROM $TABLE_RMND WHERE $ID_RMND = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            val titulo = cursor.getString(0)
            val descripcion = cursor.getString(1)
            val fechaTexto = cursor.getString(2)
            val horaTexto = cursor.getString(3)

            programarNotificacionBD(
                context,
                fechaTexto,
                horaTexto,
                titulo,
                descripcion,
                id
            )
        }

        cursor.close()
        db.close()
    }

    fun deleteReminder(id: Int, context: Context) {
        val reminder = getReminderById(id)

        if (reminder != null) {
            // Eliminar
            deleteReminder(id.toLong())

            // Crear siguiente si es repetitivo
            repetirRecordatorio(reminder, context)
        }
    }

    fun deleteReminder(id: Long): Int {
        val db = writableDatabase
        return db.delete( TABLE_RMND, "$ID_RMND = ?", arrayOf(id.toString()))
    }

    fun repetirRecordatorio(reminder: Reminder, context: Context) {
        try {
            // Si no se repite, salir
            if (reminder.repeat == "Nunca") return

            // Convertir fecha actual
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
            val formatoHora = SimpleDateFormat("HH:mm", Locale("es", "ES"))

            val fechaActual = formatoFecha.parse(reminder.date)
            val horaActual = formatoHora.parse(reminder.time)

            if (fechaActual == null || horaActual == null) return

            val calFecha = Calendar.getInstance()
            calFecha.time = fechaActual

            // Calcular nueva fecha según repetición
            when (reminder.repeat) {
                "Cada semana" -> calFecha.add(Calendar.WEEK_OF_YEAR, 1)
                "Cada mes" -> calFecha.add(Calendar.MONTH, 1)
                "Cada año" -> calFecha.add(Calendar.YEAR, 1)
            }

            // Formatear nueva fecha
            val nuevaFecha = formatoFecha.format(calFecha.time)
            val nuevaHora = reminder.time

            // Crear nuevo recordatorio
            addReminder(
                user = reminder.user,
                title =reminder.title,
                description = reminder.description,
                date = nuevaFecha,
                time = nuevaHora,
                repeat = reminder.repeat,
                context = context
            )

            Log.d("DB", "Se creó automáticamente un nuevo recordatorio repetitivo para la fecha $nuevaFecha")

        } catch (e: Exception) {
            Log.e("DB", "Error creando el siguiente recordatorio repetitivo", e)
        }
    }


    //para las variables de objetos
    companion object {
        private const val DATABASE_NAME = "REMINDERS_APP"
        private const val DATABASE_VERSION = 11
        const val TABLE_NAME = "user_info"
        const val ID_COL = "id"
        const val NAME_COL = "name"
        const val EMAIL_COL = "email"
        const val PASS_COL = "password"

        const val TABLE_RMND = "reminder"
        const val ID_RMND = "id"
        const val USER_RMND = "user"
        const val TITLE_RMND = "title"
        const val DESCRIPTION_RMND = "description"
        const val DATE_RMND = "date"
        const val TIME_RMND = "time"
        const val REPEAT_RMND = "repeat"

    }
}