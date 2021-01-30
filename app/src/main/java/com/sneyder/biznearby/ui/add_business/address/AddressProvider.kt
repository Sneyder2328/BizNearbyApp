package com.sneyder.biznearby.ui.add_business.address

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AddressProvider {

    @ContributesAndroidInjector
    abstract fun provideAddressProvider(): AddressFragment

}