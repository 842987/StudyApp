package com.catoncat.studyapp.ui.screen.allcourses

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography
import com.catoncat.studyapp.ui.util.ItemCourse

@Composable
fun AllCoursesScreen(
    viewModel: AllCoursesViewModel = viewModel<AllCoursesViewModel>(),
    backStack: SnapshotStateList<AppRoute>
) {

    val state by viewModel.uiState.collectAsState();

    when (val currentState = state) {
        is AllCoursesState.Error -> AllCoursesErrorState(
            currentState,
            onRefresh = { viewModel.onIntent(AllCoursesIntent.Refresh) }
        )

        is AllCoursesState.Loading -> AllCoursesLoadingState()
        is AllCoursesState.Content -> AllCoursesContentState(
            currentState,
            onLoadMore = { viewModel.onIntent(AllCoursesIntent.LoadMore) },
            onRefresh = { viewModel.onIntent(AllCoursesIntent.Refresh) }
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

@Composable
fun AllCoursesLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            CircularProgressIndicator(Modifier)
        }
    }
}


@Composable
fun AllCoursesContentState(
    currentState: AllCoursesState.Content,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        for (i in 0..10) {


            items(currentState.courses) { item ->
                when (item) {
                    is AllCoursesState.Item.Course -> ItemCourse(item.entity, onClick = {})
                    AllCoursesState.Item.Error -> ItemError()
                    AllCoursesState.Item.Loading -> ItemLoading()
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
            Text("Couldn't load")
        }
    }
}