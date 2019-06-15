package com.feedr.blog.nightswitch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Palak SD on 14-07-2017.
 */
public class NightSwitch extends View {

    private static final String TAG = NightSwitch.class.getSimpleName();

    private int width,height;
    private boolean animating;
    private boolean isInRight;
    private int borderPadding;
    private float offsetX;
    private int thumbCircleRadius;
    private int offsetY;
    private float offsetCircleRadius;
    private int offColor;
    private int onColor;
    private int bgColor;

    private RectF bgRectF;
    private Paint bgPaint,circlePaint;
    private AnimatorSet animatorSet;

    public NightSwitch(Context context) {
        super(context);
        init(null);
    }

    public NightSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NightSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    void init(AttributeSet attrs) {

        //here is where we will fetch attributes passed in xml
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NightSwitch);
            borderPadding = typedArray.getDimensionPixelSize(R.styleable.NightSwitch_switch_inner_padding, 5);
            offColor = typedArray.getColor(R.styleable.NightSwitch_switch_off_color, ContextCompat.getColor(getContext(),R.color.cyan));
            onColor = typedArray.getColor(R.styleable.NightSwitch_switch_on_color, ContextCompat.getColor(getContext(),R.color.purple));
        }

        setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //These are just best looking reference dimens to set the height of the view according to given width.
        //you can also consider this as aspect ratio.
        int desiredWidth = dpToPx(120);
        int desiredHeight = dpToPx(50);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Now, time to calculate height according to calculated width in reference to desired width and height!
        height = width*desiredHeight/desiredWidth;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createBg();
    }

    private void createBg() {

        width = getWidth();
        height = getHeight();

        //Initial offsetX is on the border padding left.
        offsetX = borderPadding;
        bgColor = offColor;

        bgRectF = new RectF(0,0,width,height);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);

        thumbCircleRadius = (height/2) - (borderPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        bgPaint.setColor(bgColor);

        canvas.drawRoundRect(bgRectF,height/2,height/2,bgPaint);

        canvas.drawCircle(offsetX + thumbCircleRadius, borderPadding + thumbCircleRadius, thumbCircleRadius,circlePaint);

        canvas.drawCircle(offsetX, offsetY, offsetCircleRadius,bgPaint);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if(!animating){
                    if(!isInRight){
                        animateViewToRight();
                    }
                    else{
                        animateViewToLeft();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void animateViewToRight() {

        if(animatorSet == null) animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this,"offsetX",borderPadding,getWidth() - (thumbCircleRadius *2)- borderPadding);
        objectAnimatorX.setDuration(500);
        objectAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this,"offsetCircleRadius",0, thumbCircleRadius);
        objectAnimatorOffsetRadius.setDuration(400);
        objectAnimatorOffsetRadius.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorColor = ObjectAnimator.ofArgb(this,"bgColor", offColor, onColor);
        objectAnimatorColor.setDuration(500);
        objectAnimatorColor.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this,"offsetY", thumbCircleRadius,borderPadding);
        objectAnimatorVerticalOffset.setDuration(400);
        objectAnimatorVerticalOffset.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.playTogether(objectAnimatorX,objectAnimatorOffsetRadius,objectAnimatorVerticalOffset,objectAnimatorColor);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isInRight = true;
                animating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorSet.start();
    }

    private void animateViewToLeft() {

        if(animatorSet == null) animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this,"offsetX",getWidth() - (thumbCircleRadius *2)- borderPadding,borderPadding);
        objectAnimatorX.setDuration(500);
        objectAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this,"offsetCircleRadius", thumbCircleRadius,0);
        objectAnimatorOffsetRadius.setDuration(400);
        objectAnimatorOffsetRadius.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorColor = ObjectAnimator.ofArgb(this,"bgColor", onColor, offColor);
        objectAnimatorColor.setDuration(500);
        objectAnimatorColor.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this,"offsetY",borderPadding, thumbCircleRadius);
        objectAnimatorVerticalOffset.setDuration(400);
        objectAnimatorVerticalOffset.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.playTogether(objectAnimatorX,objectAnimatorOffsetRadius,objectAnimatorVerticalOffset,objectAnimatorColor);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isInRight = false;
                animating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorSet.start();
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getContext().getResources().getDisplayMetrics());
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetCircleRadius(float offsetCircleRadius){
        this.offsetCircleRadius = offsetCircleRadius;
    }

    public void setOffsetY(int offsetY){
        this.offsetY = offsetY;
    }

    //Calling invalidate only from this method to update whole view.
    //This setter will be called by animator to set new value everytime.
    public void setBgColor(int bgColor){
        this.bgColor = bgColor;
        invalidate();
    }

}
