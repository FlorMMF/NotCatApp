package com.mtimes.notcatapp.presentation


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.derivedStateOf
import com.mtimes.notcatapp.R
import com.mtimes.notcatapp.navigation.Screen


@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(navController: NavHostController, onRegistrar: (String, String, String, String, android.content.Context) -> Unit){

    val context = LocalContext.current

    var nomUsuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmContrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val errorText by derivedStateOf {
        when {
            contrasena.isEmpty() && confirmContrasena.isEmpty() -> mutableStateOf("")
            contrasena == confirmContrasena -> mutableStateOf("")
            else -> mutableStateOf("Las contrasenas no coinciden.")
        }
    }

    val esValido by derivedStateOf {
        nomUsuario.isNotEmpty() &&
                correo.isNotEmpty() &&
                contrasena.isNotEmpty() &&
                confirmContrasena.isNotEmpty() &&
                contrasena == confirmContrasena
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
            Text("Registrarse",color = Color(0xCCC7719B), fontSize = 24.sp, fontWeight = FontWeight.Bold)


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nomUsuario,
                onValueChange = { nomUsuario= it},
                label = { Text("Nombre de Usuario")}
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") }
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = confirmContrasena,
                onValueChange = { confirmContrasena = it},
                label = { Text("Confirmar Contraseña")}
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = {
                    onRegistrar(nomUsuario,correo,contrasena,confirmContrasena,context)
                    //println("Se ha registrado exitosamente")
                    navController.popBackStack()

                },
                enabled = esValido,

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xCCC7719B),
                    contentColor = Color.White
                )
            ){
                Text("Registrar")
            }

            TextButton(
                onClick = {
                    navController.navigate(Screen.login.route) {
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Ya te registraste? Entra a tu cuenta")
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }


        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController(), onRegistrar = { _, _, _, _, _ ->})
}
