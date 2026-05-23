package com.catoncat.studyapp.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.theme.Typography

@Composable
fun ItemCourse(
    entity: CourseEntity,
    showCreator: Boolean = true,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
//            .height(150.dp)
            .padding(10.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(5.dp)) {
            Text(
                text = entity.name,
                style = Typography.headlineMedium,
            )
            if (showCreator) Text(text = entity.creator.username, style = Typography.bodyLarge)
            Text(text = entity.description)
        }
    }
}