package net.afpro.gltutorial.sample

import android.opengl.GLES20
import net.afpro.gltutorial.Mat
import net.afpro.gltutorial.floatBufferOf
import net.afpro.gltutorial.shaders.SimpleShaderProgram
import net.afpro.gltutorial.withProg

class Animated : SamplePart {
    private var prog: SimpleShaderProgram? = null
    val pos = floatBufferOf(
            0f, 2f, 0f,
            -2f, -1f, 0f,
            2f, -1f, 0f)
    val color = floatBufferOf(
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f)
    val mvp: Mat
    val rotate: Mat
    val finalMvp: Mat
    var ang = 0f

    init {
        val mem = FloatArray(48) { 0f }
        mvp = Mat(mem, 0)
        rotate = Mat(mem, 16)
        finalMvp = Mat(mem, 32)

        val p = Mat().asPerspective(60f, 1f, 0.1f, 10f)
        val v = Mat().asLookAt(0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)
        p.mul(v, mvp)
    }

    override val name: String
        get() = "Animated"

    override fun setup() {
        prog = SimpleShaderProgram()
    }

    override fun draw(delta: Float) {
        ang += 270 * delta

        prog?.withProg {
            rotate.asIdentity().rotate(ang, 0f, 1f, 0f)
            mvp.mul(rotate, finalMvp)

            GLES20.glEnableVertexAttribArray(aPos)
            GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, pos)
            GLES20.glEnableVertexAttribArray(aColor)
            GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, color)
            GLES20.glUniformMatrix4fv(uMvp, 1, false, finalMvp.data, finalMvp.offset)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        }
    }
}
