package com.catoncat.studyapp.ui.screen.takencourses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.screen.mycourses.MyCoursesIntent
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ItemCourse

@Composable
fun TakenCoursesScreen(
    viewModel: TakenCoursesViewModel = viewModel<TakenCoursesViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {

    val state by viewModel.uiState.collectAsState();

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    when (val currentState = state) {
        is TakenCoursesState.Error -> TakenCoursesErrorState(
            currentState,
            onRefresh = { viewModel.onIntent(TakenCoursesIntent.Refresh) }
        )

        is TakenCoursesState.Loading -> TakenCoursesLoadingState()
        is TakenCoursesState.Content -> TakenCoursesContentState(
            currentState,
            onLoadMore = { viewModel.onIntent(TakenCoursesIntent.LoadMore) },
            onRefresh = { viewModel.onIntent(TakenCoursesIntent.Refresh) },
            onCourseChosen = { course ->
                viewModel.onIntent(TakenCoursesIntent.ChooseCourse(course))
                backStack.add(AppRoute.CourseViewing)
            }
        )
    }

}

@Composable
fun TakenCoursesErrorState(
    currentState: TakenCoursesState.Error,
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
fun TakenCoursesLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            CircularProgressIndicator(Modifier)
        }
    }
}


@Composable
fun TakenCoursesContentState(
    currentState: TakenCoursesState.Content,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onCourseChosen: (course: CourseEntity) -> Unit
) {
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
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = {
            onRefresh()
        }
    ) {
        LazyColumn(Modifier.fillMaxSize(), state = lazyColumnListState) {
            items(currentState.courses) { item ->
                when (item) {
                    is TakenCoursesState.Item.Course -> ItemCourse(item.entity, onClick = {
                        onCourseChosen(item.entity)
                    })

                    TakenCoursesState.Item.Error -> ItemError()
                    TakenCoursesState.Item.Loading -> ItemLoading()
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