package com.mtimes.notcatapp.presentation

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mtimes.notcatapp.R
import com.mtimes.notcatapp.model.ListsViewModel
import com.mtimes.notcatapp.navigation.Screen

@Composable
fun listsScreen(navController: NavHostController, UserID: Int, viewModel: ListsViewModel) {
    val lists = viewModel.lists
    var showDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    //cargar listas del usuario
    LaunchedEffect(Unit) {
        viewModel.loadLists(UserID)
    }

    Scaffold(
        //simbolo + para crear listas
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xCCC7719B),
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Lista")
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {


            Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)   // <- now padding is really used
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //titulo
                Text(
                    "Tus listas",
                    modifier = Modifier.padding(top = 24.dp),
                    color = Color(0xCCC7719B),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    //para cada elemento de las listas del usuario hacemos una tarjeta

                    items(lists) { list ->
                        ListCard(
                            name = list.name,
                            onClick = {
                                navController.navigate(Screen.listsDetails.createRouteLstDetail(list.id))
                            }
                        )
                    }
                }
            }
        }
    }

    //pop-up para crear listas
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },

            confirmButton = {
                TextButton(onClick = {

                    if (newListName.isNotBlank()) {
                        viewModel.addList(newListName, UserID)
                    }
                    newListName = ""
                    showDialog = false
                }) {
                    Text("Crear")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },

            title = { Text("Nueva Lista") },
            text = {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Nombre de lista") }
                )
            }
        )
    }
}



//función de que crea las tarjetas para cada elemento del arreglo de listas
@Composable
fun ListCard(
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
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}