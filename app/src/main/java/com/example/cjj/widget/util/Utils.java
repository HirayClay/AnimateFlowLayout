package com.example.cjj.widget.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.HashMap;


/**
 * Created by CJJ on 2016/8/19.
 *
 */
public class Utils {

    private static final HashMap<Integer,TranslateAnimation> animationPool = new HashMap<>();
    private AnimationListener listener;
    private int counter = 0;
    private int limit = -1;


    public  Utils apply(ViewGroup group,int start,int end){
        limit = Math.abs(end - start);
        //往前还是往后移动
        if (start < end)//向前移动
        {
            for (int i = start; i < end; i++) {
                View targetView = group.getChildAt(i);
                View startView = group.getChildAt(i+1);
                animateView(startView,targetView);
            }
        }else {
            for (int i = start; i >end ; i--) {
                View targetView = group.getChildAt(i);
                View startView = group.getChildAt(i-1);
                animateView(startView,targetView);
            }
        }
        return this;
    }

    public void animateView(final View startView , View targetView)
    {
        ValueAnimator horizontalAnim =ValueAnimator.ofFloat(startView.getLeft(),targetView.getLeft());
        horizontalAnim.setDuration(400);
        horizontalAnim.setTarget(startView);
        ValueAnimator verticalAnim = ValueAnimator.ofFloat(startView.getTop(),targetView.getTop());
        verticalAnim.setDuration(400);
        verticalAnim.setTarget(startView);

        verticalAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startView.setY((Float) animation.getAnimatedValue());
            }
        });
        horizontalAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startView.setX((Float) animation.getAnimatedValue());
            }
        });
        verticalAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                counter++;
                if (counter == limit){
                    listener.end();
                    counter= 0;
                    limit = -1;
                }

            }
        });
        horizontalAnim.start();
        verticalAnim.start();
    }

    public int wrapInKey(int a,int b)
    {
        return a*a*a - 2+b*b*b - 1;
    }

    public void end(AnimationListener listener){
        this.listener = listener;
    }

    public interface AnimationListener{
        void end();
    }



    private static class AnimatinInfo{
        private View view;
        private int startx;
        private int starty;
        private int endx;
        private int endy;

        public void execute()
        {
            TranslateAnimation translateAnimation = new TranslateAnimation(startx,starty,endx,endy);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            view.startAnimation(translateAnimation);
        }
    }
}
