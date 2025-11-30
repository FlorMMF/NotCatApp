package com.mtimes.notcatapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mtimes.notcatapp.R
import com.mtimes.notcatapp.data.ListItemEntity
import com.mtimes.notcatapp.model.ListsViewModel

@Composable
fun ListDetailScreen(
    navController: NavController,
    listId: Int,
    viewModel: ListsViewModel
) {
    val items = viewModel.items   // contains List<ListItemEntity>
    val listName = viewModel.currentListName

    var showDialog by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }

    // Cargar los elementos cuando abrimos la pantalla
    LaunchedEffect(listId) {
        viewModel.loadItems(listId)
        viewModel.loadListName(listId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            //titulo
            Text(
                text = listName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xCCC7719B),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            )

            //Barra de arriba para agregar elementos
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xCCC7719B),
                    contentColor = Color(0xCCFDD7D4)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Añadir elemento", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            val unchecked = items.filter { !it.checked }
            val checked = items.filter { it.checked }

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                //Elementos sin completar
                items(unchecked) { item ->
                    ListItemRow(
                        item = item,
                        onCheckChange = { newChecked ->
                            viewModel.updateItemStatus(item.id, newChecked, listId)
                        }
                    )
                }

                // Divisor si ambas listas tienen elementos
                if (unchecked.isNotEmpty() && checked.isNotEmpty()) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completadas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xCCC7719B),
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                thickness = 5.dp,
                                color = Color(0xCCC7719B)
                            )
                        }

                    }
                }

                // Elementos checados
                items(checked) { item ->
                    ListItemRow(
                        item = item,
                        onCheckChange = { newChecked ->
                            viewModel.updateItemStatus(item.id, newChecked, listId)
                        }
                    )
                }
            }
        }

        // pop-up para agregar un elemento de las listas
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newItemText.isNotBlank()) {
                                viewModel.addItem(listId, newItemText)
                            }
                            newItemText = ""
                            showDialog = false
                        }
                    ) {
                        Text("Añadir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Nuevo elemento") },
                text = {
                    TextField(
                        value = newItemText,
                        onValueChange = { newItemText = it },
                        label = { Text("Escribe un elemento") }
                    )
                }
            )
        }
    }
}

//Listas de los elementos
@Composable
fun ListItemRow(
    item: ListItemEntity,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White.copy(alpha = 0.85f), shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.checked,
            onCheckedChange = onCheckChange
        )
        Text(
            text = item.text,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp),
            color = Color.Black,
            fontWeight = if (item.checked) FontWeight.Medium else FontWeight.Black
        )
    }
}