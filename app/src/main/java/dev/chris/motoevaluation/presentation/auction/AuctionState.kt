package dev.chris.motoevaluation.presentation.auction

import dev.chris.motoevaluation.domain.model.AuctionEvent

data class AuctionState(
    val connectionStatus: String = "Connecting...",
    val negotiationHistory: List<AuctionEvent.Negotiation> = emptyList(),
    val bids: List<AuctionEvent.Bid> = emptyList(), // We will keep this sorted
    val lowestBid: AuctionEvent.Bid? = null,
    val isAuctionActive: Boolean = true,
    val timerProgress: Float = 1f // 1.0 = Full, 0.0 = Empty
)