package com.sneyder.biznearby.ui.explore

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ExploreProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFragment(): ExploreFragment

}