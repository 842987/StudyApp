package com.catoncat.studyapp.ui.screen.coursecreating

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.catoncat.studyapp.domain.entities.AnswerEntity
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.ExerciseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ExerciseType
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun CourseCreatingScreen(
    viewModel: CourseCreatingViewModel = viewModel<CourseCreatingViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState()

    val updateKey = remember { mutableIntStateOf(0) }


    LaunchedEffect(updateKey.intValue) {
        viewModel.getData()
    }

    when (val currentState = state) {
        is CourseCreatingState.Content -> CourseCreatingContentState(
            currentState,
            onUpdateCourse = { course ->
                viewModel.onIntent(CourseCreatingIntent.UpdateCourse(course))
                Log.d("CourseCreatingScreen", "Задания")
                //                Log.d("CourseCreatingScreen", course.lessons.size.toString())
                course.lessons.forEach { lessonEntity ->
                    lessonEntity.exercises.forEach { exerciseEntity ->
                        Log.d("CourseCreatingScreen", exerciseEntity.name)
                    }
                }
//                    updateKey.intValue++
                //                backStack.remove(AppRoute.CourseCreating)
            },
            onNavigateToPreviousScreen = {
                backStack.remove(AppRoute.CourseCreating)
            }
        )

        is CourseCreatingState.Error -> CourseCreatingErrorState(currentState.reason)
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
fun CourseCreatingErrorState(reason: String) {
    Box(Modifier.fillMaxSize()) {
        Text(reason, Modifier.align(Alignment.Center))
    }
}

@Composable
fun CourseCreatingContentState(
    content: CourseCreatingState.Content,
    //    onCreateAnswer: (CourseCreatingState.Answer) -> Unit,
    //    onUpdateAnswer: (CourseCreatingState.Answer) -> Unit,
    //    onUpdateExercise: (CourseCreatingState.Exercise) -> Unit
    //    onUpdateLesson: (CourseCreatingState.Lesson) -> Unit,
    //    onCreateLesson: (CourseCreatingState.Lesson) -> Unit
    onUpdateCourse: (course: CourseEntity) -> Unit,
//    onAddLesson: (lesson: LessonEntity) -> Unit,
//    onAddExercise(lesson)
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

//    val painter = rememberAsyncImagePainter(content.course.backgroundUrl)
//    painter.state.collectAsState().value.painter!!.intrinsicSize

    val clickedLesson = remember {
        mutableStateOf(
            LessonEntity(
                null,
                "",
                "",
                "",
                "",
                2500.dp.value,
                2500.dp.value,
                persistentListOf(),
                persistentListOf()
            )
            //            CourseCreatingState.Lesson(
            //                0.0f,
            //                0.0f,
            //                "",
            //                "Без названия",
            //                0,
            //                persistentListOf()
            //        )
        )
    }
    val clickedLessonIndex = remember { mutableIntStateOf(-1) }

    val showAddDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showSaveOrNotSaveDialog = remember { mutableStateOf(false) }
    val showEditCourseDialog = remember { mutableStateOf(false) }
    val lessons = remember { mutableStateOf(content.course.lessons) }
    val courseName = remember { mutableStateOf(content.course.name) }
    //    val showEditExercisesDialog = remember { mutableStateOf(false) }

    val changed = remember { mutableStateOf(false) }

    EditCourseDialog(
        course = content.course,
        show = showEditCourseDialog,
        onSave = { name, description, backgroundUrl ->
            content.course.name = name
            courseName.value = name
            content.course.description = description
            content.course.backgroundUrl = backgroundUrl
        }
    )

    val updateLessonsKey = remember { mutableIntStateOf(0) }



    EditLessonDialog(
        "Редактирование урока",
        clickedLesson.value,
        content.course.lessons.toMutableList(),
        showEditDialog,
        //        onSaveButton = { lesson ->
        //            onUpdateLesson(lesson)
        //        },
        onSaveButton = null,
        onDeleteButton = { index, lesson ->
            //            content.course.lessons = content.course.lessons.remove(lesson)
            //            content.course.lessons = content.course.lessons.remove(lesson)
            //            lessons.value = content.course.lessons
            lesson.deleted = true
            val newLessons = content.course.lessons.toMutableList()
            newLessons[index] = lesson
            content.course.lessons = newLessons.toPersistentList()
            updateLessonsKey.intValue++
//                lessons.value = content.course.lessons
        },
        index = clickedLessonIndex.intValue,
        onUpdateLesson = { index, entity ->
            val newLessons = content.course.lessons.toMutableList()
            newLessons[index] = entity
            content.course.lessons = newLessons.toPersistentList()
        },
        edit = true
    )
    EditLessonDialog(
        "Добавление урока",
        //        CourseCreatingState.Lesson(
        //            Offset.Zero.x,
        //            Offset.Zero.y,
        ////            (newLessonOffset.value).x,
        ////            (newLessonOffset.value).y,
        //            "",
        //            "Без названия",
        //            0,
        //            persistentListOf()
        //        ),
        LessonEntity(
            null,
            "",
            "",
            "",
            "",
            2500.dp.value,
            2500.dp.value,
            persistentListOf(),
            persistentListOf()
        ),
        content.course.lessons.toMutableList(),
        showAddDialog,
        onSaveButton = { lesson ->
            Log.d("CourseCreatingContentState", lesson.toString())
            content.course.lessons = content.course.lessons.add(lesson)
            lessons.value = content.course.lessons

            Log.d("CourseCreatingContentState", content.course.lessons.size.toString())
            //            onCreateLesson(lesson)
        },
        onDeleteButton = { _, _ -> },
        onUpdateLesson = { index, lessonEntity ->
//                val newLessons = content.course.lessons.toMutableList()
//                newLessons[index] = entity
//                content.course.lessons = newLessons.toPersistentList()
            content.course.lessons = content.course.lessons.add(lessonEntity)
        },
        index = clickedLessonIndex.intValue,
        edit = false
    )
    ChooseSaveOrNotSaveDialog(
        showSaveOrNotSaveDialog,
        onSave = { onUpdateCourse(content.course) },
        onNotSave = onNavigateToPreviousScreen
    )
    //    AddLessonDialog(showAddDialog)
    //    EditExercisesDialog(content.exercises, showEditExercisesDialog)

    Box(Modifier.fillMaxSize()) {


        Box(
            Modifier
                .requiredSize(2500.dp, 2500.dp)
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
                    drawRect(Color.Transparent)
                }
            }

//            Image()

            AsyncImage(
                model = content.course.backgroundUrl,
                contentDescription = "Course background",
                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Fit,
                onError = {
                    error.value = true
                })

            key(updateLessonsKey.intValue) {
                lessons.value.forEachIndexed { index, lesson ->
                    if (!lesson.deleted) {

                        //                when (it) {
                        //                    is CourseCreatingState.Lesson -> {
                        val x = remember { mutableFloatStateOf(lesson.x) }
                        val y = remember { mutableFloatStateOf(lesson.y) }
                        val lessonImageLoadingError = remember { mutableStateOf(false) }
                        if (!lessonImageLoadingError.value) {
                            Column(
                                Modifier
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
                            ) {
                                AsyncImage(
                                    model = if (!lesson.opened) lesson.imageUrlOnLocked else if (!lesson.completed) lesson.imageUrl else lesson.imageUrlOnCompleted,
                                    contentDescription = "Lesson",
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            clickedLesson.value = lesson
                                            clickedLessonIndex.intValue = index
                                            showEditDialog.value = true
                                            changed.value = true
                                        }),
                                    onError = {
                                        lessonImageLoadingError.value = true
                                    })
                                Text(lesson.name, color = Color.White, textAlign = TextAlign.Center)
                            }
                        } else {
                            Button(
                                onClick = {
                                    clickedLesson.value = lesson
                                    clickedLessonIndex.intValue = index
                                    showEditDialog.value = true
                                    changed.value = true
                                },
                                Modifier
                                    //                        .size(50.dp)
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
                            ) { Text(lesson.name) }
                        }
                        //            }
                        //        }

                        //                    CourseCreatingState.Lesson.Error -> TODO()
                        //                }
                    }
                }
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
                    onClick = {
                        if (changed.value) {
                            showSaveOrNotSaveDialog.value = true
                        } else {
                            onNavigateToPreviousScreen()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { Text("<-") }

                Text(courseName.value, modifier = Modifier.align(Alignment.Center))

                SmallFloatingActionButton(
                    onClick = {
                        onUpdateCourse(content.course)
                        changed.value = false
                    },
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
                    changed.value = true
                },
                modifier = Modifier.padding(end = 5.dp)
                //                modifier = Modifier.align(Alignment.BottomEnd)
            ) { Text("+") }
            FloatingActionButton(
                onClick = {
                    showEditCourseDialog.value = true
                    changed.value = true
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
    editingLesson: LessonEntity,
    lessons: MutableList<LessonEntity>,
    show: MutableState<Boolean>,
    onSaveButton: ((LessonEntity) -> Unit)?,
    onDeleteButton: (index: (Int), (LessonEntity)) -> Unit,
    index: Int,
    onUpdateLesson: (index: Int, (LessonEntity)) -> Unit,
    edit: Boolean
) {
    //    val newLesson = lesson.copy()
    if (show.value) {
        val name = remember { mutableStateOf(editingLesson.name) }
        //        val requiredLessons =
        //            remember { mutableStateListOf<LessonEntity>() }
        val exercises = remember { mutableStateOf(editingLesson.exercises) }
        Dialog(
            onDismissRequest = { show.value = false },
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true,
                decorFitsSystemWindows = false
            ),
        ) {

            Card(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .imePadding()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        //                        .height(500.dp)
                        .verticalScroll(rememberScrollState()),
//                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = title,
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    OutlinedTextField(value = name.value, onValueChange = { value ->
                        name.value = value
                        editingLesson.name = name.value
                        //                        lesson.name = value
                    }, label = { Text("Название") })

                    val imageUrl = remember { mutableStateOf(editingLesson.imageUrl) }
                    OutlinedTextField(
                        value = imageUrl.value,
                        onValueChange = { value ->
                            imageUrl.value = value
                            editingLesson.imageUrl = imageUrl.value
                        },
                        label = { Text("Url на изображение, когда урок открыт для прохождения") },
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    val imageUrlOnLocked =
                        remember { mutableStateOf(editingLesson.imageUrlOnLocked) }
                    OutlinedTextField(value = imageUrlOnLocked.value, onValueChange = { value ->
                        imageUrlOnLocked.value = value
                        editingLesson.imageUrlOnLocked = imageUrlOnLocked.value
                    }, label = { Text("Url на изображение, когда урок закрыт") })

                    val imageUrlOnCompleted =
                        remember { mutableStateOf(editingLesson.imageUrlOnCompleted) }
                    OutlinedTextField(value = imageUrlOnCompleted.value, onValueChange = { value ->
                        imageUrlOnCompleted.value = value
                        editingLesson.imageUrlOnCompleted = imageUrlOnCompleted.value
                    }, label = { Text("Url на изобажение, когда урок пройден") })

                    Text("Уроки, которые нужно пройти, для разблокировки этого")

                    val showListOfLessons = remember { mutableStateOf(false) }

                    lessons.remove(editingLesson)

                    Button(onClick = {
                        showListOfLessons.value = !showListOfLessons.value
                    }) {
                        if (showListOfLessons.value) {
                            Text("^")
                        } else {
                            Text("v")
                        }
                    }

                    //                    val requiredLessons =
                    //                        remember { mutableStateOf(persistentListOf<LessonEntity>()) }

                    //                    val requiredLessons = mutableListOf<LessonEntity>()

                    //                    LazyColumn() {
                    if (showListOfLessons.value) {
                        OutlinedCard {


                            lessons.forEach { requiredLesson ->
//                                if(requiredLesson.)
                                val checked = remember {
                                    mutableStateOf(
                                        if (requiredLesson.id == null) false else editingLesson.requiredLessons.contains(
                                            requiredLesson.id
                                        )
                                    )
                                }
                                //                            item {
                                Row(Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)) {
                                    Text(text = "${requiredLesson.name} (ID: ${requiredLesson.id ?: "ещё нет"})")
                                    Checkbox(
                                        enabled = requiredLesson.id != null,
                                        checked = /*requiredLessons.value.contains(requiredLesson)*/checked.value,
                                        onCheckedChange = { value ->
//                                            if (!value && checked.value) {
//                                                requiredLesson.
//                                            }
                                            checked.value = value
                                            if (value) {
                                                editingLesson.requiredLessons =
                                                    editingLesson.requiredLessons.add(requiredLesson.id!!)

                                                //                                        requiredLessons.value =
                                                //                                            requiredLessons.value.add(requiredLesson)
                                            } else {
                                                editingLesson.requiredLessons =
                                                    editingLesson.requiredLessons.remove(
                                                        requiredLesson.id!!
                                                    )
                                                //                                        requiredLessons.value =
                                                //                                            requiredLessons.value.remove(requiredLesson)
                                            }
                                        })
                                }

                                //                            }
                            }
                        }
                    }
                    //                    }

//                    if (edit) {

                    Text("Задания")
                    //                    LazyColumn(
                    //                        Modifier
                    //                            .fillMaxWidth()
                    //
                    //                            .padding(5.dp)
                    //                    ) {


                    exercises.value.forEach { exercise ->
                        val showExercise = remember { mutableStateOf(!exercise.deleted) }
                        if (showExercise.value) {
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


                                            OutlinedTextField(
                                                value = exerciseName.value,
                                                onValueChange = { value ->
                                                    exerciseName.value = value
                                                    exercise.name = value
                                                }, label = { Text("Название") }
                                                //                                                    textStyle = Typography.titleLarge
                                            )
                                        }

                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                        ) {

                                            Text("Тип: ")

                                            val type =
                                                remember { mutableStateOf(exercise.typeName) }

                                            val expanded =
                                                remember { mutableStateOf(false) }

                                            Text(
                                                modifier = Modifier
                                                    .clickable(onClick = {
                                                        expanded.value = true
                                                    })
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.Gray
                                                    ),
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
                                                onDismissRequest = {
                                                    expanded.value = false
                                                }
                                            ) {
                                                ExerciseType.entries.forEach { exerciseType ->
                                                    DropdownMenuItem(
                                                        text = { Text(exerciseType.name) },
                                                        onClick = {
                                                            type.value = exerciseType.name
                                                            exercise.typeName =
                                                                exerciseType.name
                                                            expanded.value = false
                                                        })
                                                }
                                            }
                                        }
                                        val expandExercise =
                                            remember { mutableStateOf(false) }

                                        if (expandExercise.value) {

                                            val exerciseText =
                                                remember { mutableStateOf(exercise.text) }
                                            OutlinedTextField(
                                                value = exerciseText.value,
                                                onValueChange = { value ->
                                                    exerciseText.value = value
                                                    exercise.text = exerciseText.value
                                                }, label = { Text("Текст") })

                                            Text("Варианты ответа")

                                            val answers =
                                                remember { mutableStateOf(exercise.answers) }

                                            //                                        LazyColumn() {
//                                                Card {


                                            answers.value.forEach { answer ->
                                                val showAnswer =
                                                    remember { mutableStateOf(!answer.deleted) }
                                                if (showAnswer.value) {
                                                    OutlinedCard(Modifier.padding(bottom = 5.dp)) {
                                                        //                                                item {
                                                        Column(Modifier.padding(5.dp)) {
                                                            Row {
                                                                val answerText =
                                                                    remember {
                                                                        mutableStateOf(
                                                                            answer.text
                                                                        )
                                                                    }
                                                                OutlinedTextField(
                                                                    value = answerText.value,
                                                                    onValueChange = { value ->
                                                                        answerText.value =
                                                                            value
                                                                        answer.text =
                                                                            answerText.value
                                                                    },
                                                                    label = { Text("Текст ответа") })
                                                            }
                                                            Row {
                                                                Text("Правильный? ")
                                                                val checked =
                                                                    remember {
                                                                        mutableStateOf(
                                                                            answer.correct
                                                                        )
                                                                    }
                                                                Checkbox(
                                                                    checked = checked.value,
                                                                    onCheckedChange = {
                                                                        checked.value =
                                                                            !checked.value
                                                                        answer.correct =
                                                                            checked.value
                                                                    })
                                                            }
                                                            Button(
                                                                onClick = {
                                                                    //                                                        answers.value = answers.value.remove(answer)
                                                                    //                                                                exercise.answers =
                                                                    //                                                                    exercise.answers.remove(answer)
                                                                    //                                                                answers.value = exercise.answers
                                                                    answer.deleted = true
                                                                    showAnswer.value = false
                                                                    //                                                        exercise.answers = answers.value
                                                                },
                                                                Modifier.fillMaxWidth()
                                                            ) { Text("Удалить") }
                                                        }
                                                    }
                                                }
                                            }
//                                                }
                                            Button(onClick = {
                                                //                                            exercise.answers = exercise.answers.add(
                                                //                                                CourseCreatingState.Answer(0, "", false)
                                                //                                            )
                                                exercise.answers =
                                                    exercise.answers.add(
                                                        AnswerEntity(
                                                            null,
                                                            "",
                                                            false
                                                        )
                                                    )
                                                answers.value = exercise.answers
                                            }) { Text("Добавить") }
                                        }
                                        //                                            }
                                        //                                        }
                                        Row(Modifier.fillMaxWidth()) {
                                            Button(
                                                onClick = {
                                                    //                                                lesson.exercises = lesson.exercises.remove(exercise)
                                                    //                                                exercises.value = lesson.exercises
                                                    exercise.deleted = true
                                                    showExercise.value = false
                                                    //                                                exercises.remove(exercise)
                                                },
                                                Modifier.weight(0.7f)
                                            ) {
                                                Text("Удалить")
                                            }
                                            Button(
                                                onClick = {
                                                    expandExercise.value =
                                                        !expandExercise.value
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
                    }
                    Button(onClick = {
                        //                        exercises.value = exercises.value.add(
                        //                            CourseCreatingState.Exercise(
                        //                                0, lesson.id, "Без названия", "",
                        //                                ExerciseType.Choose.name, persistentListOf()
                        //                            )
                        //                        )
                        //                        lesson.exercises = exercises.value
                        editingLesson.exercises = editingLesson.exercises.add(
                            ExerciseEntity(
                                null, "Без названия", "",
                                ExerciseType.Choose.name, persistentListOf<AnswerEntity>()
                            )
                        )
                        exercises.value = editingLesson.exercises
                        //                        Log.d("EditLessonDialog", lesson.exercises.size.toString())
                    }) { Text("Добавить") }
//                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (edit) {
                            Button(
                                onClick = {
                                    show.value = false
                                    onDeleteButton(index, editingLesson)
                                },
                            ) { Text("Удалить") }
                            TextButton(
                                onClick = {
                                    show.value = false
                                    onUpdateLesson(index, editingLesson)
                                },
                            ) { Text("Сохранить") }
                        }
                        if (!edit && onSaveButton != null) {
                            TextButton(
                                modifier = Modifier.padding(end = 5.dp),
                                onClick = {
                                    show.value = false
                                },
                            ) { Text("Отменить") }


                            TextButton(
                                onClick = {
                                    show.value = false
                                    onSaveButton(editingLesson)
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
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Вы не сохранили изменения. Не хотите ли этого сделать?",
                        style = Typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = {
                                show.value = false
                                onNotSave()
                            },
                        ) { Text("Выйти") }
                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = {
                                show.value = false
                            },
                        ) { Text("Продолжить изменять") }
//                        TextButton(
//                            onClick = {
//                                show.value = false
//                                onSave()
//                            },
//                        ) { Text("Сохранить") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCourseDialog(
    course: CourseEntity,
    show: MutableState<Boolean>,
    onSave: (name: String, description: String, backgroundUrl: String) -> Unit
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
                    Text("Редактирование курса", style = Typography.headlineMedium)

                    val name = remember { mutableStateOf(course.name) }
//                    Column(Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { value -> name.value = value },
                        label = { Text("Название: ") })
//                    }
                    val description = remember { mutableStateOf(course.description) }
//                    Column(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { value -> description.value = value },
                        label = { Text("Описание: ") })
//                    }
                    val backgroundUrl = remember { mutableStateOf(course.backgroundUrl) }
//                    Column(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = backgroundUrl.value,
                        onValueChange = { value -> backgroundUrl.value = value },
                        label = { Text("URL на картинку для фона: ") })
//                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            modifier = Modifier.padding(end = 5.dp),
                            onClick = {
                                show.value = false
                            },
                        ) { Text("Отменить") }
                        TextButton(
                            onClick = {
                                show.value = false
                                onSave(name.value, description.value, backgroundUrl.value)
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
