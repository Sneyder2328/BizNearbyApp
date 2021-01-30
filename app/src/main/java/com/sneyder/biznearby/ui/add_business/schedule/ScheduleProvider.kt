package com.sneyder.biznearby.ui.add_business.schedule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ScheduleProvider {

    @ContributesAndroidInjector
    abstract fun provideScheduleProvider(): ScheduleFragment

}