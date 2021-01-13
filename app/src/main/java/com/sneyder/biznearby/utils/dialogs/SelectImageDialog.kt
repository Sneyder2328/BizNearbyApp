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

package com.sneyder.biznearby.utils.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sneyder.biznearby.R


class SelectImageDialog: BottomSheetDialogFragment() {

    companion object {

        fun newInstance(): SelectImageDialog {
            return SelectImageDialog()
        }

    }

    private var selectImageListener: SelectImageListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.bottom_sheet_pick_image, container, false)
        view.findViewById<View>(R.id.removeButton).setOnClickListener {
            selectImageListener?.onRemoveImage()
            dismissAllowingStateLoss()
        }
        view.findViewById<View>(R.id.galleryButton).setOnClickListener {
            selectImageListener?.onPickImage()
            dismissAllowingStateLoss()
        }
        view.findViewById<View>(R.id.cameraButton).setOnClickListener {
            selectImageListener?.onTakePicture()
            dismissAllowingStateLoss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SelectImageListener) {
            selectImageListener = context
        } else {
            throw RuntimeException("$context must implement SelectImageListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        selectImageListener = null
    }

    interface SelectImageListener {
        fun onTakePicture()
        fun onPickImage()
        fun onRemoveImage()
//        fun onShowAddUrlImageDialog()
    }

}