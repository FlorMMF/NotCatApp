package com.mtimes.notcatapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.presentation.LoginScreen
import com.mtimes.notcatapp.presentation.PrincipalScreen
import com.mtimes.notcatapp.presentation.RegisterScreen
import com.mtimes.notcatapp.presentation.ReminderScreen
import com.mtimes.notcatapp.presentation.editReminderScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    dbHelper: UserDB,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Screen.login.route,
        modifier = modifier
    ) {
        composable(Screen.login.route) {
            LoginScreen(
                navController = navController, onEntrar ={ correo, contra, context ->
                    val userId = dbHelper.checkUser(correo, contra)

                    if (userId != -1L) {
                        navController.navigate("principal_screen/$userId")
                        //navController.navigate("reminder_screen/$userId")

                    } else {
                        Toast.makeText(context, "Datos incorrectos", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        composable(Screen.register.route) {
            RegisterScreen(navController = navController,  onRegistrar = { nomUsuario, correo, contra, confirmContra, context ->
                if (dbHelper.checkUser(nomUsuario)) {
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
            })

        }

        composable(
            route = Screen.principal.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getLong("userId") ?: -1L

            PrincipalScreen(
                navController = navController,
                dbHelper = dbHelper,
                userId = userId
            )
        }

        composable(
            route = Screen.editReminder.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("reminderId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val reminderId = backStackEntry.arguments?.getInt("reminderId") ?: -1
            val context = LocalContext.current
            val dbHelper = UserDB(context, null)

            editReminderScreen(
                navController = navController,
                UserID = userId,
                reminderID = reminderId,
                onSaveReminder = { user, title, description, date, time, repeat, context ->
                    val result = dbHelper.updateReminder(
                            id = reminderId,
                            title = title,
                            description = description,
                            date = date,
                            time = time,
                            repeat = repeat,
                            context = context
                    )

                    if (result) {
                        Toast.makeText(context, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                        Log.d("Reminders", "Reminder saved with ID = $result")
                    } else {
                        Toast.makeText(context, "Error al guardar recordatorio", Toast.LENGTH_SHORT).show()
                        Log.e("Reminders", "Insert returned -1")
                    }

                }
            )
        }



        composable(Screen.lists.route){
            //ListScreen()
        }


        composable(
            route = "reminder_screen/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val context = LocalContext.current
            val dbHelper = UserDB(context, null)

            ReminderScreen(
                navController = navController,
                UserID = userId,
                onSaveReminder = { user, title, description, date, time, repeat, context ->
                    val result = dbHelper.addReminder(user,title,description,date,time,repeat,context)

                    if (result != -1L) {
                        Toast.makeText(context, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                        Log.d("Reminders", "Reminder saved with ID = $result")
                    } else {
                        Toast.makeText(context, "Error al guardar recordatorio", Toast.LENGTH_SHORT).show()
                        Log.e("Reminders", "Insert returned -1")
                    }
                }
            )
        }
    }
}