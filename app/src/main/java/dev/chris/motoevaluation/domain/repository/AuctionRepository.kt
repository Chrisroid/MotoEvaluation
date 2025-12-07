package dev.chris.motoevaluation.domain.repository

import dev.chris.motoevaluation.domain.model.AuctionEvent
import kotlinx.coroutines.flow.Flow

interface AuctionRepository {
    fun getAuctionEvents(): Flow<AuctionEvent>
}