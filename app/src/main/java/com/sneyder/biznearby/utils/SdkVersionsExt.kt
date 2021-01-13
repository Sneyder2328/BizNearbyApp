/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sneyder.biznearby.utils

// Check API Version
fun isJellyBeanOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN

fun isKitkatOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT

fun isLollipopOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP

fun isMarshmallowOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M

fun isNougatOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N

fun isOreoOrLater(): Boolean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O