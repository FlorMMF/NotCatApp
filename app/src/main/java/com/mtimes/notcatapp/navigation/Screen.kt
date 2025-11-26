package com.mtimes.notcatapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen (
    val route: String,
    val label: String
){
    @Serializable
    object login : Screen(route = "login_screen", label = "Login")

    @Serializable
    object register : Screen(route = "register_screen", label = "Register")

    @Serializable
    object principal : Screen(
        route = "principal_screen/{userId}",
        label = "Principal"
    ) {
        fun createRoute(userId: Long) = "principal_screen/$userId"
    }


    @Serializable
    object Reminder : Screen(
        route = "reminder_screen/{userId}",
        label = "Reminder"
    ) {
        fun createRoute(userId: Int) = "reminder_screen/$userId"
    } //para que reciba argumentos la funcion necesitas crear una ruta con el argumento que recibe
    @Serializable
    object lists : Screen (route = "lists_screen", label = "Lists"  )
}