package com.mtimes.notcatapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mtimes.notcatapp.data.ListsRepository
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.model.ListsViewModel
import com.mtimes.notcatapp.model.ListsViewModelFactory
import com.mtimes.notcatapp.presentation.ListDetailScreen
import com.mtimes.notcatapp.presentation.LoginScreen
import com.mtimes.notcatapp.presentation.PrincipalScreen
import com.mtimes.notcatapp.presentation.RegisterScreen
import com.mtimes.notcatapp.presentation.ReminderScreen
import com.mtimes.notcatapp.presentation.listsScreen

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
            route = "listsDetails_screen/{listId}",
            arguments = listOf(
                navArgument("listId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val listId = backStackEntry.arguments?.getInt("listId") ?: -1
            val context = LocalContext.current
            val db = UserDB(context, null)

            // Repository -> ViewModel
            val repository = remember { ListsRepository(db) }
            val viewModel: ListsViewModel = viewModel(
                factory = ListsViewModelFactory(repository)
            )

            ListDetailScreen(
                navController = navController,
                listId = listId,
                viewModel = viewModel
            )
        }

        composable(
            route = "lists_screen/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val context = LocalContext.current
            val db = UserDB(context, null)

            // Repository -> ViewModel
            val repository = remember { ListsRepository(db) }
            val viewModel: ListsViewModel = viewModel(
                factory = ListsViewModelFactory(repository)
            )

            listsScreen(
                navController = navController,
                UserID = userId,
                viewModel = viewModel
            )
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