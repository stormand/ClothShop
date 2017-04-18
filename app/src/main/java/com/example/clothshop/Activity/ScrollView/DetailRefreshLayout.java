package com.example.clothshop.Activity.ScrollView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.clothshop.Activity.DetailPostActivity;

/**
 * Created by 一凡 on 2017/4/5.
 */

public class DetailRefreshLayout extends SwipeRefreshLayout {
    ViewPager mViewPager;
    DetailScrollView mDetailScrollView;
    private float startY;
    private float startX;
    // 记录viewPager是否拖拽的标记
    private boolean mIsVpDragger;
    private final int mTouchSlop;


    public void setmDetailScrollView(DetailScrollView mDetailScrollView) {
        this.mDetailScrollView = mDetailScrollView;
    }

    public DetailRefreshLayout(Context context) {
        super(context);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public DetailRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if(mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if(distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        if (view==null){
            return false;
        }
        int []location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x
                || ev.getX() > (x + view.getWidth())
                || ev.getY() < y
                || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    public void setmViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (inRangeOfView(mViewPager,ev)){
//            mViewPager.onTouchEvent(ev);
//            return mDetailScrollView.onTouchEvent(ev);
//        }
//        return super.onTouchEvent(ev);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev){
//        if (inRangeOfView(mViewPager,ev)){
//            mViewPager.onTouchEvent(ev);
//            return mDetailScrollView.onTouchEvent(ev);
//        }
//        return super.dispatchTouchEvent(ev);
//    }


}
