package com.mtimes.notcatapp.presentation

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtimes.notcatapp.R
import com.mtimes.notcatapp.data.ReminderRepository
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.model.ReminderViewModel
import com.mtimes.notcatapp.model.ReminderViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun editReminderScreen(
    navController: NavHostController,
    UserID: Int,
    reminderID: Int,
    onSaveReminder: (String, String, String, String, String, String, Context) -> Unit){

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Build DB → Repository → ViewModel
    val db = remember { UserDB(context, null) }
    val repository = remember { ReminderRepository(
        db,
        context
    ) }

    val viewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(repository)
    )

    val user = viewModel.user

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var repeatOption by remember { mutableStateOf("Nunca") }

    var showDialogC by remember { mutableStateOf(false) }
    var showDialogT by remember { mutableStateOf(false) }
    var activDropmenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val options = listOf("Nunca", "Cada año", "Cada mes", "Cada semana")

    LaunchedEffect(reminderID) {
        if (reminderID != -1) {
            scope.launch(Dispatchers.IO) {
                Log.d("DEBUG_EDIT", "Buscando ID: $reminderID")

                val reminder = try {
                    db.getReminderById(reminderID)
                } catch (e: Exception) {
                    Log.e("DEBUG_EDIT", "Error al buscar", e)
                    null
                }

                Log.d("DEBUG_EDIT", "Resultado encontrado: $reminder")


                withContext(Dispatchers.Main) {
                    reminder?.let {
                        title = it.title
                        description = it.description
                        date = it.date
                        time = it.time
                        repeatOption = it.repeat


                        if(it.repeat != "Nunca") {
                            activDropmenu = true
                        }
                    }
                }
            }
        }
        // Cargar usuario
        viewModel.loadUser(UserID)
    }

    val esValido by derivedStateOf {
        title.isNotEmpty() &&
                description.isNotEmpty() &&
                date.isNotEmpty() &&
                time.isNotEmpty() &&
                repeatOption.isNotEmpty()
        //errorText.isEmpty()
    }



    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Editar Recordatorio",
                color = Color(0xCCC7719B),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold)


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it},
                label = { Text("Titulo") }

            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("No olvides los detalles!")},
                singleLine = false
            )

            //Fecha y hora
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.Center,
            ){
                FilledTonalButton(
                    onClick = {showDialogC= true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCCC7719B),
                        contentColor = Color.White
                    )
                ){
                    Text(if(date.isEmpty()) "Elegir fecha" else date)
                }


                FilledTonalButton(
                    onClick = {showDialogT= true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCCC7719B),
                        contentColor = Color.White
                    )
                ){
                    Text(if(time.isEmpty()) "Elegir hora" else time)
                }
            }


            // pop up del calendario
            if (showDialogC) {
                DatePickerDialog(
                    onDismissRequest = { showDialogC = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val calendarDate = Calendar.getInstance()
                                calendarDate.timeInMillis = millis
                                calendarDate.add(Calendar.DAY_OF_MONTH, 1) // A veces necesario con DatePicker
                                val formatter  = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
                                date = formatter.format(Date(millis))
                            }
                            showDialogC = false
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogC = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    // DatePicker interno — se adapta automáticamente al Locale del sistema
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = Color(0xCCBF7172),
                            selectedDayContentColor = Color(0xCCFDD7D4)
                        )
                    )
                }
            }

            //pop up del la hora
            if (showDialogT) {
                TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                        cal.set(Calendar.MINUTE, selectedMinute)

                        val format = SimpleDateFormat("HH:mm", Locale("es", "ES"))
                        time = format.format(cal.time)
                        showDialogT = false
                    },
                    hour,
                    minute,
                    false
                ).apply {
                    setOnDismissListener { showDialogT = false }
                    show()
                }
            }


            // Repetir Recordatorio
            Row(verticalAlignment = Alignment.CenterVertically){
                Checkbox(
                    checked = activDropmenu ,
                    onCheckedChange =  {activDropmenu= it
                        if (!it) repeatOption = "Nunca"},
                    modifier = Modifier.padding(6.dp),
                    enabled = true,
                    colors = CheckboxDefaults.colors()
                )

                Text(
                    text = "Repetir Recordatorio",
                    color = Color(0xCCC7719B), fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                )
            }

            Box {
                OutlinedTextField(
                    value = repeatOption,
                    onValueChange = {},
                    label = { Text("Selecciona una opción") },
                    readOnly = true,
                    enabled = activDropmenu,
                    trailingIcon = {
                        if (activDropmenu) {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },

                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFE0E0E0), // darker gray when disabled
                        disabledTextColor = Color(0xFF7A7A7A),     // readable dark gray text
                        focusedContainerColor = Color(0xFFFFF5F5), // light pink when active
                        unfocusedContainerColor = Color(0xFFFCE4EC) // subtle pink when idle
                    )

                )

                DropdownMenu(
                    expanded = expanded && activDropmenu,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                repeatOption = option
                                expanded = false
                            }
                        )
                    }
                }

            }




            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = {
                    if (user != null) {
                        scope.launch(Dispatchers.IO) {
                            if (reminderID != -1) {

                                try {
                                    db.updateReminder(
                                        id = reminderID,
                                        title = title,
                                        description = description,
                                        date = date,
                                        time = time,
                                        repeat = repeatOption,
                                        context = context
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {

                                withContext(Dispatchers.Main) {
                                    onSaveReminder(user.nomUs, title, description, date, time, repeatOption, context)
                                }
                            }

                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        }
                    }
                    //println("Se ha registrado exitosamente")

                },
                enabled = esValido,

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xCCC7719B),
                    contentColor = Color.White
                )
            ){
                Text("Guardar")
            }


        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun editReminderScreenPreview() {
    ReminderScreen(navController = rememberNavController(),-1, onSaveReminder = { _, _, _, _, _, _, _ ->})
}
