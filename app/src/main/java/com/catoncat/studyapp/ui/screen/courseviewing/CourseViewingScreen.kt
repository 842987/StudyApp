package com.catoncat.studyapp.ui.screen.courseviewing

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.catoncat.studyapp.data.dto.ExerciseDto
import com.catoncat.studyapp.domain.entities.ExerciseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ExerciseType
import kotlin.math.roundToInt

@Composable
fun CourseViewingScreen(
    viewModel: CourseViewingViewModel = viewModel<CourseViewingViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    val chosenLesson = remember { mutableStateOf<LessonEntity?>(null) }

    if (chosenLesson.value == null) {
        when (val currentState = state) {
            is CourseViewingState.Content -> CourseViewingContentState(
                currentState,
                onChooseLesson = { lesson ->
                    chosenLesson.value = lesson
                },
                onNavigateToPreviousScreen = {
                    backStack.remove(AppRoute.CourseViewing)
                }
            )

            is CourseViewingState.Error -> CourseViewingErrorState(currentState.reason)
            CourseViewingState.Loading -> CourseViewingLoadingState()
        }
    } else {
        val lessonCompleted = remember { mutableStateOf(false) }
        if (!lessonCompleted.value) {
            LessonView(lesson = chosenLesson.value!!, onLessonCompleted = {
                lessonCompleted.value = true
                viewModel.onIntent(CourseViewingIntent.CompleteLesson(chosenLesson.value?.id!!))
            })
        } else {
            LessonCompletedScreen(onNextButtonClicked = {
                chosenLesson.value = null
                viewModel.getData()
            })
        }
    }
}

@Composable
fun CourseViewingLoadingState() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CourseViewingErrorState(reason: String) {
    Box(Modifier.fillMaxSize()) {
        Text(reason, Modifier.align(Alignment.Center))
    }
}

@Composable
fun CourseViewingContentState(
    content: CourseViewingState.Content,
    onChooseLesson: (lesson: LessonEntity) -> Unit,
    onNavigateToPreviousScreen: () -> Unit
) {
    val scale = remember { mutableFloatStateOf(1.0f) }
    val offset = remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .requiredSize(10000.dp, 10000.dp)
                .scale(scale = scale.floatValue)
                .offset {
                    IntOffset(
                        offset.value.x.roundToInt(),
                        offset.value.y.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale.floatValue *= zoom
                        offset.value += pan
                    }
                }
        ) {
            val error = remember { mutableStateOf(false) }
            if (error.value) {
                Canvas(Modifier.fillMaxSize()) {
                    drawRect(Color.Transparent)
                }
            }

            AsyncImage(
                model = content.courseEntity.backgroundUrl,
                contentDescription = "Course background",
                modifier = Modifier.fillMaxSize(),
                onError = {
                    error.value = true
                })

            content.courseEntity.lessons.forEach { lesson ->
                if (!lesson.deleted) {
                    val lessonImageLoadingError = remember { mutableStateOf(false) }
                    if (!lessonImageLoadingError.value) {
                        Column(
                            Modifier
//                        .size(50.dp)
                                .offset {
                                    IntOffset(
                                        lesson.x.toInt(),
                                        lesson.y.toInt()
                                    )
                                }) {
                            AsyncImage(
                                model = if (!lesson.opened) lesson.imageUrlOnLocked else if (!lesson.completed) lesson.imageUrl else lesson.imageUrlOnCompleted,
                                contentDescription = "Lesson",
                                modifier = Modifier
                                    .clickable(enabled = lesson.opened, onClick = {
                                        onChooseLesson(lesson)
                                    }),
                                onError = {
                                    lessonImageLoadingError.value = true
                                })
                            Text(lesson.name, color = Color.White, textAlign = TextAlign.Center)
                        }
                    } else {
                        Button(
                            onClick = {
                                onChooseLesson(lesson)
                            },
                            Modifier
//                        .size(50.dp)
                                .offset {
                                    IntOffset(
                                        lesson.x.toInt(),
                                        lesson.y.toInt()
                                    )
                                },
                            enabled = lesson.opened,
                            border = if (lesson.completed) BorderStroke(
                                1.5.dp,
                                Color.Green
                            ) else null,
//                            shape = CircleShape
                        ) { Text(lesson.name) }
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
                    onClick = onNavigateToPreviousScreen,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { Text("<-") }

                Text(content.courseEntity.name, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun LessonView(lesson: LessonEntity, onLessonCompleted: () -> Unit) {
    if (lesson.exercises.isEmpty()) {
        Box(Modifier.fillMaxSize()) {
            Text(text = "Урок не содержит заданий", Modifier.align(Alignment.Center))
            Button(onClick = onLessonCompleted, Modifier.align(Alignment.BottomCenter)) {
                Text("Дальше")
            }
        }
    } else {
        val exerciseQueue =
            remember { mutableStateListOf<ExerciseEntity>(*lesson.exercises.toTypedArray()) }
//        exerciseQueue.addAll()
        val exerciseCount = lesson.exercises.size;
//        var currentLessonId = 0
//        val currentExercise = remember { mutableStateOf(lesson.exercises[currentLessonId]) }
//        val currentExercise = remember { derivedStateOf {  exerciseQueue.last() } }
        val rightChosen = remember { mutableStateOf(false) }
        val canAnswer = remember { mutableStateOf(false) }
        val lessonProgress =
            remember { derivedStateOf { 1 - exerciseQueue.size.toFloat() / exerciseCount } }
        val rightAnswers = remember {
            derivedStateOf {
//                currentExercise.value.answers.filter { answerEntity -> answerEntity.correct }
                exerciseQueue[0].answers.filter { answerEntity -> answerEntity.correct }
            }
        }
        val answered = remember { mutableStateOf(false) }

        Box(Modifier.fillMaxSize()) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)) {
                LinearProgressIndicator(
                    progress = { lessonProgress.value }, Modifier.fillMaxWidth()
                )

                Text(
                    text = exerciseQueue[0].name,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge
                )

                Spacer(Modifier.height(10.dp))

                Text(
//                text = currentExercise.value.text,
                    text = exerciseQueue[0].text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge
                )
            }

            when (exerciseQueue[0].typeName) {
                ExerciseType.Input.typeName -> {

                    val inputText = remember { mutableStateOf("") }
                    OutlinedTextField(value = inputText.value, onValueChange = { value ->
                        inputText.value = value
                        if (!value.isEmpty()) {
                            rightChosen.value = false
                            for (answerEntity in rightAnswers.value) {
                                if (answerEntity.text == value) {
                                    rightChosen.value = true
                                    break
                                }
                            }
                            canAnswer.value = true
                        } else {
                            canAnswer.value = false
                        }
                    }, Modifier.align(Alignment.Center), label = {Text("Ответ")})
                }

                ExerciseType.Choose.typeName -> {
                    val checkedAnswerId = remember { mutableLongStateOf(-1) }
                    Column(
                        Modifier.fillMaxWidth()
                            .selectableGroup()
                            .align(Alignment.Center)
                    ) {
                        exerciseQueue[0].answers.forEach { answer ->
                            IconToggleButton(
                                enabled = !answered.value,
                                checked = (answer.id!!) == checkedAnswerId.longValue,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        checkedAnswerId.longValue = answer.id!!
                                        rightChosen.value = answer.correct
                                        canAnswer.value = true
                                        Log.e("Test", "${answer.correct}")
                                    }
                                    canAnswer.value = checked
                                }, modifier = Modifier.fillMaxWidth()) {
                                Text(answer.text)
                            }
                        }
                    }
                }
            }

//            val isAnswerWrong = remember { mutableStateOf(false) }

            Column(Modifier.align(Alignment.BottomCenter)) {

                if (exerciseQueue[0].answers.isEmpty()) {
                    Button(
                        onClick = {
                            exerciseQueue.removeAt(0)
                            if (exerciseQueue.isEmpty()) {
                                onLessonCompleted()
                            } else {
                                canAnswer.value = false
                                answered.value = false
                                rightChosen.value = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
//                            .align(Alignment.BottomCenter)
                    ) {
                        Text("Дальше")
                    }
                } else if (answered.value) {
                    if (rightChosen.value) {
//                    val text =
//                        StringBuilder("Правильно. ${if (rightAnswers.size > 1) "Другие правильные ответы: " else "Другой правильный ответ: "}")
//                    rightAnswers.forEachIndexed { index, answerEntity ->
//                        text.append(answerEntity.text)
//                        if (index != rightAnswers.size-1) {
//                            text.append(", ")
//                        }
//                    }
//                    Text(text.toString(), Modifier.fillMaxWidth())
                        Text("Правильно", Modifier.fillMaxWidth())
                        Button(
                            onClick = {
//                            currentLessonId++
//                            currentExercise.value = lesson.exercises[currentLessonId]
                                exerciseQueue.removeAt(0)
                                if (exerciseQueue.isEmpty()) {
                                    onLessonCompleted()
                                } else {
                                    canAnswer.value = false
                                    answered.value = false
                                    rightChosen.value = false
//                                    currentExercise.value = exerciseQueue.last()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .fillMaxWidth()
//                            .align(Alignment.BottomCenter)
                        ) {
                            Text("Дальше")
                        }
                    } else {
//                    val text =
//                        StringBuilder("Неправильно. Правильный ответ${if (rightAnswers.size > 1) "ы: " else ": "}")
//                    rightAnswers.forEachIndexed { index, answerEntity ->
//                        text.append(answerEntity.text)
//                        if (index != rightAnswers.size-1) {
//                            text.append(", ")
//                        }
//                    }
//                    Text(text.toString(), Modifier.fillMaxWidth())
                        Text("Неправильно", Modifier.fillMaxWidth())
                        Button(
                            onClick = {
//                            currentLessonId++
//                            currentExercise.value = lesson.exercises[currentLessonId]
                                exerciseQueue.add(exerciseQueue[0])
                                exerciseQueue.removeAt(0)
                                canAnswer.value = false
                                answered.value = false
                                rightChosen.value = false
//                            if (exerciseQueue.isEmpty()) {
//                                onLessonCompleted()
//                            } else {
//                                currentExercise.value = exerciseQueue.last()
//                            }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
//                            .align(Alignment.BottomCenter)
                        ) {
                            Text("Дальше")
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            answered.value = true
                        },
                        enabled = canAnswer.value,
                        modifier = Modifier
                            .fillMaxWidth()
//                        .align(Alignment.BottomCenter)
                    ) {
                        Text("Ответить")
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCompletedScreen(onNextButtonClicked: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text("Урок пройден!", Modifier.align(Alignment.Center))
        Button(onClick = onNextButtonClicked, Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            Text("Дальше")
        }
    }
}