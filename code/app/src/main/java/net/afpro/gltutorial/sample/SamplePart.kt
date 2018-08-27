package net.afpro.gltutorial.sample

interface SamplePart {
    val name: String
    fun setup() {}
    fun draw(delta: Float)
    fun clear() {}
}
