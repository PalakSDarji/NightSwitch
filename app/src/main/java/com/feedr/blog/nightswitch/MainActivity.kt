package com.feedr.blog.nightswitch

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.feedr.blog.nightswitchlibrary.NightSwitch

/**
 * Created by Palak SD on 03-09-2017
 *
 */
class MainActivity : AppCompatActivity() {

    private var cyanColorBg: Int = 0
    private var purpleColorBg: Int = 0
    private var relativeLayout: RelativeLayout? = null
    private var isInRight: Boolean = false
    private var nightSwitch: NightSwitch? = null
    private var isAnimating: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cyanColorBg = ContextCompat.getColor(this, R.color.cyan_bg)
        purpleColorBg = ContextCompat.getColor(this, R.color.purple_bg)
        relativeLayout = findViewById<View>(R.id.rlContainer) as RelativeLayout

        nightSwitch = findViewById<View>(R.id.nightSwitch) as NightSwitch
        relativeLayout!!.setBackgroundColor(cyanColorBg)

        nightSwitch!!.setOnClickListener(View.OnClickListener {
            if (isAnimating) return@OnClickListener
            if (!isInRight) {
                val valueAnimator = ValueAnimator.ofArgb(cyanColorBg, purpleColorBg)
                valueAnimator.duration = 500
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        isAnimating = true
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        isAnimating = false
                        isInRight = true
                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })
                valueAnimator.addUpdateListener { valueAnimator ->
                    Log.v("Asd", "asd " + valueAnimator.animatedValue)
                    relativeLayout!!.setBackgroundColor(valueAnimator.animatedValue as Int)
                }
                valueAnimator.start()
            } else {
                val valueAnimator = ValueAnimator.ofArgb(purpleColorBg, cyanColorBg)
                valueAnimator.duration = 500
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        isAnimating = true
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        isAnimating = false
                        isInRight = false
                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })
                valueAnimator.addUpdateListener { valueAnimator -> relativeLayout!!.setBackgroundColor(valueAnimator.animatedValue as Int) }
                valueAnimator.start()
            }
        })


    }

}
