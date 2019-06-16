package com.feedr.blog.nightswitchlibrary

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

/**
 * Created by Palak SD on 14-07-2017.
 */
class NightSwitch : View {

    private var animating: Boolean = false
    private var isInRight: Boolean = false
    private var borderPadding: Int = 0
    private var offsetX: Float = 0.toFloat()
    private var thumbCircleRadius: Int = 0
    private var offsetY: Int = 0
    private var offsetCircleRadius: Float = 0.toFloat()
    private var offColor: Int = 0
    private var onColor: Int = 0
    private var bgColor: Int = 0

    private var bgRectF: RectF? = null
    private var bgPaint: Paint? = null
    private var circlePaint: Paint? = null
    private var animatorSet: AnimatorSet? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    internal fun init(attrs: AttributeSet?) {

        //here is where we will fetch attributes passed in xml
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NightSwitch)
            borderPadding = typedArray.getDimensionPixelSize(R.styleable.NightSwitch_switch_inner_padding, 5)
            offColor = typedArray.getColor(R.styleable.NightSwitch_switch_off_color, ContextCompat.getColor(context, R.color.cyan))
            onColor = typedArray.getColor(R.styleable.NightSwitch_switch_on_color, ContextCompat.getColor(context, R.color.purple))
        }

        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //These are just best looking reference dimens to set the height of the view according to given width.
        //you can also consider this as aspect ratio.
        val desiredWidth = dpToPx(120)
        val desiredHeight = dpToPx(50)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val width: Int
        val height: Int

        //Measure Width
        if (widthMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize)
        } else {
            //Be whatever you want
            width = desiredWidth
        }

        //Now, time to calculate height according to calculated width in reference to desired width and height!
        height = width * desiredHeight / desiredWidth

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBg()
    }

    private fun createBg() {

        //Initial offsetX is on the border padding left.
        offsetX = borderPadding.toFloat()
        bgColor = offColor

        bgRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.color = bgColor

        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint!!.color = Color.WHITE

        thumbCircleRadius = height / 2 - borderPadding
    }

    override fun onDraw(canvas: Canvas) {

        bgPaint!!.color = bgColor

        canvas.drawRoundRect(bgRectF!!, (height / 2).toFloat(), (height / 2).toFloat(), bgPaint!!)

        canvas.drawCircle(offsetX + thumbCircleRadius, (borderPadding + thumbCircleRadius).toFloat(), thumbCircleRadius.toFloat(), circlePaint!!)

        canvas.drawCircle(offsetX, offsetY.toFloat(), offsetCircleRadius, bgPaint!!)

        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_UP -> if (!animating) {
                if (!isInRight) {
                    animateViewToRight()
                } else {
                    animateViewToLeft()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animateViewToRight() {

        if (animatorSet == null) animatorSet = AnimatorSet()

        val objectAnimatorX = ObjectAnimator.ofFloat(this, "offsetX", borderPadding.toFloat(), (width - thumbCircleRadius * 2 - borderPadding).toFloat())
        objectAnimatorX.setDuration(500)
        objectAnimatorX.setInterpolator(AccelerateDecelerateInterpolator())

        val objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this, "offsetCircleRadius", 0F, thumbCircleRadius.toFloat())
        objectAnimatorOffsetRadius.setDuration(400)
        objectAnimatorOffsetRadius.setInterpolator(AccelerateDecelerateInterpolator())

        val objectAnimatorColor = ObjectAnimator.ofArgb(this, "bgColor", offColor, onColor)
        objectAnimatorColor.duration = 500
        objectAnimatorColor.interpolator = LinearInterpolator()

        val objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this, "offsetY", thumbCircleRadius, borderPadding)
        objectAnimatorVerticalOffset.duration = 400
        objectAnimatorVerticalOffset.interpolator = AccelerateDecelerateInterpolator()

        animatorSet!!.playTogether(objectAnimatorX, objectAnimatorOffsetRadius, objectAnimatorVerticalOffset, objectAnimatorColor)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                animating = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isInRight = true
                animating = false
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animatorSet!!.start()
    }

    private fun animateViewToLeft() {

        if (animatorSet == null) animatorSet = AnimatorSet()

        val objectAnimatorX = ObjectAnimator.ofFloat(this, "offsetX", (width - thumbCircleRadius * 2 - borderPadding).toFloat(), borderPadding.toFloat())
        objectAnimatorX.setDuration(500)
        objectAnimatorX.setInterpolator(AccelerateDecelerateInterpolator())

        val objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this, "offsetCircleRadius", thumbCircleRadius.toFloat(), 0F)
        objectAnimatorOffsetRadius.setDuration(400)
        objectAnimatorOffsetRadius.setInterpolator(AccelerateDecelerateInterpolator())

        val objectAnimatorColor = ObjectAnimator.ofArgb(this, "bgColor", onColor, offColor)
        objectAnimatorColor.duration = 500
        objectAnimatorColor.interpolator = LinearInterpolator()

        val objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this, "offsetY", borderPadding, thumbCircleRadius)
        objectAnimatorVerticalOffset.duration = 400
        objectAnimatorVerticalOffset.interpolator = AccelerateDecelerateInterpolator()

        animatorSet!!.playTogether(objectAnimatorX, objectAnimatorOffsetRadius, objectAnimatorVerticalOffset, objectAnimatorColor)
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                animating = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isInRight = false
                animating = false
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animatorSet!!.start()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), this.context.resources.displayMetrics).toInt()
    }

    fun setOffsetX(offsetX: Float) {
        this.offsetX = offsetX
    }

    fun setOffsetCircleRadius(offsetCircleRadius: Float) {
        this.offsetCircleRadius = offsetCircleRadius
    }

    fun setOffsetY(offsetY: Int) {
        this.offsetY = offsetY
    }

    //Calling invalidate only from this method to update whole view.
    //This setter will be called by animator to set new value everytime.
    fun setBgColor(bgColor: Int) {
        this.bgColor = bgColor
        invalidate()
    }
}
