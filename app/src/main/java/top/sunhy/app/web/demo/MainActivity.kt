package top.sunhy.app.web.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.sunhy.app.web.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)

        val fragment = WebFragment.newInstance("file:///android_asset/WebJsBridgeDemo.html")

        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .commit()
    }
}