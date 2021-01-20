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

package com.sneyder.biznearby.di.module;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sneyder.biznearby.ViewModelProviderFactory;
import com.sneyder.biznearby.di.ViewModelKey;
import com.sneyder.biznearby.ui.add_business.AddBusinessViewModel;
import com.sneyder.biznearby.ui.add_business_image.AddBizImageViewModel;
import com.sneyder.biznearby.ui.explore.ExploreViewModel;
import com.sneyder.biznearby.ui.home.HomeViewModel;
import com.sneyder.biznearby.ui.login.LogInViewModel;
import com.sneyder.biznearby.ui.moderators.ModeratorsViewModel;
import com.sneyder.biznearby.ui.pick_city.PickCityViewModel;
import com.sneyder.biznearby.ui.signup.SignUpViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@SuppressWarnings(value = "unused")
@Module
public abstract class ViewModelModule {

//  @Binds
//  @IntoMap
//  @ViewModelKey(CategoriesViewModel.class)
//  abstract ViewModel bindCategoriesViewModel(CategoriesViewModel viewModel);
//
//  @Binds
//  @IntoMap
//  @ViewModelKey(MainViewModel.class)
//  abstract ViewModel bindMainViewModel(MainViewModel MainViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(ExploreViewModel.class)
  abstract ViewModel bindExploreViewModel(ExploreViewModel ExploreViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(HomeViewModel.class)
  abstract ViewModel bindHomeViewModel(HomeViewModel HomeViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SignUpViewModel.class)
  abstract ViewModel bindSignUpViewModel(SignUpViewModel SignUpViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(LogInViewModel.class)
  abstract ViewModel bindLogInViewModel(LogInViewModel LogInViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AddBusinessViewModel.class)
  abstract ViewModel bindAddBusinessViewModel(AddBusinessViewModel AddBusinessViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(PickCityViewModel.class)
  abstract ViewModel bindPickCityViewModel(PickCityViewModel PickCityViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AddBizImageViewModel.class)
  abstract ViewModel bindAddBizImageViewModel(AddBizImageViewModel AddBizImageViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(ModeratorsViewModel.class)
  abstract ViewModel bindModeratorsViewModel(ModeratorsViewModel ModeratorsViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory factory);

}
