package dev.chris.motoevaluation.presentation.auction

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chris.motoevaluation.presentation.auction.components.BidItem
import dev.chris.motoevaluation.presentation.auction.components.LifecycleAwareTimer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AuctionScreen(
    viewModel: AuctionViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show winner message when auction ends
    LaunchedEffect(state.isAuctionActive) {
        if (!state.isAuctionActive && state.lowestBid != null) {
            snackbarHostState.showSnackbar(
                "Auction Ended! Winner: ${state.lowestBid?.driverId} at Br ${state.lowestBid?.amount}"
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Moto Live Auction", fontWeight = FontWeight.Bold)
                        Text(
                            text = state.connectionStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = if(state.connectionStatus == "Connected") Color.Green else Color.Gray
                        )
                    }
                },
                actions = {
                    if (state.isAuctionActive) {
                        LifecycleAwareTimer(
                            durationMs = 15000, // 15 Seconds Auction
                            onFinished = { viewModel.onTimerFinished() },
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    } else {
                        Text("CLOSED", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // --- SECTION 1: NEGOTIATION LOG ---
            if (state.negotiationHistory.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Negotiation Phase", style = MaterialTheme.typography.labelLarge, color = Color.Blue)
                        state.negotiationHistory.takeLast(3).forEach { negotiation ->
                            Text(
                                "${negotiation.driverId}: ${negotiation.message}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // --- SECTION 2: LIVE BIDS ---
            Text(
                "Live Bids (${state.bids.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // We use the driverId as the key. This enables the smooth reordering animation.
                items(
                    items = state.bids,
                    key = { it.driverId }
                ) { bid ->
                    val isLowest = bid == state.lowestBid

                    BidItem(
                        bid = bid,
                        isLowest = isLowest,
                        modifier = Modifier.animateItem()// The Magic Animation Line
                    )
                }
            }
        }
    }
}