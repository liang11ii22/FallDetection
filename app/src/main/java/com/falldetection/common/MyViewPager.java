package com.falldetection.common;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Liang
 */
public class MyViewPager extends ViewPager {
    PointF downP = new PointF();// touch point
    PointF curP = new PointF(); // current point
    OnSingleTouchListener onSingleTouchListener;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent ) {

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        //record current pos
        if(getChildCount()<=1)
        {
            return super.onTouchEvent(arg0);
        }
        curP.x = arg0.getX();
        curP.y = arg0.getY();

        if(arg0.getAction() == MotionEvent.ACTION_DOWN)
        {

            //record touch pos
            // downP = curP X
            downP.x = arg0.getX();
            downP.y = arg0.getY();

            getParent().requestDisallowInterceptTouchEvent(true);//notify parent
        }

        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            getParent().requestDisallowInterceptTouchEvent(true);//notify parent
        }

        if(arg0.getAction() == MotionEvent.ACTION_UP || arg0.getAction() == MotionEvent.ACTION_CANCEL){
            getParent().requestDisallowInterceptTouchEvent(false);
            if(downP.x==curP.x && downP.y==curP.y){

                return true;
            }
        }
        super.onTouchEvent(arg0);
        return true;
    }

    /**
     *  click
     */
     public void onSingleTouch() {
         if (onSingleTouchListener!= null) {

             onSingleTouchListener.onSingleTouch();
             }
         }

    /**
     * create click inteface
     *
     */
    public interface OnSingleTouchListener {
        public void onSingleTouch();
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

}
