package com.feedr.blog.nightswitch;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Palak SD on 03-09-2017
 *
 */
public class MainActivity extends AppCompatActivity {

    private int cyanColorBg;
    private int purpleColorBg;
    private RelativeLayout relativeLayout;
    private boolean isInRight;
    private NightSwitch nightSwitch;
    private boolean isAnimating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cyanColorBg = ContextCompat.getColor(this,R.color.cyan_bg);
        purpleColorBg = ContextCompat.getColor(this,R.color.purple_bg);
        relativeLayout = (RelativeLayout) findViewById(R.id.rlContainer);

        nightSwitch = (NightSwitch) findViewById(R.id.nightSwitch);
        relativeLayout.setBackgroundColor(cyanColorBg);

        nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isAnimating) return;
                if(!isInRight){
                    ValueAnimator valueAnimator = ValueAnimator.ofArgb(cyanColorBg,purpleColorBg);
                    valueAnimator.setDuration(500);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            isAnimating = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            isAnimating = false;
                            isInRight = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Log.v("Asd","asd "+ valueAnimator.getAnimatedValue());
                            relativeLayout.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                        }
                    });
                    valueAnimator.start();
                }
                else{
                    ValueAnimator valueAnimator = ValueAnimator.ofArgb(purpleColorBg,cyanColorBg);
                    valueAnimator.setDuration(500);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            isAnimating = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            isAnimating = false;
                            isInRight = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            relativeLayout.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                        }
                    });
                    valueAnimator.start();
                }
            }
        });


    }

}
