package com.catoncat.studyapp.ui.screen.mycourses

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.catoncat.studyapp.domain.allcourses.entities.CourseEntity

@Composable
fun MyCoursesScreen() {
    LazyColumn {
        for (i in 0..10) {
            item(content = { ItemCourse(CourseEntity("Test", "Test description")) })
        }
    }
    FloatingActionButton(onClick = {}, content = { Text(text = "+") })
}

@Composable
fun ItemCourse(entity: CourseEntity) {
    ElevatedCard(Modifier.padding(10.dp)) {
        Row {
            Text(entity.name)
            Text(entity.description)
        }
    }
}