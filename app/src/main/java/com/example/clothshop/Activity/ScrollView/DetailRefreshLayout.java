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

    public void setmDetailScrollView(DetailScrollView mDetailScrollView) {
        this.mDetailScrollView = mDetailScrollView;
    }

    public DetailRefreshLayout(Context context) {
        super(context);
    }

    public DetailRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
// TODO Auto-generated method stub
        if (inRangeOfView(mViewPager,ev)){
            return mDetailScrollView.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if (inRangeOfView(mViewPager,ev)){
            return mDetailScrollView.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


}
