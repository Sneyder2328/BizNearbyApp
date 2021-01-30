package com.sneyder.biznearby.ui.add_business.basic_info

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class BasicInfoProvider {

    @ContributesAndroidInjector
    abstract fun provideBasicInfo(): BasicInfoFragment

}