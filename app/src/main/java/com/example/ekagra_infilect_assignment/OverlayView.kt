package com.example.ekagra_infilect_assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.scale

class OverlayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // tick image
    val tickImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.green_tick).scale(200, 200)

    var points: List<TickPoint> = emptyList()

    fun updateTicks(list: List<TickPoint>) {
        points = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (p in points) {

            val hw = tickImg.width / 2f
            val hh = tickImg.height / 2f

            canvas.drawBitmap(tickImg, p.x - hw, p.y - hh, null)
        }
    }
}

data class TickPoint(val x: Float, val y: Float)