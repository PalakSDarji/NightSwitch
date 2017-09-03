package com.feedr.blog.nightswitch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Palak SD on 14-07-2017.
 */

public class NightSwitch extends View {

    private static final String TAG = NightSwitch.class.getSimpleName();
    private int width;
    private int height;
    private RectF bgRectF;
    private Paint bgPaint,bg1Paint;
    private Paint circlePaint;
    private boolean animating;
    private boolean isInRight;
    private int borderPadding;
    private float maskX;
    private int circleRadius;
    private int verticalOffset;
    private Paint offsetPaint;
    private float offsetRadius;
    private AnimatorSet animatorSet;
    private int cyanColor;
    private int purpleColor;
    private int bgColor;

    public NightSwitch(Context context) {
        super(context);
    }

    public NightSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createBg();
    }

    private void createBg() {

        width = getWidth();
        height = getHeight();
        borderPadding = dpToPx(5);
        maskX = borderPadding;
        cyanColor = ContextCompat.getColor(getContext(),R.color.cyan);
        purpleColor = ContextCompat.getColor(getContext(),R.color.purple);
        bgColor = cyanColor;

        bgRectF = new RectF(0,0,width,height);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgPaint.setPathEffect(null);

        bg1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg1Paint.setColor(Color.BLACK);
        bg1Paint.setStyle(Paint.Style.FILL_AND_STROKE);
        bg1Paint.setPathEffect(null);


        offsetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        offsetPaint.setColor(Color.RED);
        offsetPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offsetPaint.setPathEffect(null);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setPathEffect(null);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        bgPaint.setColor(bgColor);
        canvas.drawRoundRect(bgRectF,dpToPx(25),dpToPx(25),bgPaint);
        circleRadius = (height/2) - (borderPadding);

        canvas.drawCircle(maskX + circleRadius, borderPadding + circleRadius ,circleRadius,circlePaint);

        canvas.drawCircle(maskX,verticalOffset,offsetRadius,bgPaint);

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

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this,"maskX",borderPadding,getWidth() - (circleRadius*2)- borderPadding);
        objectAnimatorX.setDuration(500);
        objectAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this,"offsetRadius",0,circleRadius);
        objectAnimatorOffsetRadius.setDuration(400);
        objectAnimatorOffsetRadius.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorColor = ObjectAnimator.ofArgb(this,"bgColor",cyanColor,purpleColor);
        objectAnimatorColor.setDuration(500);
        objectAnimatorColor.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this,"verticalOffset",circleRadius,borderPadding);
        objectAnimatorVerticalOffset.setDuration(400);
        objectAnimatorVerticalOffset.setInterpolator(new DecelerateInterpolator());

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

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this,"maskX",getWidth() - (circleRadius*2)- borderPadding,borderPadding);
        objectAnimatorX.setDuration(500);
        objectAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorOffsetRadius = ObjectAnimator.ofFloat(this,"offsetRadius",circleRadius,0);
        objectAnimatorOffsetRadius.setDuration(400);
        objectAnimatorOffsetRadius.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator objectAnimatorColor = ObjectAnimator.ofArgb(this,"bgColor",purpleColor,cyanColor);
        objectAnimatorColor.setDuration(500);
        objectAnimatorColor.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorVerticalOffset = ObjectAnimator.ofInt(this,"verticalOffset",borderPadding,circleRadius);
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

    public int convertPxToDp(int px) {
        DisplayMetrics displayMetrics = this.getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getContext().getResources().getDisplayMetrics());
    }

    public void setMaskX(float maskX) {
        this.maskX = maskX;
        invalidate();
    }

    public void setOffsetRadius(float offsetRadius){
        this.offsetRadius = offsetRadius;
        //invalidate();
    }

    public void setVerticalOffset(int verticalOffset){
        this.verticalOffset = verticalOffset;
        //invalidate();
    }

    public void setBgColor(int bgColor){
        this.bgColor = bgColor;
        invalidate();
    }

}
