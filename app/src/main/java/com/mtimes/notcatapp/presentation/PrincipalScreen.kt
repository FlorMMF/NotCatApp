package com.mtimes.notcatapp.presentation
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.lifecycle.ViewModel
import androidx.compose.material.icons.filled.Edit
import com.mtimes.notcatapp.R
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.model.ReminderViewModel
import com.mtimes.notcatapp.navigation.Screen
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit


// Pantalla raíz que incluye drawer + scaffold + NavHost
@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun PrincipalScreen(
    navController: NavHostController,
    dbHelper: UserDB,
    userId: Int,
    viewModel: ReminderViewModel) {

    val reminders = viewModel.reminders
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.loadReminder(userId)
    }
    val imagePainter = painterResource(id = R.drawable.imagen_gatito)

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(//ya se puede ver el fondo
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        ModalNavigationDrawer (
            drawerState = drawerState,
            drawerContent = {//se agrego esto y ya se puede ver el fondo
                ModalDrawerSheet(drawerContainerColor = Color(0xAA000000)) {}

            }
        ) {

            Scaffold(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                topBar = {
                    TopBar(
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding).fillMaxSize()
                ){
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        numList(navController)
                        Spacer(modifier = Modifier.width(32.dp))
                        Image(
                            painter = painterResource(id = R.drawable.imagen_gatito),
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 120.dp, height = 120.dp)
                            //.offset(x = 200.dp, y = 0.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Tareas Pendientes:",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier/*.offset(x = 50.dp, y = 30.dp)*/
                            .padding(start = 50.dp, bottom = 4.dp)
                    )

                    ExtendedFloatingActionButton(
                        onClick = {  navController.navigate(Screen.Reminder.createRoute(userId.toInt())) } ,

                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        icon = { Icon(Icons.Filled.Edit, "Añadir") },
                        text = { Text(text = "Añadir recordatorio") },
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    /*ToDo()
                    Spacer(modifier = Modifier.height(16.dp))*/

                    LazyColumn(
                        modifier = Modifier.padding(16.dp).fillMaxSize()
                    ){
                        items(reminders){ remind ->
                            RemindCard(
                                name = remind.title,
                                onClick = {
                                    //navController.navigate(Screen.listsDetails.createRouteLstDetail(list.id))
                                }
                            )


                    ExtendedFloatingActionButton(
                        onClick = {  navController.navigate(Screen.Reminder.createRoute(userId.toInt())) } ,

                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        icon = { Icon(Icons.Filled.Edit, "Añadir") },
                        text = { Text(text = "Añadir recordatorio") },
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    ExtendedFloatingActionButton(
                        onClick = {  navController.navigate(Screen.editReminder.createRoute(userId.toInt(), 1)) } ,

                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        icon = { Icon(Icons.Filled.Edit, "Editar") },
                        text = { Text(text = "Editar recordatorio") },
                        modifier = Modifier
                            .padding(16.dp)
                    )


                }
            }
        }
    }

}

@Composable
fun RemindCard(
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCC7719B),
            contentColor = Color(0xCCFDD7D4)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

///*************************************************************************************************
@Composable
fun DrawerContent(navController: NavHostController, userId: Long) {
    ModalDrawerSheet {
        Text("Menú principal", modifier = Modifier.padding(16.dp))
        Divider()
        NavigationDrawerItem(
            label = { Text("Recordatorios") },
            selected = false,
            onClick = {
                navController.navigate(Screen.Reminder.createRoute(userId.toInt())) { launchSingleTop = true }
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

@Composable
fun numList(navController: NavHostController){
    val ColorPaletaRosa = Color(0xCCC7719B)

    val offsetX = 30.dp
    val offsetY = 50.dp
    Box(
        modifier = Modifier
        /*modifier = Modifier/*.padding(paddingValues)*///se agrego y se puede ver la tarjeta de num de listas
            .offset(x = offsetX, y = offsetY)*/
    ){
        Card(

            modifier = Modifier.size(width = 140.dp, height = 140.dp),
            colors = CardDefaults.cardColors(
                containerColor = ColorPaletaRosa
            )
        ){
            Text(text = "\tNúmero de  Listas",
                fontSize = 10.sp,
                textAlign = TextAlign.Start
            )

        }
    }

}

@Composable
fun ToDo(
    name: String,
    onClick: () -> Unit
){//Card de tareas pendientes
   /* val offsetX = 20.dp
    val offsetY = 50.dp*/

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCC7719B),
            contentColor = Color(0xCCFDD7D4)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
    /*Box(
        modifier = Modifier/*.padding(innerPadding)*/
            .offset(x = offsetX)
    ){
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.size(width = 320.dp, height = 100.dp)
        ){
            Text(text = "Por hacer",//deberia salir el nombre de la tarea
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Text(text = "Tengo que ir a hacer mandado despues de recoger al niño a la escuela",//descripccion de la tarea
                modifier = Modifier.padding (8.dp),
                textAlign = TextAlign.Justify,
                fontSize = 10.sp
            )
        }
    }*/
}
/*
@Preview(showBackground = true, showSystemUi = true)//la preview sigue sin funcionar, se esta usando la conexion directa con el celular para poder visualizar la Screen
@Composable
fun PrincipalScreenPreview() {
    PrincipalScreen(
        navController = rememberNavController(),
        dbHelper = UserDB(
            context = LocalContext.current,
            factory = TODO()
        ),
        userId = 1
    )
}*/





