package com.catoncat.studyapp.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.res.painterResource

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize()) {
        Image(painter = painterResource(com.catoncat.studyapp.R.drawable.baseline_account_circle_24), contentDescription = "Avatar")
    }
}