package com.catoncat.studyapp.ui.screen.coursecreating

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
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
import coil3.compose.AsyncImage
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ExerciseType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun CourseCreatingScreen(
    viewModel: CourseCreatingViewModel = viewModel<CourseCreatingViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {
        is CourseCreatingState.Content -> CourseCreatingContentState(
            currentState,
            onUpdateCourse = {
                viewModel.onIntent(CourseCreatingIntent.UpdateCourse(currentState.course))
                backStack.remove(AppRoute.CourseCreating)
            },
            onNavigateToPreviousScreen = {
                backStack.remove(AppRoute.CourseCreating)
            }
        )

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
fun CourseCreatingContentState(
    content: CourseCreatingState.Content,
//    onCreateAnswer: (CourseCreatingState.Answer) -> Unit,
//    onUpdateAnswer: (CourseCreatingState.Answer) -> Unit,
//    onUpdateExercise: (CourseCreatingState.Exercise) -> Unit
//    onUpdateLesson: (CourseCreatingState.Lesson) -> Unit,
//    onCreateLesson: (CourseCreatingState.Lesson) -> Unit
    onUpdateCourse: () -> Unit,
    onNavigateToPreviousScreen: () -> Unit
) {
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
    val newLessonOffset = remember { mutableStateOf(Offset.Zero) }

    val clickedLesson = remember {
        mutableStateOf(
            CourseCreatingState.Lesson(
                0.0f,
                0.0f,
                "",
                "Без названия",
                0,
                persistentListOf()
            )
        )
    }

    val showAddDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showSaveOrNotSaveDialog = remember { mutableStateOf(false) }
    val lessons = remember { mutableStateOf(content.course.lessons) }
//    val showEditExercisesDialog = remember { mutableStateOf(false) }

    EditLessonDialog(
        "Редактирование урока",
        clickedLesson.value,
        showEditDialog,
//        onSaveButton = { lesson ->
//            onUpdateLesson(lesson)
//        },
        onSaveButton = null,
        onDeleteButton = { lesson ->
            content.course.lessons = content.course.lessons.remove(lesson)
            lessons.value = content.course.lessons;
        }
    )
    EditLessonDialog(
        "Добавление урока",
        CourseCreatingState.Lesson(
            Offset.Zero.x,
            Offset.Zero.y,
//            (newLessonOffset.value).x,
//            (newLessonOffset.value).y,
            "",
            "Без названия",
            0,
            persistentListOf()
        ),
        showAddDialog,
        onSaveButton = { lesson ->
            content.course.lessons = content.course.lessons.add(lesson)
            lessons.value = content.course.lessons
//            onCreateLesson(lesson)
        },
        onDeleteButton = null
    )
    ChooseSaveOrNotSaveDialog(
        showSaveOrNotSaveDialog,
        onSave = onUpdateCourse,
        onNotSave = onNavigateToPreviousScreen
    )
//    AddLessonDialog(showAddDialog)
//    EditExercisesDialog(content.exercises, showEditExercisesDialog)

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
                        newLessonOffset.value -= pan
                        Log.d("CourseCreatingState", offset.toString())
                    }
                }
//                .transformable(rememberTransformableState { zoomChange, panChange, rotationChange ->
//                    scale.floatValue *= zoomChange
//                    offset.value += panChange
//                }, lockRotationOnZoomPan = true, enabled = true)
        ) {
            val error = remember { mutableStateOf(false) }
            if (error.value) {

                Canvas(Modifier.fillMaxSize()) {
                    drawRect(Color.DarkGray)
                }
            }

            AsyncImage(
                model = content.course.background,
                contentDescription = "Course background",
                modifier = Modifier.fillMaxSize(),
                onError = { error.value = true })

            lessons.value.forEach { lesson ->
//                when (it) {
//                    is CourseCreatingState.Lesson -> {
                val x = remember { mutableFloatStateOf(lesson.x) }
                val y = remember { mutableFloatStateOf(lesson.y) }
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
                            lesson.x += o.x
                            lesson.y += o.y
                        })
                ) { Text("1") }
//            }
//        }

//                    CourseCreatingState.Lesson.Error -> TODO()
//                }
            }
        }
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Box(Modifier.fillMaxWidth()) {
                SmallFloatingActionButton(
                    onClick = onNavigateToPreviousScreen,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { Text("<-") }
                Text("Редактирование курса", modifier = Modifier.align(Alignment.Center))

                SmallFloatingActionButton(
                    onClick = onUpdateCourse,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { Text("Сохранить") }
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                onClick = {
                    showAddDialog.value = true
                },
                modifier = Modifier.padding(end = 5.dp)
//                modifier = Modifier.align(Alignment.BottomEnd)
            ) { Text("+") }
            FloatingActionButton(
                onClick = {
                    showAddDialog.value = true
                },
//                modifier = Modifier.align(Alignment.BottomEnd)
            ) { Text("⚙") }
        }

//        BottomAppBar(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            actions = {
//                Button(
//                    onClick = {
//                        showAddDialog.value = true
//                    },
////                    .padding(10.dp)
//                ) { Text("Добавить урок") }
//                Button(
//                    onClick = {
//                        showEditExercisesDialog.value = true
//                    },
////                    .padding(10.dp)
//                ) { Text("Задания") }
//            },
//        )
    }
}

@Composable
fun EditLessonDialog(
    title: String,
    lesson: CourseCreatingState.Lesson,
    show: MutableState<Boolean>,
    onSaveButton: ((CourseCreatingState.Lesson) -> Unit)?,
    onDeleteButton: ((CourseCreatingState.Lesson) -> Unit)?
) {
//    val newLesson = lesson.copy()
    if (show.value) {
        val name = remember { mutableStateOf(lesson.name) }
        val requiredLessons =
            remember { mutableStateListOf<CourseCreatingState.Lesson>() }
        val exercises = remember { mutableStateOf(lesson.exercises) }
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
//                        .height(500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = title,
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Text("Название")
                    OutlinedTextField(value = name.value, onValueChange = { value ->
                        name.value = value
                        lesson.name = name.value
//                        lesson.name = value
                    })
                    Text("Изображение")
                    Button(onClick = {}) { Text("Загрузить") }
                    Text("Уроки, которые нужно пройти, для разблокировки этого")

//                    LazyColumn() {
                    requiredLessons.forEach { lesson ->
//                            item {
                        Text(lesson.name)
//                            }
                    }
//                    }
                    Button(onClick = {
                    }) { Text("Добавить") }
                    Text("Задания")
//                    LazyColumn(
//                        Modifier
//                            .fillMaxWidth()
//
//                            .padding(5.dp)
//                    ) {


                    exercises.value.forEach { exercise ->
//                            item {
                        OutlinedCard(
//                            onClick = {
//                                show.value = !show.value
//                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp),
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                ) {

                                    Row {
                                        val exerciseName =
                                            remember { mutableStateOf(exercise.name) }

                                        Text("Название: ")
                                        OutlinedTextField(
                                            value = exerciseName.value,
                                            onValueChange = { value ->
                                                exerciseName.value = value
                                                exercise.name = value
                                            }
//                                                    textStyle = Typography.titleLarge
                                        )
                                    }

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text("Тип: ")
                                        val type =
                                            remember { mutableStateOf(exercise.type) }

                                        val expanded =
                                            remember { mutableStateOf(false) }

                                        Text(
                                            modifier = Modifier
                                                .clickable(onClick = {
                                                    expanded.value = true
                                                })
                                                .border(width = 1.dp, color = Color.Gray),
                                            text = type.value
                                        )
//                                        OutlinedTextField(value = type.value, onValueChange = {}, Modifier.clickable(onClick = {expanded.value=!expanded.value}))

                                        DropdownMenu(
//                                            expanded = expanded.value,
//                                            onExpandedChange = { value->
//                                                expanded.value =
//                                                    value
//                                            }) {
                                            expanded = expanded.value,
                                            onDismissRequest = { expanded.value = false }
                                        ) {
                                            ExerciseType.entries.forEach { exerciseType ->
                                                DropdownMenuItem(
                                                    text = { Text(exerciseType.name) },
                                                    onClick = {
                                                        type.value = exerciseType.name
                                                        exercise.type = exerciseType.name
                                                        expanded.value = false
                                                    })
                                            }
                                        }
                                    }
                                    val expandExercise = remember { mutableStateOf(false) }

                                    if (expandExercise.value) {

                                        Text("Текст: ")
                                        val exerciseText =
                                            remember { mutableStateOf(exercise.text) }
                                        OutlinedTextField(
                                            value = exerciseText.value,
                                            onValueChange = { value ->
                                                exerciseText.value = value
                                                exercise.text = exerciseText.value
                                            })

                                        Text("Варианты ответа")

                                        val answers = remember { mutableStateOf(exercise.answers) }

//                                        LazyColumn() {
                                        answers.value.forEach { answer ->
                                            OutlinedCard(Modifier.padding(5.dp)) {
//                                                item {
                                                Column {
                                                    Row {
                                                        Text("Текст ответа: ")
                                                        val answerText =
                                                            remember { mutableStateOf(answer.text) }
                                                        OutlinedTextField(
                                                            value = answerText.value,
                                                            onValueChange = { value ->
                                                                answerText.value = value
                                                                answer.text = answerText.value
                                                            })
                                                    }
                                                    Row {
                                                        Text("Правильный? ")
                                                        val checked =
                                                            remember { mutableStateOf(answer.correct) }
                                                        Checkbox(
                                                            checked = checked.value,
                                                            onCheckedChange = {
                                                                checked.value = !checked.value
                                                                answer.correct = checked.value
                                                            })
                                                    }
                                                    Button(onClick = {
                                                        answers.value = answers.value.remove(answer)
                                                        exercise.answers = answers.value
                                                    }, Modifier.fillMaxWidth()) { Text("Удалить") }
                                                }
                                            }
                                        }

                                        Button(onClick = {
                                            exercise.answers = exercise.answers.add(
                                                CourseCreatingState.Answer(0, "", false)
                                            )
                                            answers.value = exercise.answers
                                        }) { Text("Добавить") }
                                    }
//                                            }
//                                        }
                                    Row(Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = {
                                                exercises.value = exercises.value.remove(exercise)
                                            },
                                            Modifier.weight(0.7f)
                                        ) {
                                            Text("Удалить")
                                        }
                                        Button(
                                            onClick = {
                                                expandExercise.value = !expandExercise.value
                                            },
                                            Modifier.weight(0.3f)
                                        ) {
                                            Text(
                                                if (expandExercise.value) {
                                                    "^"
                                                } else {
                                                    "v"
                                                }
                                            )
                                        }
                                    }
                                }

                            }


                        }
//                            }
//                        }
                    }
                    Button(onClick = {
                        exercises.value = exercises.value.add(
                            CourseCreatingState.Exercise(
                                0, lesson.id, "Без названия", "",
                                ExerciseType.Choose.name, persistentListOf()
                            )
                        )
                        lesson.exercises = exercises.value
                    }) { Text("Добавить") }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (onDeleteButton != null) {
                            Button(
                                onClick = {
                                    show.value = false
                                    onDeleteButton(lesson)
                                },
                            ) { Text("Удалить") }
                            TextButton(
                                onClick = {
                                    show.value = false
                                },
                            ) { Text("Сохранить") }
                        }
                        if (onSaveButton != null) {
                            TextButton(
                                modifier = Modifier.padding(end = 5.dp),
                                onClick = {
                                    show.value = false
                                },
                            ) { Text("Отменить") }


                            TextButton(
                                onClick = {
                                    show.value = false
                                    onSaveButton(lesson)
                                },
                            ) { Text("Добавить") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseSaveOrNotSaveDialog(
    show: MutableState<Boolean>,
    onSave: () -> Unit,
    onNotSave: () -> Unit
) {
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
//                        .height(500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Сохранить изменения?",
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = {
                                show.value = false
                            },
                        ) { Text("Продолжать изменять") }
                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = {
                                show.value = false
                                onNotSave()
                            },
                        ) { Text("Удалить изменения") }
                        TextButton(
                            onClick = {
                                show.value = false
                                onSave()
                            },
                        ) { Text("Сохранить") }
                    }
                }
            }
        }
    }
}

//@Composable
//fun AddLessonDialog(show: MutableState<Boolean>) {
//    if (show.value) {
//        Dialog(
//            onDismissRequest = { show.value = false }, DialogProperties(
//                dismissOnBackPress = true,
//                dismissOnClickOutside = false,
//                usePlatformDefaultWidth = true,
//            )
//        ) {
//
//            Card(
//                Modifier
//                    .fillMaxWidth()
//                    .height(IntrinsicSize.Max)
//            ) {
//                Column(
//                    Modifier
//                        .fillMaxSize()
//                        .padding(10.dp)
//                ) {
//                    Text(
//                        text = "Добавление урока",
//                        style = Typography.headlineMedium,
//                        modifier = Modifier.padding(bottom = 5.dp)
//                    )
//                    var name by remember { mutableStateOf(" ") }
//                    Text("Название")
//                    OutlinedTextField(value = name, onValueChange = { s -> name = s })
//                    Text("Изображение")
//                    Button(onClick = {}) { Text("Загрузить") }
//                    Text("Уроки, которые нужно пройти, для разблокировки этого")
//                    val lessons =
//                        remember { mutableStateListOf<CourseCreatingState.Lesson>() }
//                    Column() {
//                        lessons.forEach { lesson ->
//                            Text(lesson.name)
//                        }
//                    }
//                    Button(onClick = {}) { Text("Добавить") }
//
//                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//
//                        TextButton(
//                            modifier = Modifier.padding(end = 5.dp),
//                            onClick = { show.value = false },
//                        ) { Text("Отменить") }
//                        TextButton(
//                            onClick = { show.value = false },
//                        ) { Text("Добавить") }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun EditExercisesDialog(
//    exercises: PersistentList<CourseCreatingState.Exercise>,
//    show: MutableState<Boolean>,
////    onUpdateExercise: (CourseCreatingState.Exercise) -> Unit
//) {
//    val exercises = remember { mutableStateOf(exercises) }
//    if (show.value) {
//        Dialog(
//            onDismissRequest = { show.value = false }, DialogProperties(
//                dismissOnBackPress = true,
//                dismissOnClickOutside = false,
//                usePlatformDefaultWidth = true,
//            )
//        ) {
//            Card(
//                Modifier
//                    .fillMaxWidth()
//                    .height(IntrinsicSize.Max)
//            ) {
//                Column(
//                    Modifier
//                        .fillMaxSize()
//                        .padding(10.dp)
//                ) {
//                    var editExercise = remember {
//                        mutableStateOf(
//                            CourseCreatingState.Exercise(
//                                null, "", "", "",
//                                persistentListOf()
//                            )
//                        )
//                    }
//                    val showEditExercise = remember { mutableStateOf(false) }
//                    if (showEditExercise.value) {
//                        Row(Modifier.fillMaxWidth()) {
//                            Button(
//                                onClick = { showEditExercise.value = false }
//                            ) { Text("<-") }
//                        }
//                        Column {
//                            val text = remember { mutableStateOf(editExercise.value.text) }
//                            Text("Text")
//                            OutlinedTextField(
//                                value = text.value,
//                                onValueChange = { value -> text.value = value })
//                            Text("Answers")
//                            val answers = remember { mutableStateOf(editExercise.value.answers) }
//                            LazyColumn(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .height(350.dp)
//                            ) {
//                                answers.value.forEach { answer ->
//                                    item {
//                                        val answerText = remember { mutableStateOf(answer.text) }
//                                        val checked =
//                                            remember { mutableStateOf(answer.correct) }
//                                        OutlinedCard {
//                                            Column {
//                                                OutlinedTextField(
//                                                    value = answerText.value,
//                                                    onValueChange = { value ->
//                                                        answerText.value = value
////                                                        CourseCreatingState.Answer(
////                                                            answer.id,
////                                                            answerText.value,
////                                                            checked.value
////                                                        )
//                                                    })
//                                                Row {
//                                                    Text("Correct")
//                                                    Checkbox(
//                                                        checked = checked.value,
//                                                        onCheckedChange = { value ->
//                                                            checked.value = value
////                                                        CourseCreatingIntent.UpdateAnswer(
////                                                            CourseCreatingState.Answer(
////                                                                answer.id,
////                                                                answerText.value,
////                                                                checked.value
////                                                            )
////                                                        )
//                                                        })
//                                                }
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                                Button(onClick = {
//                                    answers.value = answers.value.add(
//                                        CourseCreatingState.Answer(
//                                            null,
//                                            "",
//                                            false
//                                        )
//                                    )
////                                    CourseCreatingIntent.UpdateAnswer(
////                                        CourseCreatingState.Answer(
////                                            answer.id,
////                                            answerText.value,
////                                            checked.value
////                                        )
////                                    )
//                                }) { Text("+") }
//                            }
//                        }
//                    } else {
//                        Row(Modifier.fillMaxWidth()) {
//                            Button(
//                                onClick = { show.value = false }, Modifier.padding(end = 5.dp)
//                            ) { Text("<-") }
//                            Text("Задания", style = Typography.headlineMedium)
//                        }
//
//                        LazyColumn(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(350.dp)
//                                .padding(5.dp)
//                        ) {
//                            exercises.value.forEach { exercise ->
//                                item {
//                                    OutlinedCard(
//                                        onClick = {
//                                            editExercise.value = exercise
//                                            showEditExercise.value = true
//                                        },
//                                        Modifier
//                                            .fillMaxWidth()
//                                            .padding(bottom = 5.dp)
//                                    ) {
//                                        Column(
//                                            Modifier
//                                                .fillMaxWidth()
//                                                .padding(5.dp)
//                                        ) {
//                                            Text(exercise.name, style = Typography.titleLarge)
//                                            Row(Modifier.fillMaxWidth()) {
//                                                Text("Тип: ")
//                                                Text(exercise.type)
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                        }
//                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                            Button(
//                                onClick = {
//                                    exercises.value = exercises.value.add(
//                                        CourseCreatingState.Exercise(
//                                            null,
//                                            "Без названия",
//                                            "Без текста",
//                                            "Choose",
//                                            persistentListOf()
//                                        )
//                                    )
//                                }
//                            ) { Text("+") }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
