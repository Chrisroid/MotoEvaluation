package dev.chris.motoevaluation.presentation.auction.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chris.motoevaluation.domain.model.AuctionEvent
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun BidItem(
    bid: AuctionEvent.Bid,
    isLowest: Boolean,
    modifier: Modifier = Modifier
) {
    // 1. Flash Animation: Start Yellow, fade to White/Grey
    val backgroundColor = remember { Animatable(Color(0xFFFFEB3B)) }

    LaunchedEffect(Unit) {
        backgroundColor.animateTo(
            targetValue = if (isLowest) Color.White else Color(0xFFF5F5F5),
            animationSpec = tween(durationMillis = 700)
        )
    }

    // 2. Text Color Animation
    val amountColor by animateColorAsState(
        targetValue = if (isLowest) Color(0xFF2E7D32) else Color.Gray,
        label = "color"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor.value),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLowest) 6.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bid.driverId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(bid.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Br ${String.format("%.2f", bid.amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )

                if (!isLowest) {
                    Text(
                        text = "OUTBID",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                } else {
                    Text(
                        text = "WINNING",
                        color = Color(0xFF2E7D32), // Green
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFF2E7D32).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}