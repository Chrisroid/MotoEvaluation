package dev.chris.motoevaluation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chris.motoevaluation.data.remote.KtorRealTimeService
import dev.chris.motoevaluation.domain.repository.AuctionRepositoryImpl
import dev.chris.motoevaluation.presentation.auction.AuctionScreen
import dev.chris.motoevaluation.presentation.auction.AuctionViewModel
import dev.chris.motoevaluation.ui.theme.MotoEvaluationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Manual Dependency Injection
        val service = KtorRealTimeService()
        val repository = AuctionRepositoryImpl(service)

        // 2. ViewModel Factory
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuctionViewModel(repository) as T
            }
        }

        setContent {
            MotoEvaluationTheme {
                // 3. Obtain ViewModel
                val viewModel: AuctionViewModel = viewModel(factory = factory)

                // 4. Show Screen
                AuctionScreen(viewModel = viewModel)
            }
        }
    }

    // Note: In a real app, you'd handle service.close() in a scoped component/onDestroy
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MotoEvaluationTheme {
    }
}