package net.afpro.gltutorial

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glPanel.setEGLContextClientVersion(2)
        glPanel.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glPanel.setRenderer(this)
        glPanel.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onDrawFrame(ignored: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun onSurfaceChanged(ignored: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(ignored: GL10?, config: EGLConfig?) {
        glClearColor(0.3f, 0.6f, 0.9f, 1f)
        glClearDepthf(1f)
    }
}
