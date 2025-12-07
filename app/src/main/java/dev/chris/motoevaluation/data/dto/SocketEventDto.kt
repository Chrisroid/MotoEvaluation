package dev.chris.motoevaluation.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SocketEventDto(
    val type: String, // "NEGOTIATION" or "BID"
    val driverId: String,
    val amount: Double,
    val timestamp: Long,
    val message: String? = null
)