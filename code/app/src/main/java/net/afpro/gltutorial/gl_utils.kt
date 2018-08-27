package net.afpro.gltutorial

import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.Matrix
import java.io.Closeable
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

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

open class ShaderProgram : Closeable {
    private val progId: Int

    constructor(vs: String, fs: String) {
        // create vertex shader
        val vsId = createAndCompileShader(GLES20.GL_VERTEX_SHADER, vs)
        // create fragment shader
        val fsId = createAndCompileShader(GLES20.GL_FRAGMENT_SHADER, fs)
        // create program
        progId = createAndLinkProgram(vsId, fsId)
        // mark shader deletion flag
        GLES20.glDeleteShader(vsId)
        GLES20.glDeleteShader(fsId)
    }

    protected constructor(assetPrefix: String) : this(App.readAsset("${assetPrefix}_vs.glsl"), App.readAsset("${assetPrefix}_fs.glsl"))

    override fun close() {
        GLES20.glDeleteProgram(progId)
    }

    fun useProg() {
        GLES20.glUseProgram(progId)
    }

    fun uniformId(name: String): Int {
        return GLES20.glGetUniformLocation(progId, name)
    }

    fun attributeId(name: String): Int {
        return GLES20.glGetAttribLocation(progId, name)
    }
}


inline fun <T : ShaderProgram, R> T.withProg(block: T.() -> R): R {
    useProg()
    return block(this)
}


class Vec(val data: FloatArray = floatArrayOf(0f, 0f, 0f, 1f), val offset: Int = 0) {
    var x: Float
        get() = data[offset]
        set(value) {
            data[offset] = value
        }

    var y: Float
        get() = data[offset + 1]
        set(value) {
            data[offset + 1] = value
        }

    var z: Float
        get() = data[offset + 2]
        set(value) {
            data[offset + 2] = value
        }

    var w: Float
        get() = data[offset + 3]
        set(value) {
            data[offset + 3] = value
        }

    fun normalize(): Vec {
        if (w > 1e6 && w < -1e6) {
            x /= w
            y /= w
            z /= w
            w = 1f
        }
        return this
    }

    override fun toString(): String {
        return "vec[$x, $y, $z, $w]"
    }
}

class Mat(val data: FloatArray = FloatArray(16) { 0f }, val offset: Int = 0) {

    fun fill(v: Float) {
        data.fill(v, offset, offset + 16)
    }

    operator fun get(i: Int): Float {
        return data[i + offset]
    }

    operator fun set(i: Int, v: Float) {
        data[i + offset] = v
    }

    operator fun get(r: Int, c: Int): Float {
        return data[r + c * 4 + offset]
    }

    operator fun set(r: Int, c: Int, v: Float) {
        data[r + c * 4 + offset] = v
    }

    fun asIdentity(): Mat {
        Matrix.setIdentityM(data, offset)
        return this
    }

    fun asLookAt(eyeX: Float, eyeY: Float, eyeZ: Float,
                 centerX: Float, centerY: Float, centerZ: Float,
                 upX: Float = 0f, upY: Float = 1f, upZ: Float = 0f): Mat {
        Matrix.setLookAtM(data, offset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
        return this
    }

    fun asLookAt(eye: Vec, center: Vec, up: Vec = Vec(data = floatArrayOf(0f, 1f, 0f, 1f))): Mat {
        Matrix.setLookAtM(data, offset,
                eye.x, eye.y, eye.z,
                center.x, center.y, center.z,
                up.x, up.y, up.z)
        return this
    }

    fun asPerspective(fov: Float, aspect: Float, near: Float, far: Float): Mat {
        Matrix.perspectiveM(data, offset, fov, aspect, near, far)
        return this
    }

    fun asFrustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat {
        Matrix.frustumM(data, offset, left, right, bottom, top, near, far)
        return this
    }

    fun asOrtho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat {
        Matrix.orthoM(data, offset, left, right, bottom, top, near, far)
        return this
    }

    fun mul(right: Mat, result: Mat = Mat()): Mat {
        Matrix.multiplyMM(result.data, result.offset, data, offset, right.data, right.offset)
        return result
    }

    operator fun times(right: Mat): Mat {
        return mul(right)
    }

    fun mul(right: Vec, result: Vec = Vec()): Vec {
        Matrix.multiplyMV(result.data, result.offset, data, offset, right.data, right.offset)
        return result
    }

    operator fun times(right: Vec): Vec {
        return mul(right)
    }

    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f): Mat {
        Matrix.translateM(data, offset, x, y, z)
        return this
    }

    fun rotate(a: Float, x: Float, y: Float, z: Float): Mat {
        Matrix.rotateM(data, offset, a, x, y, z)
        return this
    }

    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f): Mat {
        Matrix.scaleM(data, offset, x, y, z)
        return this
    }
}


fun floatBufferOf(vararg v: Float): Buffer = ByteBuffer.allocateDirect(4 * v.size)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(v)
        .position(0)

fun intBufferOf(vararg v: Int): Buffer = ByteBuffer.allocateDirect(4 * v.size)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer()
        .put(v)
        .position(0)


object Timer {
    private var prev = System.currentTimeMillis()
    private var paused = false

    var time: Float = 0f
        private set

    fun advance() {
        if (paused)
            return
        val now = System.currentTimeMillis()
        time += (now - prev) * 0.001f
        prev = now
    }

    fun pause() {
        advance()
        paused = true
    }

    fun resume() {
        prev = System.currentTimeMillis()
        paused = false
    }
}
