package com.catoncat.studyapp.ui.screen.allcourses

import android.text.Layout
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ItemCourse
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AllCoursesScreen(
    viewModel: AllCoursesViewModel = viewModel<AllCoursesViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {
    val state by viewModel.uiState.collectAsState();

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    when (val currentState = state) {
        is AllCoursesState.Error -> AllCoursesErrorState(
            currentState,
            onRefresh = { viewModel.onIntent(AllCoursesIntent.Refresh("")) }
        )

        is AllCoursesState.Loading -> AllCoursesLoadingState(onRefresh = { query ->
            viewModel.onIntent(
                AllCoursesIntent.Refresh(query)
            )
        })

        is AllCoursesState.Content -> AllCoursesContentState(
            currentState,
            onLoadMore = { viewModel.onIntent(AllCoursesIntent.LoadMore) },
            onRefresh = { query -> viewModel.onIntent(AllCoursesIntent.Refresh(query)) },
            onCourseChosen = { courseEntity ->
                viewModel.onIntent(
                    AllCoursesIntent.TakeCourse(
                        courseEntity.id
                    )
                )
            }
        )
    }

}

@Composable
fun AllCoursesErrorState(
    currentState: AllCoursesState.Error,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoursesLoadingState(onRefresh: (query: String) -> Unit) {
//    Box(Modifier.fillMaxSize()) {
//        val searchQuery = remember { mutableStateOf("") }
//
//        Row(
//            Modifier
//                .fillMaxWidth()
//                .align(Alignment.TopCenter)
//        ) {
//
//            OutlinedTextField(
//                value = searchQuery.value,
//                onValueChange = { value -> searchQuery.value = value },
//                placeholder = { Text("Поиск") })
//            Button(onClick = {
//                onRefresh(searchQuery.value)
//            }) { Text("Найти") }
//        }
//        CircularProgressIndicator(Modifier.align(Alignment.Center))
//    }
    val searchQuery = remember { mutableStateOf("") }
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = {
            onRefresh("")
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top=5.dp),
                query = searchQuery.value,
                onQueryChange = { value ->
                    searchQuery.value = value
                },
                onSearch = {
                    onRefresh(searchQuery.value)
                },
                expanded = false,
                onExpandedChange = {
                },
                placeholder = { Text("Поиск") }
            )
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoursesContentState(
    currentState: AllCoursesState.Content,
    onLoadMore: () -> Unit,
    onRefresh: (query: String) -> Unit,
    onCourseChosen: (course: CourseEntity) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

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

//    val isRefreshing by remember { mutableStateOf(false) }

//    val searchExpanded = remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = {
            onRefresh("")
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {


            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top=5.dp),
                query = searchQuery.value,
                onQueryChange = { value ->
                    searchQuery.value = value
                },
                onSearch = {
                    onRefresh(searchQuery.value)
                },
                expanded = false,
                onExpandedChange = {
                },
                placeholder = { Text("Поиск") }
            )
//                    value = searchQuery.value,
//                    onValueChange = { value -> searchQuery.value = value },
//                    placeholder = { Text("Поиск") },
//                    inputField = { },
//                    modifier = Modifier.weight(0.7f).padding(horizontal = 3.dp))
//                Button(onClick = {
//                    onRefresh(searchQuery.value)
//                }, Modifier.weight(0.3f).padding(horizontal = 3.dp)) { Text("Найти") }

            CourseList(courses = currentState.courses, onCourseChosen)
        }
    }


}

@Composable
fun CourseList(
    courses: PersistentList<AllCoursesState.Item>,
    onCourseChosen: (course: CourseEntity) -> Unit
) {
    val lazyColumnListState = rememberLazyListState()
    LazyColumn(
        Modifier
            .fillMaxWidth(),
        state = lazyColumnListState
    ) {
        items(courses) { item ->
            when (item) {
                is AllCoursesState.Item.Course -> ItemCourse(
                    item.entity,
                    onClick = { onCourseChosen(item.entity) })

                AllCoursesState.Item.Error -> ItemError()
                AllCoursesState.Item.Loading -> ItemLoading()
            }
        }
    }
}

//@Composable
//fun ItemCourse(entity: CourseEntity) {
//    ElevatedCard(
//        Modifier
//            .fillMaxWidth()
////            .height(150.dp)
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
            Text("Не загружается")
        }
    }
}