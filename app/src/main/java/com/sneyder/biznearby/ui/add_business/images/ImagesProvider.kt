package com.sneyder.biznearby.ui.add_business.images

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ImagesProvider {

    @ContributesAndroidInjector
    abstract fun provideImagesProvider(): ImagesFragment

}