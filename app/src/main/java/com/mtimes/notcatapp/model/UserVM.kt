package com.mtimes.notcatapp.model

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.presentation.RegisterScreen

data class UserVM(
    val id: Int,
    val nomUs: String,
    val email: String,
    val pass: String
)

class RegisterActivity: ComponentActivity(){
    //private val listaUsuarios = mutableListOf<UserVM>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent{
            val navController = rememberNavController()
            val dbHelper = UserDB(applicationContext, null)
            RegisterScreen(navController, onRegistrar = { nomUsuario, correo, contra, confirmContra, context ->
                if (dbHelper.checkEmail(nomUsuario)) {
                    Toast.makeText(context, "Usuario ya registrado", Toast.LENGTH_SHORT).show()
                } else if (dbHelper.checkEmail(correo)) {
                    Toast.makeText(context, "Correo ya registrado", Toast.LENGTH_SHORT).show()
                } else {

                    val id = dbHelper.addUser(nomUsuario, correo, contra)
                    Log.d("RegisterActivity", "Intentando registrar usuario: $nomUsuario, ID devuelto = $id")
                    if (id != -1L) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        Log.d("DB", " Usuario insertado correctamente con ID: $id")
                    } else {
                        Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        Log.e("DB", "Error al insertar usuario (insert devolvió -1)")

                    }
                }
            }
            )
        }
    }
}