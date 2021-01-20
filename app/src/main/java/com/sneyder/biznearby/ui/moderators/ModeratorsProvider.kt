package com.sneyder.biznearby.ui.moderators

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ModeratorsProvider {

    @ContributesAndroidInjector
    abstract fun provideModeratorsFragment(): ModeratorsFragment

}