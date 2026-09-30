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

package com.example.taskflow.auth.data.remote

import com.example.taskflow.auth.domain.AuthError
import com.example.taskflow.auth.domain.AuthException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val authApi: AuthApi,
    private val json: Json,
    ) {

    suspend fun register(
        username: String,
        password: String,
    ): UserResponse {
        try {
            return authApi.register(
                RegisterRequest(
                    username = username,
                    password = password,
                )
            )
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun login(
        username: String,
        password: String,
    ): LoginResponse {
        try {
            return authApi.login(
                LoginRequest(
                    username = username,
                    password = password,
                )
            )
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    private fun mapException(
        exception: Exception,
    ): AuthException {
        return when (exception) {
            is SocketTimeoutException -> {
                AuthException(
                    error = AuthError.Timeout,
                    cause = exception,
                )
            }

            is HttpException -> {
                mapHttpException(exception)
            }

            is IOException -> {
                AuthException(
                    error = AuthError.Network,
                    cause = exception,
                )
            }

            else -> {
                AuthException(
                    error = AuthError.Unknown(
                        message = exception.message,
                    ),
                    cause = exception,
                )
            }
        }
    }

    private fun mapHttpException(
        exception: HttpException,
    ): AuthException {
        val apiError = try {
            exception.response()
                ?.errorBody()
                ?.string()
                ?.let { body ->
                    json.decodeFromString<ApiErrorResponse>(body)
                }
        } catch (_: Exception) {
            null
        }

        val error = when (apiError?.code) {
            "INVALID_CREDENTIALS" -> {
                AuthError.InvalidCredentials
            }

            "USERNAME_ALREADY_EXISTS" -> {
                AuthError.UsernameAlreadyExists
            }

            "INVALID_REGISTRATION" -> {
                AuthError.InvalidRegistration(
                    message = apiError.message,
                )
            }

            else -> {
                AuthError.Server(
                    statusCode = exception.code(),
                    message = apiError?.message,
                )
            }
        }

        return AuthException(
            error = error,
            cause = exception,
        )
    }
}