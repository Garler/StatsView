package ru.netology.statsview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StatsView>(R.id.StatsView)
        view.data = listOf(
//            500F, 500F, 500F, 500F  // Задача. Smart StatsView
            0.25F, 0.25F, 0.25F, 0.25F //Задача. Not Filled

        )
        val textView = findViewById<TextView>(R.id.label)
        view.startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.animation)
                .apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        @SuppressLint("SetTextI18n")
                        override fun onAnimationStart(animation: Animation?) {
                            textView.text = "started"
                        }
                        @SuppressLint("SetTextI18n")
                        override fun onAnimationEnd(animation: Animation?) {
                            textView.text = "ended"
                        }
                        @SuppressLint("SetTextI18n")
                        override fun onAnimationRepeat(animation: Animation?) {
                            textView.text = "repeat"
                        }
                    }
                    )
                }
        )
    }
}

