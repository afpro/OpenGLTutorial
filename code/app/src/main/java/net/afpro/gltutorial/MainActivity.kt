package net.afpro.gltutorial

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import net.afpro.gltutorial.sample.SampleRenderer

class MainActivity : AppCompatActivity() {
    private val renderer = SampleRenderer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glPanel.setEGLContextClientVersion(2)
        glPanel.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glPanel.setRenderer(renderer)
        glPanel.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        val itemNames = Array(renderer.all.size + 1) { "" }
        itemNames[0] = "SELECT ONE"
        (1..renderer.all.size).forEach {
            itemNames[it] = renderer.all[it - 1].name
        }

        sampleChooser.adapter = ArrayAdapter<String>(this,
                R.layout.spinner_item, R.id.spinner_item_text,
                itemNames)

        sampleChooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(ignoredView: AdapterView<*>?) {
                renderer.currentPart = null
            }

            override fun onItemSelected(ignoredView: AdapterView<*>?, itemView: View?, pos: Int, id: Long) {
                renderer.currentPart = if (pos == 0) null else renderer.all[pos - 1]
            }
        }
    }
}
