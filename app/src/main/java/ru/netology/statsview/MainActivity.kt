package ru.netology.statsview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<StatsView>(R.id.StatsView).data = listOf(
//            500F, 500F, 500F, 500F  // Задача. Smart StatsView
            0.25F, 0.25F, 0.25F,  //Задача. Not Filled

        )
    }
}