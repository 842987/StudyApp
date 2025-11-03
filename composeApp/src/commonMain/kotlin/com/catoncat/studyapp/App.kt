package com.catoncat.studyapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import studyapplication3.composeapp.generated.resources.Res
import studyapplication3.composeapp.generated.resources.compose_multiplatform

@Composable
fun AllCoursesPage(modifier: Modifier) {
    Column {

        OutlinedTextField("", {}, Modifier.fillMaxWidth().padding(vertical = Dp(2.5f)), placeholder = {Text("Search courses...")})

        LazyColumn(modifier) {
//        item{Button(onClick = {}, modifier) {
//            Text("SuperCourse", Modifier.scale(15.0f))
//            Text("This course is very cool course")
//        }}
//        item{Button(onClick = {}, modifier) {
//            Text("SuperCourse", Modifier.scale(15.0f))
//            Text("This course is very cool course")
//        }}
//        item{Button(onClick = {}, modifier) {
//            Text("SuperCourse", Modifier.scale(15.0f))
//            Text("This course is very cool course")
//        }}
            for (i in 1..10) {
                item {
                    Button(
                        onClick = {}, Modifier.fillMaxSize()
                    ) {
                        Text("SuperCourse\nThis course is very cool course", textAlign = TextAlign.Left)
                    }
                }
            }
        }
    }
}

@Composable
fun MyCoursesPage(modifier: Modifier) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth()/*.border(Dp(1.5f), Color.Black)*/) {
            Button(onClick = {}, Modifier.weight(0.5f)) {
                Text("Learned")

            }
            Button(onClick = {}, Modifier.weight(0.5f)) {
                Text("Created")
            }
        }
        LazyColumn {
            for (i in 1..3) {
                item {
                    Button(
                        onClick = {}, Modifier.fillMaxSize()
                    ) {
                        Text("SuperCourse\nThis course is very cool course", textAlign = TextAlign.Left)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsPage(modifier: Modifier) {

}

@Composable
@Preview
fun App() {

//    val navController = rememberNavController();
//    NavHost(navController, startDestination = "All courses") {
//        composable("All courses") {
//            AllCoursesPage()
//        }
//        composable("My courses") {
//
//        }
//        composable("Settings") {
//
//        }
//    }
    var page by remember { mutableStateOf(0) };

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        NavigationBar() {
            NavigationBarItem(
                label = { Text("All courses") },
                onClick = { page = 0 },
                selected = false,
                icon = { Text("📚") })
            NavigationBarItem(
                label = { Text("My courses") },
                onClick = { page = 1 },
                selected = false,
                icon = { Text("📝") })
            NavigationBarItem(
                label = { Text("Settings") },
                onClick = { page = 2 },
                selected = false,
                icon = { Text("⚙") })
        }
    }) { innerPadding ->
        var modifier = Modifier.padding(innerPadding).fillMaxSize()
        if (page == 0) {
            AllCoursesPage(modifier)
        } else if (page == 1) {
            MyCoursesPage(modifier);
        } else if (page == 2) {
            SettingsPage(modifier);
        }
    }
}
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }

//}