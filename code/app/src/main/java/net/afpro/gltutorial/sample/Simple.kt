package net.afpro.gltutorial.sample

import android.opengl.GLES20
import net.afpro.gltutorial.Mat
import net.afpro.gltutorial.floatBufferOf
import net.afpro.gltutorial.shaders.SimpleShaderProgram
import net.afpro.gltutorial.withProg


class Simple : SamplePart {
    var prog: SimpleShaderProgram? = null
    val mvp = Mat().asPerspective(60f, 1f, 0.1f, 10f) * Mat().asLookAt(0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)
    val pos = floatBufferOf(
            0f, 2f, 0f,
            -2f, -1f, 0f,
            2f, -1f, 0f)
    val color = floatBufferOf(
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f)

    override val name: String
        get() = "Simple"

    override fun setup() {
        prog = SimpleShaderProgram()
    }

    override fun draw(delta: Float) {
        prog?.withProg {
            GLES20.glEnableVertexAttribArray(aPos)
            GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, pos)
            GLES20.glEnableVertexAttribArray(aColor)
            GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, color)
            GLES20.glUniformMatrix4fv(uMvp, 1, false, mvp.data, mvp.offset)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        }
    }
}