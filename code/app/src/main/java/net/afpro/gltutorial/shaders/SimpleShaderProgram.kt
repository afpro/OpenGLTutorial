package net.afpro.gltutorial.shaders

import net.afpro.gltutorial.ShaderProgram


class SimpleShaderProgram : ShaderProgram("simple") {
    var uMvp = 0
    var aColor = 0
    var aPos = 0

    init {
        uMvp = uniformId("u_mvp")
        aColor = attributeId("a_color")
        aPos = attributeId("a_pos")
    }
}