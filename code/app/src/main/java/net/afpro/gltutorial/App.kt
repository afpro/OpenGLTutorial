package net.afpro.gltutorial

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        private lateinit var app: App

        fun readAsset(path: String): String {
            return app.assets.open(path).reader().use { it.readText() }
        }
    }
}
