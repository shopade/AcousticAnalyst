package com.example.acousticanalyst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acousticanalyst.ui.screens.MainScreen
import com.example.acousticanalyst.ui.theme.AcousticAnalystTheme
import com.example.acousticanalyst.ui.viewmodel.AcousticViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcousticAnalystTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val acousticViewModel: AcousticViewModel = viewModel()
                    MainScreen(viewModel = acousticViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    AcousticAnalystTheme {
        val previewViewModel: AcousticViewModel = viewModel()
        MainScreen(viewModel = previewViewModel)
    }
}
