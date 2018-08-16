package net.afpro.gltutorial

import android.opengl.GLES20
import android.opengl.GLU
import java.util.*

const val TAG = "OpenGLTutorial"

class GLErrorGuard {
    private var hasFinalizer = false
    private val clearFnList by lazy { LinkedList<() -> Unit>() }

    fun addClearFn(f: () -> Unit) {
        hasFinalizer = true
        clearFnList.add(f)
    }

    fun invokeClearFn() {
        clearFnList.forEach { it() }

        hasFinalizer = false
        clearFnList.clear()
    }

    inline fun assertNoError(hasError: Boolean, extraError: () -> String = { "" }) {
        val err = GLES20.glGetError()
        if (err == GLES20.GL_NO_ERROR && !hasError)
            return

        invokeClearFn()

        if (err == GLES20.GL_NO_ERROR) {
            throw RuntimeException("gl error manually. ${extraError()}")
        } else {
            val errStr = GLU.gluErrorString(err)
            throw RuntimeException("gl error $err: $errStr. ${extraError()}")
        }
    }

    inline fun assertNoError(extraError: () -> String = { "" }) {
        assertNoError(false, extraError)
    }

    companion object {
        inline fun <R> run(f: (GLErrorGuard) -> R): R {
            val guard = GLErrorGuard()
            return f(guard)
        }
    }
}

fun createAndCompileShader(type: Int, source: String): Int = GLErrorGuard.run { guard ->
    val id = GLES20.glCreateShader(type)
    guard.assertNoError(id == 0)
    guard.addClearFn { GLES20.glDeleteShader(id) }

    GLES20.glShaderSource(id, source)
    guard.assertNoError { GLES20.glGetShaderInfoLog(id) }

    GLES20.glCompileShader(id)
    guard.assertNoError { GLES20.glGetShaderInfoLog(id) }

    id
}

fun createAndLinkProgram(vs: Int, fs: Int = 0): Int = GLErrorGuard.run { guard ->
    val id = GLES20.glCreateProgram()
    guard.assertNoError(id == 0)
    guard.addClearFn { GLES20.glDeleteProgram(id) }

    GLES20.glAttachShader(id, vs)
    guard.assertNoError { GLES20.glGetProgramInfoLog(id) }

    if (fs != 0) {
        GLES20.glAttachShader(id, fs)
        guard.assertNoError { GLES20.glGetProgramInfoLog(id) }
    }

    GLES20.glLinkProgram(id)
    guard.assertNoError { GLES20.glGetProgramInfoLog(id) }

    id
}
