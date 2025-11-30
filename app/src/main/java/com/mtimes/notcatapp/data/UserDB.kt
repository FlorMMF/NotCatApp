package com.mtimes.notcatapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.pdf.models.ListItem
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
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

            db.execSQL(
                "CREATE TABLE $TABLE_LISTS (" +
                        "$LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        "$LIST_USER_ID INTEGER, " +
                        "$LIST_NAME TEXT)"
            )

            db.execSQL(
                "CREATE TABLE $TABLE_ITEMS (" +
                        "$ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$ITEM_LIST_ID INTEGER, " +
                        "$ITEM_CONTENT TEXT, " +
                        "$ITEM_DONE INTEGER, " +
                        "FOREIGN KEY ($ITEM_LIST_ID) REFERENCES $TABLE_LISTS($LIST_ID) )"
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
            db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
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
                    mensaje = description,
                    reminderId = id.toInt()

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
                descripcion,
                id
            )
        }

        cursor.close()
        db.close()
    }

    fun deleteReminder(id: Long): Int {
        val db = writableDatabase
        return db.delete("rmnd", "id = ?", arrayOf(id.toString()))
    }

    fun createList(name: String, userId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(LIST_NAME, name)
            put(LIST_USER_ID, userId)
        }
        return db.insert(TABLE_LISTS, null, values)
    }

    fun getAllLists(userId: Int): List<ListEntity> {
        val db = readableDatabase
        val lists = mutableListOf<ListEntity>()

        val cursor = db.query(
            TABLE_LISTS,
            arrayOf(LIST_ID, LIST_USER_ID, LIST_NAME),
            "$LIST_USER_ID=?",
            arrayOf(userId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            lists.add(
                ListEntity(
                    id = cursor.getInt(0),
                    userId = cursor.getInt(1),
                    name = cursor.getString(2)
                )
            )
        }

        cursor.close()
        return lists
    }

    fun getListName(listId: Int): String {
        val cursor = readableDatabase.rawQuery(
            "SELECT $LIST_NAME FROM $TABLE_LISTS WHERE $LIST_ID = ?",
            arrayOf(listId.toString())
        )
        val name = if (cursor.moveToFirst())
            cursor.getString(cursor.getColumnIndexOrThrow(LIST_NAME))
        else ""
        cursor.close()
        return name
    }

    fun addItemToList(listId: Int, text: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ITEM_LIST_ID, listId)   // correct column name
            put(ITEM_CONTENT, text)     // correct column name
            put(ITEM_DONE, 0)           // default unchecked
        }
        db.insert(TABLE_ITEMS, null, values)  // correct table name
    }

    fun getItemsForList(listId: Int): List<ListItemEntity> {
        val db = readableDatabase
        val items = mutableListOf<ListItemEntity>()

        val cursor = db.rawQuery(
            "SELECT $ITEM_ID, $ITEM_LIST_ID, $ITEM_CONTENT, $ITEM_DONE FROM $TABLE_ITEMS WHERE $ITEM_LIST_ID = ?",
                    arrayOf(listId.toString())
        )

        cursor.use { c ->
            if (c.moveToFirst()) {

                val idIndex = c.getColumnIndexOrThrow(ITEM_ID)
                val listIdIndex = c.getColumnIndexOrThrow(ITEM_LIST_ID)
                val contentIndex = c.getColumnIndexOrThrow(ITEM_CONTENT)
                val doneIndex = c.getColumnIndexOrThrow(ITEM_DONE)

                do {
                    items.add(
                        ListItemEntity(
                            id = c.getInt(idIndex),
                            listId = c.getInt(listIdIndex),
                            text = c.getString(contentIndex),
                            checked = c.getInt(doneIndex) == 1
                        )
                    )
                } while (c.moveToNext())
            }
        }

        return items
    }

    fun updateItemDone(itemId: Int, done: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ITEM_DONE, if (done) 1 else 0)
        }
        return db.update(
            TABLE_ITEMS,
            values,
            "$ITEM_ID = ?",
            arrayOf(itemId.toString())
        )
    }


    //para las variables de objetos
    companion object {
        private const val DATABASE_NAME = "REMINDERS_APP"
        private const val DATABASE_VERSION = 13
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

        const val TABLE_LISTS = "lists"
        const val LIST_ID = "id_list"
        const val LIST_USER_ID = "user_id"
        const val LIST_NAME = "name"

        const val TABLE_ITEMS = "list_items"
        const val ITEM_ID = "id_item"
        const val ITEM_LIST_ID = "list_id"
        const val ITEM_CONTENT = "content"
        const val ITEM_DONE = "is_done"
    }
}