package com.catoncat.studyapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.catoncat.studyapp.ui.theme.DarkColorScheme
import com.catoncat.studyapp.ui.theme.Typography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import studyapplication3.composeapp.generated.resources.Res
import studyapplication3.composeapp.generated.resources.compose_multiplatform
import kotlin.math.roundToInt

@Composable
fun AllCoursesPage(modifier: Modifier) {
    Column {
        OutlinedTextField(
            "",
            {},
            Modifier.fillMaxWidth().padding(all = 2.5.dp),
            placeholder = { Text("Search courses...") })

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
                    OutlinedCard(
                        Modifier.fillMaxWidth().padding(5.dp), colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            "Супер курс\nThis course is very cool course",
                            textAlign = TextAlign.Left,
                            modifier = Modifier.padding(2.5.dp).fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesPage(modifier: Modifier) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Test") },
//                navigationIcon = { Text("T") }
//            )
//        }) {
//
//    }

//    Column(modifier) {
//        Row(Modifier.fillMaxWidth()/*.border(Dp(1.5f), Color.Black)*/) {
//            Button(onClick = {}, Modifier.weight(0.5f)) {
//                Text("Learned")
//
//            }
//            Button(onClick = {}, Modifier.weight(0.5f)) {
//                Text("Created")
//            }
//        }
//        LazyColumn {
//            for (i in 1..3) {
//                item {
//                    Button(
//                        onClick = {}, Modifier.fillMaxSize()
//                    ) {
//                        Text("SuperCourse\nThis course is very cool course", textAlign = TextAlign.Left)
//                    }
//                }
//            }
//        }
//    }
    Row {
        Canvas(Modifier.weight(1.0f)) {
            drawLine(Color.White, Offset(0.0f, 0.0f), Offset(10.0f, 10.0f))
        }
        VerticalDivider(Modifier.fillMaxHeight())
        var offsetX by remember { mutableStateOf(0.0f) };
        var offsetY by remember { mutableStateOf(0.0f) };
        Column(
            Modifier.weight(0.35f)
        ) {
            Text(
                "Test",
                Modifier.fillMaxSize().offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }.draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { distance ->
                        {
                            offsetX += distance
                            offsetY += distance
                        }
                    })
            )
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

    var test = remember { mutableListOf(1, 2, 3) }

    MaterialTheme(DarkColorScheme, typography = Typography) {
        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
            NavigationBar() {
                NavigationBarItem(
                    label = { Text("Все курсы") },
                    onClick = { page = 0 },
                    selected = false,
                    icon = { Text("📚") })
                NavigationBarItem(
                    label = { Text("Мои курсы") },
                    onClick = { page = 1 },
                    selected = false,
                    icon = { Text("📝") })
                NavigationBarItem(
                    label = { Text("Настройки") },
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