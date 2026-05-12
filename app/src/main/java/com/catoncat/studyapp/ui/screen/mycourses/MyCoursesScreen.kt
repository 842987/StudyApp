package com.catoncat.studyapp.ui.screen.mycourses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ItemCourse

@Composable
fun MyCoursesScreen(
    viewModel: MyCoursesViewModel = viewModel<MyCoursesViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {

    val state by viewModel.uiState.collectAsState();

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    when (val currentState = state) {
        is MyCoursesState.Error -> MyCoursesErrorState(
            currentState,
            onRefresh = { viewModel.onIntent(MyCoursesIntent.Refresh) }
        )

        is MyCoursesState.Loading -> MyCoursesLoadingState()
        is MyCoursesState.Content -> MyCoursesContentState(
            currentState,
            onLoadMore = { viewModel.onIntent(MyCoursesIntent.LoadMore) },
            onRefresh = { viewModel.onIntent(MyCoursesIntent.Refresh) },
            onCreate = { name, description ->
                viewModel.onIntent(
                    MyCoursesIntent.CreateCourse(
                        name,
                        description
                    )
                )
            },
            onCourseClick = { courseEntity ->
                viewModel.onIntent(MyCoursesIntent.ChooseCourse(courseEntity))
                backStack.add(AppRoute.CourseCreating)
            }
        )
    }

}

@Composable
fun MyCoursesErrorState(
    currentState: MyCoursesState.Error,
    onRefresh: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(currentState.reason)
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}

@Composable
fun MyCoursesLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            CircularProgressIndicator(Modifier)
        }
    }
}


@Composable
fun MyCoursesContentState(
    currentState: MyCoursesState.Content,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onCreate: (name: String, description: String) -> Unit,
    onCourseClick: (CourseEntity) -> Unit
) {
    val showCreateCourseDialog = remember { mutableStateOf(false) }
    val lazyColumnListState = rememberLazyListState()

//    val isNeededLoadMore by remember {
//        derivedStateOf {
//            val lastVisibleItem =
//                lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: Int.MIN_VALUE
//            val totalItems = lazyColumnListState.layoutInfo.totalItemsCount
//            lastVisibleItem >= totalItems - 5
//        }
//    }
//
//    LaunchedEffect(isNeededLoadMore, currentState.isLastPage) {
//        if (isNeededLoadMore && !currentState.isLastPage) onLoadMore.invoke()
//    }

    CreateCourseDialog(showCreateCourseDialog, onCreate = onCreate)
    Box(Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {
                onRefresh()
            }
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                state = lazyColumnListState
            ) {
                items(currentState.courses) { item ->
                    when (item) {
                        is MyCoursesState.Item.Course -> ItemCourse(
                            item.entity, showCreator = false,
                            onClick = { onCourseClick(item.entity) })

                        MyCoursesState.Item.Error -> ItemError()
                        MyCoursesState.Item.Loading -> ItemLoading()
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showCreateCourseDialog.value = true }, modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(5.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun CreateCourseDialog(
    show: MutableState<Boolean>,
    onCreate: (name: String, description: String) -> Unit
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
                    Text("Создание курса")

                    val name = remember { mutableStateOf("") }
                    Row(Modifier.fillMaxWidth()) {
                        Text("Название: ")
                        TextField(
                            value = name.value,
                            onValueChange = { value -> name.value = value })
                    }
                    val description = remember { mutableStateOf("") }
                    Row(Modifier.fillMaxWidth()) {
                        Text("Описание: ")
                        TextField(
                            value = description.value,
                            onValueChange = { value -> description.value = value })
                    }


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
                                onCreate(name.value, description.value)
                            },
                        ) { Text("Создать") }
                    }
                }
            }
        }
    }
}

//@Composable
//fun ItemCourse(entity: CourseEntity) {
//    ElevatedCard(
//        Modifier
//            .fillMaxWidth()
//            .height(150.dp)
//            .padding(10.dp)
//    ) {
//        Column(Modifier.fillMaxSize()) {
//            Text(
//                text = entity.name,
//                style = Typography.headlineMedium,
//                modifier = Modifier.padding(5.dp)
//            )
//            Text(text = entity.creator.username, style = Typography.bodyLarge)
//            Text(text = entity.description)
//        }
//    }
//}

@Composable
fun ItemLoading() {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ItemError() {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Couldn't load")
        }
    }
}