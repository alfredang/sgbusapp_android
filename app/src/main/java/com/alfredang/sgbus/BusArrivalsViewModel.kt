package com.alfredang.sgbus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class BusArrivalsUiState(
    val query: String = "",
    val selectedStop: BusStop = BusStop.popular.first(),
    val services: List<BusService> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdated: LocalTime? = null,
) {
    val filteredStops: List<BusStop>
        get() {
            val trimmed = query.trim()
            return if (trimmed.isEmpty()) BusStop.popular else BusStop.popular.filter { it.matches(trimmed) }
        }

    val lastUpdatedText: String?
        get() = lastUpdated?.format(DateTimeFormatter.ofPattern("h:mm a"))
}

class BusArrivalsViewModel(
    private val client: LTADataMallClient = LTADataMallClient(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(BusArrivalsUiState())
    val uiState: StateFlow<BusArrivalsUiState> = _uiState.asStateFlow()

    init {
        loadArrivals()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun selectStop(stop: BusStop) {
        _uiState.update {
            it.copy(
                query = "",
                selectedStop = stop,
                services = emptyList(),
                errorMessage = null,
            )
        }
        loadArrivals()
    }

    fun loadArrivals() {
        val stopCode = _uiState.value.selectedStop.id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { client.fetchArrivals(stopCode) }
                .onSuccess { services ->
                    _uiState.update {
                        it.copy(
                            services = services.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { service -> service.serviceNo }),
                            isLoading = false,
                            lastUpdated = LocalTime.now(),
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (BuildConfig.LTA_ACCOUNT_KEY.isBlank()) {
                                "Add your LTA DataMall account key in local.properties."
                            } else {
                                "Arrival data is not available right now."
                            },
                        )
                    }
                }
        }
    }
}
