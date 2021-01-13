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
package com.sneyder.biznearby.utils.base

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.sneyder.biznearby.utils.isMarshmallowOrLater
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


abstract class DaggerActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var listFunctionsSuccess: MutableMap<Int, () -> Unit> = HashMap()
    private var listFunctionsError: MutableMap<Int, () -> Unit> = HashMap()

    fun ifHasPermission(
        permissionsToAskFor: Array<String>,
        requestCode: Int,
        func: () -> Unit,
        funcError: () -> Unit = {}
    ) {
        val pendingPermissions = permissionsToAskFor.filter {
            isMarshmallowOrLater() && ContextCompat.checkSelfPermission(
                this,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, pendingPermissions, requestCode)
            listFunctionsSuccess[requestCode] = func
            listFunctionsError[requestCode] = funcError
            return
        }
        func.invoke()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermissionResults(grantResults)) {
            listFunctionsSuccess[requestCode]?.invoke()
        } else {
            listFunctionsError[requestCode]?.invoke()
        }
        listFunctionsSuccess.remove(requestCode)
        listFunctionsError.remove(requestCode)
    }

    /**
     * Checks if the user allowed all permissions that were requested.
     *
     * @param results Array of request results
     * @return True if then user allowed all permissions, false otherwise
     */
    private fun checkPermissionResults(results: IntArray): Boolean {
        for (result in results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        listFunctionsSuccess.clear()
        listFunctionsError.clear()
    }
}