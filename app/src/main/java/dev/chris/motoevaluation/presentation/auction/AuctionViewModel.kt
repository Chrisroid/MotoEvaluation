package dev.chris.motoevaluation.presentation.auction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.chris.motoevaluation.domain.model.AuctionEvent
import dev.chris.motoevaluation.domain.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuctionViewModel(
    private val repository: AuctionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuctionState())
    val state: StateFlow<AuctionState> = _state.asStateFlow()

    init {
        startObserving()
    }

    private fun startObserving() {
        viewModelScope.launch {
            _state.update { it.copy(connectionStatus = "Connected") }

            repository.getAuctionEvents().collect { event ->
                when (event) {
                    is AuctionEvent.Negotiation -> {
                        _state.update { currentState ->
                            currentState.copy(
                                negotiationHistory = currentState.negotiationHistory + event
                            )
                        }
                    }
                    is AuctionEvent.Bid -> {
                        handleNewBid(event)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun handleNewBid(newBid: AuctionEvent.Bid) {
        _state.update { currentState ->
            if (!currentState.isAuctionActive) return@update currentState

            val listWithoutDriver = currentState.bids.filter { it.driverId != newBid.driverId }

            val updatedList = listWithoutDriver + newBid

            val sortedList = updatedList.sortedBy { it.amount }.take(10)

            val winner = sortedList.firstOrNull()

            currentState.copy(
                bids = sortedList,
                lowestBid = winner
            )
        }
    }
    fun onTimerFinished() {
        _state.update { it.copy(isAuctionActive = false) }
    }
}