package com.example.broadcastjournal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BroadcastViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BroadcastRepository(application.applicationContext)

    private val _events = MutableStateFlow<List<BroadcastEvent>>(emptyList())
    val events: StateFlow<List<BroadcastEvent>> = _events.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeEvents().collect { event ->
                _events.update { current -> listOf(event) + current }
            }
        }
    }

    fun deleteEvent(event: BroadcastEvent) {
        _events.update { current -> current - event }
    }

    fun clearAll() {
        _events.value = emptyList()
    }
}