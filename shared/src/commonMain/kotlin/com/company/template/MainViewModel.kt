package com.company.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.template.core.domain.repository.TaskRepository
import com.company.template.core.domain.util.onFailure
import com.company.template.core.domain.util.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state
        .onStart {
            taskRepository.createTask("EXAMPLE")
            delay(5000.milliseconds)
            taskRepository.getTasks()
                .onSuccess {  }
                .onFailure {  }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainState()
        )
}