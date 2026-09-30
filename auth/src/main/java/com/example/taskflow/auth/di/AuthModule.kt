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

package com.example.taskflow.auth.di

import com.example.taskflow.auth.data.AuthRepositoryImpl
import com.example.taskflow.auth.data.local.SessionManager
import com.example.taskflow.auth.data.local.SessionManagerImpl
import com.example.taskflow.auth.data.remote.AuthApi
import com.example.taskflow.auth.domain.AuthRepository
import com.example.taskflow.core.network.auth.AccessTokenProvider
import com.example.taskflow.core.network.auth.AuthenticationFailureHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionManager(
        implementation: SessionManagerImpl,
    ): SessionManager

    @Binds
    @Singleton
    abstract fun bindAccessTokenProvider(
        implementation: SessionManagerImpl,
    ): AccessTokenProvider

    @Binds
    @Singleton
    abstract fun bindAuthenticationFailureHandler(
        implementation: SessionManagerImpl,
    ): AuthenticationFailureHandler

    companion object {

        @Provides
        @Singleton
        fun provideAuthApi(
            retrofit: Retrofit,
        ): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }
    }
}