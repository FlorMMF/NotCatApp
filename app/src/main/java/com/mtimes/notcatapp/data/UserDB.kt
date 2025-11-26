package com.mtimes.notcatapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mtimes.notcatapp.model.UserVM
import com.mtimes.notcatapp.notification.programarNotificacionBD


class UserDB(context: Context, factory: SQLiteDatabase.CursorFactory?) :
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
        db.close()

        return user
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
                    mensaje = description
                )
            }
        }catch(e: Exception){
            Log.e("DB", "Error al guardar el usuario", e)
        }


        return id
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
                descripcion
            )
        }

        cursor.close()
        db.close()
    }


    //para las variables de objetos
    companion object {
        private const val DATABASE_NAME = "REMINDERS_APP"
        private const val DATABASE_VERSION = 9
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