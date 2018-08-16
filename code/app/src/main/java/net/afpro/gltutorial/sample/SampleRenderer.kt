package net.afpro.gltutorial.sample

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class SampleRenderer : GLSurfaceView.Renderer {
    private var part: SamplePart? = null
    var currentPart: SamplePart? = null

    val all = listOf(
            Simple())

    override fun onDrawFrame(ignoredGL10: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        val current = currentPart
        if (part !== current) {
            part?.clear()
            current?.setup()
            part = current
        }
        current?.draw()
    }

    override fun onSurfaceChanged(ignoredGL10: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(ignoredGL10: GL10, eglConfig: EGLConfig) {
        GLES20.glClearColor(0.3f, 0.6f, 0.9f, 1.0f)
        GLES20.glClearDepthf(1f)
        GLES20.glDepthFunc(GLES20.GL_LESS)
    }
}