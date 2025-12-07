package dev.chris.motoevaluation.domain.model

sealed interface AuctionEvent {
    data class Negotiation(
        val driverId: String,
        val amount: Double,
        val message: String,
        val timestamp: Long
    ) : AuctionEvent

    data class Bid(
        val driverId: String,
        val amount: Double,
        val timestamp: Long
    ) : AuctionEvent

    data object Unknown : AuctionEvent
}