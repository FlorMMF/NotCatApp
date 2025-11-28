package com.mtimes.notcatapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.mtimes.notcatapp.data.UserDB
import com.mtimes.notcatapp.navigation.AppNavHost
import com.mtimes.notcatapp.ui.theme.NotCatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            }
        }
        setContent {
            NotCatAppTheme {
                val dbHelper = UserDB(applicationContext, null)
                val navController = rememberNavController()
                val navigateTo = intent.getStringExtra("navigateTo")

                LaunchedEffect(navigateTo) {
                    if (navigateTo != null) {
                        navController.navigate(navigateTo) {
                            popUpTo(0)
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize())
                {
                    AppNavHost(
                        navController = navController, dbHelper = dbHelper
                    )
                }
            }
        }
    }
}
