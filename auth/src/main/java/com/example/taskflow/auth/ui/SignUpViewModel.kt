/*
 *
 * TaskFlow - An Android project demonstrating modern Clean Architecture,
 * Jetpack Compose, and Unidirectional Data Flow.
 *
 * Copyright (C) 2026 Khoa Ngo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.taskflow.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> =
        _uiState.asStateFlow()

    private val _effects =
        MutableSharedFlow<SignUpUiEffect>()

    val effects: SharedFlow<SignUpUiEffect> =
        _effects.asSharedFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.SignUpClicked -> {
                signUp(
                    username = event.username,
                    password = event.password,
                )
            }
        }
    }

    private fun signUp(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true)
            }

            try {
                val user = registerUseCase(
                    username = username,
                    password = password,
                )

                _effects.emit(
                    SignUpUiEffect.RegistrationSucceeded(
                        username = user.username,
                    )
                )
            } catch (e: Exception) {
                _effects.emit(
                    SignUpUiEffect.RegistrationFailed(
                        message = e.message
                            ?: "Registration failed",
                    )
                )
            } finally {
                _uiState.update {
                    it.copy(isSubmitting = false)
                }
            }
        }
    }
}