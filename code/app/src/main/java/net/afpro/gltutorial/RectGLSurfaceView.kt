package net.afpro.gltutorial

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import kotlin.math.min

class RectGLSurfaceView : GLSurfaceView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val s = min(w, h)
        setMeasuredDimension(s, s)
    }
}
