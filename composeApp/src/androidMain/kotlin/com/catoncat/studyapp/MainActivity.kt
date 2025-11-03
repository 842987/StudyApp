package com.catoncat.studyapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.catoncat.studyapp.ui.theme.Purple40
import com.catoncat.studyapp.ui.theme.Purple80
import com.catoncat.studyapp.ui.theme.StudyApplication2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {

            StudyApplication2Theme { App() }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    StudyApplication2Theme {
        App()
    }
}