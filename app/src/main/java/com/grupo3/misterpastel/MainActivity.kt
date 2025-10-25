package com.grupo3.misterpastel

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.grupo3.misterpastel.navigation.AppNavigation
import com.grupo3.misterpastel.ui.theme.MrPastelTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MrPastelTheme {
                AppNavigation()
            }

        }
    }
}
