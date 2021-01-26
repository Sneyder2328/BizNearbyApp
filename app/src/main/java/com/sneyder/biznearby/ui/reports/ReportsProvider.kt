package com.sneyder.biznearby.ui.reports

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ReportsProvider {

    @ContributesAndroidInjector
    abstract fun provideReportsFragment(): ReportsFragment

}