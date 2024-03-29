/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.biznearby.di.module

import com.sneyder.biznearby.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun provideUserRepository(appUserRepository: AppUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun provideLocationsRepositoryRepository(appLocationsRepositoryRepository: AppLocationsRepository): LocationsRepository

    @Binds
    @Singleton
    abstract fun provideBusinessRepository(appBusinessRepository: AppBusinessRepository): BusinessRepository

}