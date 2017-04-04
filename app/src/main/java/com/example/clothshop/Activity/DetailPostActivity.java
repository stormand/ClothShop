package com.example.clothshop.Activity;

import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.example.clothshop.Activity.ScrollView.DetailScrollView;
import com.example.clothshop.R;
import com.example.clothshop.adapter.ImagePagerAdapter;

import java.util.ArrayList;

public class DetailPostActivity extends AppCompatActivity{

    private ViewPager mImageViewPager;
    private GestureDetector detector;
    private DetailScrollView mDetailScrollView;
    private ArrayList<String> mImageList;
    private ImageView imageView;
    private ImageView[] imageViews;
    //包裹点点的LinearLayout
    private ViewGroup mPointGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_detail_post);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        mDetailScrollView= (DetailScrollView) findViewById(R.id.detail_scroll_view);
        mDetailScrollView.setGestureDetector(detector);
        initViewPager();
        mDetailScrollView.setmViewPager(mImageViewPager);
        initToolbar();
        initPointer();

    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        lp.setMargins(0,getStatusBarHeight(), 0, 0);
        toolbar.setLayoutParams(lp);
        setSupportActionBar(toolbar);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initViewPager(){
        mImageViewPager= (ViewPager) findViewById(R.id.detail_view_pager);
        mImageList=new ArrayList<String>();
        mImageList.add("1");
        mImageList.add("2");
        mImageList.add("3");
        mImageViewPager.setAdapter(new ImagePagerAdapter(mImageList,DetailPostActivity.this));
        mImageViewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    //导航小白点
    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup= (ViewGroup) findViewById(R.id.detail_point_view_Group);
        imageViews = new ImageView[mImageList.size()];
        for (int i = 0; i < imageViews.length; i++) {
            imageView = new ImageView(this);
            //设置控件的宽高
            //imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            //设置控件的padding属性
            LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(20, 20);
            lp.leftMargin=10;
            lp.rightMargin=10;
            imageView.setLayoutParams(lp);
            imageViews[i] = imageView;
                         //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                imageViews[i].setBackgroundResource(R.drawable.point_focused);
                /**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */
            } else {
                imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
            }
            mPointGroup.addView(imageViews[i]);
        }
    }


         public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                     }

                 //页面滑动完成后执行
            @Override
            public void onPageSelected(int position) {
                //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
                for (int i = 0; i < imageViews.length; i++) {
                    imageViews[position].setBackgroundResource(R.drawable.point_focused);
                    if (position != i) {
                        imageViews[i].setBackgroundResource(R.drawable.point_not_focused);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
         }

}
