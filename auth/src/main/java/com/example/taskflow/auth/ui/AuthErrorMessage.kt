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

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.taskflow.auth.R
import com.example.taskflow.auth.domain.AuthError

sealed interface AuthErrorMessage {

    data class Resource(
        @StringRes val resourceId: Int,
        val args: List<Any> = emptyList(),
    ) : AuthErrorMessage

    data class Dynamic(
        val value: String,
    ) : AuthErrorMessage
}

fun AuthError.toMessage(): AuthErrorMessage {
    return when (this) {
        AuthError.InvalidCredentials -> {
            AuthErrorMessage.Resource(
                R.string.auth_error_invalid_credentials
            )
        }

        AuthError.UsernameAlreadyExists -> {
            AuthErrorMessage.Resource(
                R.string.auth_error_username_already_exists
            )
        }

        is AuthError.InvalidRegistration -> {
            AuthErrorMessage.Dynamic(message)
        }

        AuthError.Network -> {
            AuthErrorMessage.Resource(
                R.string.auth_error_network
            )
        }

        AuthError.Timeout -> {
            AuthErrorMessage.Resource(
                R.string.auth_error_timeout
            )
        }

        is AuthError.Server -> {
            if (message != null) {
                AuthErrorMessage.Dynamic(message)
            } else {
                AuthErrorMessage.Resource(
                    resourceId = R.string.auth_error_server,
                    args = listOf(statusCode),
                )
            }
        }

        is AuthError.Unknown -> {
            if (message != null) {
                AuthErrorMessage.Dynamic(message)
            } else {
                AuthErrorMessage.Resource(
                    R.string.auth_error_unknown
                )
            }
        }
    }
}

@Composable
fun AuthErrorMessage.asString(): String {
    return when (this) {
        is AuthErrorMessage.Resource -> {
            stringResource(
                id = resourceId,
                *args.toTypedArray(),
            )
        }

        is AuthErrorMessage.Dynamic -> {
            value
        }
    }
}
