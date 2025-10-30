package com.example.mapsplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsplanner.ui.MapsPlannerRoute
import com.example.mapsplanner.ui.PlannerViewModel
import com.example.mapsplanner.ui.theme.MapsPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsPlannerTheme {
                MapsPlannerApp()
            }
        }
    }
}

@Composable
private fun MapsPlannerApp() {
    val viewModel: PlannerViewModel = viewModel()
    MapsPlannerRoute(
        modifier = Modifier,
        viewModel = viewModel
    )
}
