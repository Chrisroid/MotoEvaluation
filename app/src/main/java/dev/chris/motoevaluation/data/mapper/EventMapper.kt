package dev.chris.motoevaluation.data.mapper

import dev.chris.motoevaluation.data.dto.SocketEventDto
import dev.chris.motoevaluation.domain.model.AuctionEvent

fun SocketEventDto.toDomain(): AuctionEvent {
    return when (this.type) {
        "NEGOTIATION" -> AuctionEvent.Negotiation(
            driverId = this.driverId,
            amount = this.amount,
            message = this.message ?: "Offer received",
            timestamp = this.timestamp
        )
        "BID" -> AuctionEvent.Bid(
            driverId = this.driverId,
            amount = this.amount,
            timestamp = this.timestamp
        )
        else -> AuctionEvent.Unknown
    }
}