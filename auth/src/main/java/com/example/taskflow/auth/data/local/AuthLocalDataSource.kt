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

package com.example.taskflow.auth.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskflow.auth.domain.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal val Context.authDataStore by preferencesDataStore(name = "auth",)

class AuthLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        val ACCESS_TOKEN =
            stringPreferencesKey("access_token")

        val TOKEN_TYPE =
            stringPreferencesKey("token_type")

        val EXPIRES_AT =
            longPreferencesKey("expires_at")
    }

    suspend fun saveSession(
        session: AuthSession,
    ) {
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] =
                session.accessToken

            preferences[TOKEN_TYPE] =
                session.tokenType

            preferences[EXPIRES_AT] =
                session.expiresAtEpochSeconds
        }
    }

    val session: Flow<AuthSession?> =
        context.authDataStore.data
            .map { preferences ->
                val accessToken =
                    preferences[ACCESS_TOKEN]

                val tokenType =
                    preferences[TOKEN_TYPE]

                val expiresAt =
                    preferences[EXPIRES_AT]

                if (
                    accessToken == null ||
                    tokenType == null ||
                    expiresAt == null
                ) {
                    null
                } else {
                    AuthSession(
                        accessToken = accessToken,
                        tokenType = tokenType,
                        expiresAtEpochSeconds = expiresAt,
                    )
                }
            }

    suspend fun clearSession() {
        context.authDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(TOKEN_TYPE)
            preferences.remove(EXPIRES_AT)
        }
    }
}