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

import timber.log.Timber

typealias TypeMessage = Any?

/** Invokes an action if any trees are planted */
inline fun ifPlanted(action: () -> Unit) {
    if (Timber.treeCount() != 0) {
        action()
    }
}

/** Delegates the provided message to [Timber.e] if any trees are planted. */
fun logError(message: TypeMessage) = ifPlanted { Timber.e(message.toString()) }

/** Delegates the provided message to [Timber.e] if any trees are planted. */
fun logError(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.e(throwable, message.toString())
}

/** Delegates the provided message to [Timber.w] if any trees are planted. */
fun warn(message: TypeMessage) = ifPlanted { Timber.w(message.toString()) }

        /** Delegates the provided message to [Timber.w] if any trees are planted. */
fun warn(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.w(throwable, message.toString())
}

/** Delegates the provided message to [Timber.i] if any trees are planted. */
fun info(message: TypeMessage) = ifPlanted { Timber.i(message.toString()) }

/** Delegates the provided message to [Timber.i] if any trees are planted. */
fun info(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.i(throwable, message.toString())
}

/** Delegates the provided message to [Timber.d] if any trees are planted. */
fun debug(message: TypeMessage) = ifPlanted { Timber.d(message.toString()) }

/** Delegates the provided message to [Timber.d] if any trees are planted. */
fun debug(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.d(throwable, message.toString())
}

/** Delegates the provided message to [Timber.v] if any trees are planted. */
fun verbose(message: TypeMessage) = ifPlanted { Timber.v(message.toString()) }

/** Delegates the provided message to [Timber.v] if any trees are planted. */
fun verbose(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.v(throwable, message.toString())
}

/** Delegates the provided message to [Timber.wtf] if any trees are planted. */
fun wtf(message: String) = ifPlanted { Timber.wtf(message) }

/** Delegates the provided message to [Timber.wtf] if any trees are planted. */
fun wtf(throwable: Throwable, message: TypeMessage) = ifPlanted {
    Timber.wtf(throwable, message.toString())
}

/** Delegates the provided message to [Timber.log] if any trees are planted. */
fun log(priority: Int, t: Throwable, message: TypeMessage) = ifPlanted {
    Timber.log(priority, t, message.toString())
}

/** Delegates the provided message to [Timber.log] if any trees are planted. */
fun log(priority: Int, message: TypeMessage) = ifPlanted {
    Timber.log(priority, message.toString())
}