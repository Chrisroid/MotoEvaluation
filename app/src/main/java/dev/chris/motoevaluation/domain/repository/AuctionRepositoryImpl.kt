package dev.chris.motoevaluation.domain.repository

import dev.chris.motoevaluation.data.mapper.toDomain
import dev.chris.motoevaluation.data.remote.KtorRealTimeService
import dev.chris.motoevaluation.domain.model.AuctionEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuctionRepositoryImpl(
    private val service: KtorRealTimeService
) : AuctionRepository {

    override fun getAuctionEvents(): Flow<AuctionEvent> {
        return service.observeEvents().map { dto ->
            dto.toDomain()
        }
    }
}