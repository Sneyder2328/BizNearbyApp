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

package com.sneyder.biznearby.di.builder

import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.add_business.address.AddressProvider
import com.sneyder.biznearby.ui.add_business.basic_info.BasicInfoProvider
import com.sneyder.biznearby.ui.add_business.images.ImagesProvider
import com.sneyder.biznearby.ui.add_business.schedule.ScheduleProvider
import com.sneyder.biznearby.ui.add_business_image.AddBizImageActivity
import com.sneyder.biznearby.ui.add_report.AddReportActivity
import com.sneyder.biznearby.ui.business_details.BusinessDetailsActivity
import com.sneyder.biznearby.ui.home.HomeActivity
import com.sneyder.biznearby.ui.explore.ExploreProvider
import com.sneyder.biznearby.ui.login.LogInActivity
import com.sneyder.biznearby.ui.moderators.ModeratorsActivity
import com.sneyder.biznearby.ui.my_businesses.MyBusinessesActivity
import com.sneyder.biznearby.ui.pick_city.PickCityActivity
import com.sneyder.biznearby.ui.pick_location.PickLocationActivity
import com.sneyder.biznearby.ui.reports.ReportsActivity
import com.sneyder.biznearby.ui.signup.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [ExploreProvider::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector()
    abstract fun bindReportsActivity(): ReportsActivity

    @ContributesAndroidInjector()
    abstract fun bindSignUpActivity(): SignUpActivity

    @ContributesAndroidInjector()
    abstract fun bindLogInActivity(): LogInActivity

    @ContributesAndroidInjector()
    abstract fun bindPickCityActivity(): PickCityActivity

    @ContributesAndroidInjector()
    abstract fun bindPickLocationActivity(): PickLocationActivity

    @ContributesAndroidInjector()
    abstract fun bindAddBizImageActivity(): AddBizImageActivity

    @ContributesAndroidInjector()
    abstract fun bindBusinessDetailsActivity(): BusinessDetailsActivity

    @ContributesAndroidInjector()
    abstract fun bindMyBusinessesActivity(): MyBusinessesActivity

    @ContributesAndroidInjector()
    abstract fun bindModeratorsActivity(): ModeratorsActivity

    @ContributesAndroidInjector(modules=[BasicInfoProvider::class, AddressProvider::class, ScheduleProvider::class, ImagesProvider::class])
    abstract fun bindAddBusinessActivity(): AddBusinessActivity

    @ContributesAndroidInjector()
    abstract fun bindAddReportActivity(): AddReportActivity

}