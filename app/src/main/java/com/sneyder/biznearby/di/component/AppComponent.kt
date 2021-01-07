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
package com.sneyder.biznearby.di.component

import android.app.Application
import com.sneyder.biznearby.BaseApp
import com.sneyder.biznearby.di.builder.ActivityBuilder
import com.sneyder.biznearby.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.DaggerApplication
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (ActivityBuilder::class)])
interface AppComponent: AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    fun inject(app: BaseApp)

}