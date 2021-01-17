package com.sneyder.biznearby.ui.add_business

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AddBusinessProvider {

    @ContributesAndroidInjector
    abstract fun provideAddBusinessFragment(): AddBusinessFragment

}