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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap

class CircleImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return

        if (width == 0 || height == 0) {
            return
        }
        val b = drawable.toBitmap()
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val roundBitmap = getCroppedBitmap(bitmap, width)
        canvas.drawBitmap(roundBitmap, 0f, 0f, null)

    }

    companion object {

        fun getCroppedBitmap(bmp: Bitmap, radius: Int): Bitmap {
            val bitmap = if (bmp.width != radius || bmp.height != radius) {
                val smallest = Math.min(bmp.width, bmp.height).toFloat()
                val factor = smallest / radius
                Bitmap.createScaledBitmap(bmp, (bmp.width / factor).toInt(), (bmp.height / factor).toInt(), false)
            } else {
                bmp
            }

            val output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val paint = Paint()
            val rect = Rect(0, 0, radius, radius)

            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            paint.isDither = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.parseColor("#BAB399")
            canvas.drawCircle(radius / 2 + 0.7f,
                    radius / 2 + 0.7f, radius / 2 + 0.1f, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)

            return output
        }
    }

}
