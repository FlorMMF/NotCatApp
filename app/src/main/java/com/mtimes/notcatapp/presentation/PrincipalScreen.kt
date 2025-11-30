package com.mtimes.notcatapp.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavHostController,
                    dbHelper: UserDB,
                    userId: Long ) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController)
        }
    ) {

        Scaffold(
            topBar = {
                TopBar(
                    onMenuClick = {
                        //
                        scope.launch { drawerState.open() }
                    }

                )
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.Center,
                ){
                    ExtendedFloatingActionButton(
                        onClick = {  navController.navigate(Screen.Reminder.createRoute(userId.toInt())) } ,

                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        icon = { Icon(Icons.Filled.Edit, "Añadir") },
                        text = { Text(text = "Añadir recordatorio") },
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    ExtendedFloatingActionButton(
                        onClick = {  navController.navigate(Screen.lists.createRouteLst(userId.toInt())) } ,

                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        icon = { Icon(Icons.Filled.Edit, "Añadir") },
                        text = { Text(text = "Añadir Lista") },
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavHostController) {
    ModalDrawerSheet {
        Text("Menú principal", modifier = Modifier.padding(16.dp))
        Divider()
        NavigationDrawerItem(
            label = { Text("Recordatorios") },
            selected = false,
            onClick = {
                //
            }
        )
        Divider()
        NavigationDrawerItem(
            label = { Text("Listas") },
            selected = false,
            onClick = {
                navController.navigate(Screen.lists.route) { launchSingleTop = true }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onMenuClick: () -> Unit

) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        title = { Text("NotCat") },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Cuenta")
            }
        }
    )
}







