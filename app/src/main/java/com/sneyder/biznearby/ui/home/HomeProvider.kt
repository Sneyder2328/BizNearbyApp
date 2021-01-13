package com.sneyder.biznearby.ui.home

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFragment(): HomeFragment

}