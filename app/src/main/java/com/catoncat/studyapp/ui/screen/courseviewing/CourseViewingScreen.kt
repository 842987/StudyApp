package com.catoncat.studyapp.ui.screen.courseviewing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.util.ExerciseType
import kotlin.math.roundToInt

@Composable
fun CourseViewingScreen(
    viewModel: CourseViewingViewModel = viewModel<CourseViewingViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState()

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
        LessonView(lesson = chosenLesson.value!!)
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
                .requiredSize(1500.dp, 1500.dp)
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
                    drawRect(Color.DarkGray)
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
                    val x = remember { mutableFloatStateOf(lesson.x) }
                    val y = remember { mutableFloatStateOf(lesson.y) }
                    Button(
                        onClick = {
                            onChooseLesson(lesson)
                        },
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
                    ) { Text(lesson.name) }
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
fun LessonView(lesson: LessonEntity) {
    if (lesson.exercises.isEmpty()) {
        Box(Modifier.fillMaxSize()) {
            Text(text = "Урок не содержит заданий", Modifier.align(Alignment.Center))
        }
    } else {
        var currentLessonId = 0
        val currentExercise = remember { mutableStateOf(lesson.exercises[currentLessonId]) }
        var rightChosen: Boolean? = null;
        val canAnswer = remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()) {

            Text(text = currentExercise.value.text, modifier = Modifier.fillMaxWidth())

            when (currentExercise.value.typeName) {
                ExerciseType.Input.typeName -> {
                    val rightAnswers =
                        currentExercise.value.answers.filter { answerEntity -> answerEntity.correct }
                    val inputText = remember { mutableStateOf("") }
                    TextField(value = inputText.value, onValueChange = { value ->
                        if (!value.isEmpty()) {
                            inputText.value = value
                            for (answerEntity in rightAnswers) {
                                if (answerEntity.text == value) {
                                    rightChosen = true
                                    break
                                }
                            }
                            rightChosen = false
                            canAnswer.value = true
                        }
                    })
                }

                ExerciseType.Choose.typeName -> {
                    val checkedAnswerId = remember { mutableLongStateOf(-1) }
                    Column(Modifier.selectableGroup()) {
                        currentExercise.value.answers.forEach { answer ->
                            IconToggleButton(
                                checked = (answer.id!!) == checkedAnswerId.longValue,
                                onCheckedChange = { checked ->
                                    rightChosen = answer.correct
                                    if (!checked) checkedAnswerId.longValue = answer.id
                                    canAnswer.value = true
                                }) {
                                Text(answer.text)
                            }
                        }
                    }
                }
            }

            Button(onClick = {
                if (rightChosen!!) {
                    currentLessonId++
                    currentExercise.value = lesson.exercises[currentLessonId]
                }
            }, enabled = canAnswer.value, modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                Text("Ответить")
            }
        }
    }
}