package net.afpro.gltutorial.sample

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import net.afpro.gltutorial.*
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Simple : SamplePart {
    override val name: String
        get() = "Simple"

    var vsId = 0
    var fsId = 0
    var progId = 0

    var mvpUniformId = 0
    var colorAttributeId = 0
    var posAttributeId = 0

    val mvp = FloatArray(16) { 0f }

    val pos: Buffer
    val color: Buffer

    init {
        val v = FloatArray(16) { 0f }
        val p = FloatArray(16) { 0f }

        Matrix.setLookAtM(v, 0,
                0f, 0f, -5f,
                0f, 0f, 0f,
                0f, 1f, 0f)
        Matrix.perspectiveM(p, 0,
                60f, 1f,
                0.1f, 10f)
        Matrix.multiplyMM(mvp, 0, p, 0, v, 0)

        val posAry = floatArrayOf(
                0f, 1f, 0f,
                -1f, -0.5f, 0f,
                1f, -0.5f, 0f)

        val colorAry = floatArrayOf(
                1f, 0f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 0f, 1f, 1f)

        pos = ByteBuffer
                .allocateDirect(posAry.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(posAry)
                .position(0)

        color = ByteBuffer
                .allocateDirect(colorAry.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colorAry)
                .position(0)
    }

    override fun setup() {
        vsId = createAndCompileShader(GLES20.GL_VERTEX_SHADER, App.readAsset("simple_vs.glsl"))
        fsId = createAndCompileShader(GLES20.GL_FRAGMENT_SHADER, App.readAsset("simple_fs.glsl"))
        progId = createAndLinkProgram(vsId, fsId)
        Log.d(TAG, "vs=$vsId fs=$fsId prog=$progId")

        GLErrorGuard.run { g ->
            mvpUniformId = GLES20.glGetUniformLocation(progId, "u_mvp")
            g.assertNoError(mvpUniformId < 0) { "getting mvp" }
            posAttributeId = GLES20.glGetAttribLocation(progId, "a_pos")
            g.assertNoError(posAttributeId < 0) { "getter pos" }
            colorAttributeId = GLES20.glGetAttribLocation(progId, "a_color")
            g.assertNoError(colorAttributeId < 0) { "getter color" }
        }
        Log.d(TAG, "mvp=$mvpUniformId pos=$posAttributeId color=$colorAttributeId")
    }

    override fun draw() {
        GLES20.glUseProgram(progId)

        GLES20.glEnableVertexAttribArray(posAttributeId)
        GLES20.glVertexAttribPointer(posAttributeId, 3, GLES20.GL_FLOAT, false, 0, pos)
        GLES20.glEnableVertexAttribArray(colorAttributeId)
        GLES20.glVertexAttribPointer(colorAttributeId, 4, GLES20.GL_FLOAT, false, 0, color)
        GLES20.glUniformMatrix4fv(mvpUniformId, 1, false, mvp, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    override fun clear() {
        GLES20.glDeleteProgram(progId)
        GLES20.glDeleteShader(vsId)
        GLES20.glDeleteShader(fsId)
    }
}