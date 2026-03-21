package com.catoncat.studyapp.ui.screen.coursecreating

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import kotlin.math.roundToInt

@Composable
fun CourseCreatingScreen(
    viewModel: CourseCreatingViewModel = viewModel<CourseCreatingViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {
        is CourseCreatingState.Content -> CourseCreatingContentState(currentState)
        is CourseCreatingState.Error -> CourseCreatingErrorState()
        CourseCreatingState.Loading -> CourseCreatingLoadingState()
    }
}

@Composable
fun CourseCreatingLoadingState() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CourseCreatingErrorState() {
    TODO("Error state")
}

@Composable
fun CourseCreatingContentState(content: CourseCreatingState.Content) {
//    Canvas(Modifier.fillMaxSize()) {
//        content.lessons.forEach {
//            when(it) {
//                is CourseCreatingState.Lesson.Content -> {
//                    drawCircle(Color.Green, 10.0f, Offset(it.x, it.y))
//                }
//                CourseCreatingState.Lesson.Error -> TODO("Lesson error")
//            }
//
//        }
//    }

    val scale = remember { mutableFloatStateOf(1.0f) }
    val offset = remember { mutableStateOf(Offset.Zero) }

    val clickedLesson = remember { mutableStateOf(content.lessons[0]) }
    val showAddDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }

    EditLessonDialog(clickedLesson.value, showEditDialog)
    AddLessonDialog(showAddDialog)

    Box(Modifier.fillMaxSize()) {

        Box(
            Modifier
                .requiredSize(1500.dp, 1500.dp)
//                .background(Color.Cyan)
//                .border(5.dp,Color.Green)
                .scale(scale = scale.floatValue)
                .offset {
                    IntOffset(
                        offset.value.x.roundToInt(),
                        offset.value.y.roundToInt()
                    )
                }
//                .graphicsLayer(
//                    scaleX = scale.floatValue,
//                    scaleY = scale.floatValue,
//                    translationX = offset.value.x,
//                    translationY = offset.value.y
//                )

                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale.floatValue *= zoom
                        offset.value += pan
                    }
                }
//                .transformable(rememberTransformableState { zoomChange, panChange, rotationChange ->
//                    scale.floatValue *= zoomChange
//                    offset.value += panChange
//                }, lockRotationOnZoomPan = true, enabled = true)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Color.DarkGray)
            }
            content.lessons.forEach { lesson ->
//                when (it) {
//                    is CourseCreatingState.Lesson -> {
                val x = remember { mutableFloatStateOf(lesson.x) }
                val y = remember { mutableFloatStateOf(lesson.x) }
                Button(
                    onClick = {
                        clickedLesson.value = lesson
                        showEditDialog.value = true
                    },
                    Modifier
                        .size(50.dp)
                        .offset {
                            IntOffset(
                                x.floatValue.roundToInt(),
                                y.floatValue.roundToInt()
                            )
                        }
                        .draggable2D(rememberDraggable2DState { o ->
                            x.floatValue += o.x
                            y.floatValue += o.y
                        })
                ) { Text("1") }
//            }
//        }

//                    CourseCreatingState.Lesson.Error -> TODO()
//                }
            }
        }


        FloatingActionButton(
            onClick = {
                showAddDialog.value = true
            },
            Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) { Text("+") }

    }
}

@Composable
fun EditLessonDialog(lesson: CourseCreatingState.Lesson, show: MutableState<Boolean>) {
    if (show.value) {

        Dialog(
            onDismissRequest = { show.value = false }, DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true,
            )
        ) {

            Card(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Редактирование урока",
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    var name by remember { mutableStateOf(lesson.name) }
                    Text("Название")
                    OutlinedTextField(value = name, onValueChange = { s -> name = s })
                    Text("Изображение")
                    Button(onClick = {}) { Text("Загрузить") }
                    Text("Уроки, которые нужно пройти, для разблокировки этого")
                    val lessons =
                        remember { mutableStateListOf<CourseCreatingState.Lesson>() }
                    Column() {
                        lessons.forEach { lesson ->
                            Text(lesson.name)
                        }
                    }
                    Button(onClick = {}) { Text("Добавить") }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = { show.value = false },
                        ) { Text("Отменить") }
                        TextButton(
                            onClick = { show.value = false },
                        ) { Text("Сохранить") }
                    }
                }
            }
        }
    }
}

@Composable
fun AddLessonDialog(show: MutableState<Boolean>) {
    if (show.value) {
        Dialog(
            onDismissRequest = { show.value = false }, DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true,
            )
        ) {

            Card(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Добавление урока",
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    var name by remember { mutableStateOf(" ") }
                    Text("Название")
                    OutlinedTextField(value = name, onValueChange = { s -> name = s })
                    Text("Изображение")
                    Button(onClick = {}) { Text("Загрузить") }
                    Text("Уроки, которые нужно пройти, для разблокировки этого")
                    val lessons =
                        remember { mutableStateListOf<CourseCreatingState.Lesson>() }
                    Column() {
                        lessons.forEach { lesson ->
                            Text(lesson.name)
                        }
                    }
                    Button(onClick = {}) { Text("Добавить") }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = { show.value = false },
                        ) { Text("Отменить") }
                        TextButton(
                            onClick = { show.value = false },
                        ) { Text("Добавить") }
                    }
                }
            }
        }
    }
}