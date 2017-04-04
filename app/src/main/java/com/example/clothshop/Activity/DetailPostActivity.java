package com.example.clothshop.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.clothshop.Activity.ScrollView.DetailScrollView;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.adapter.ImagePagerAdapter;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailPostActivity extends AppCompatActivity{

    private ViewPager mImageViewPager;
    private GestureDetector detector;
    private DetailScrollView mDetailScrollView;
    private String[] imageList;
    private ImageView imageView;
    private ImageView[] imageViews;
    private PostInfo mPostInfo;
    //包裹点点的LinearLayout
    private ViewGroup mPointGroup;

    private GetDataHandler handler;
    private GetDataThread mGetDataThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_detail_post);
        initToolbar();
        getData();


    }

    public void initStatusBar(){
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void initScrollView(){
        mDetailScrollView= (DetailScrollView) findViewById(R.id.detail_scroll_view);
        mDetailScrollView.setGestureDetector(detector);
        mDetailScrollView.setmViewPager(mImageViewPager);
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

    private void getData(){
        mPostInfo=new PostInfo();
        Intent intent=getIntent();
        mPostInfo.setPid(intent.getStringExtra("pid"));
        handler=new GetDataHandler();
        mGetDataThread=new GetDataThread();
        mGetDataThread.start();
    }

    private void initViewPager(){
        mImageViewPager= (ViewPager) findViewById(R.id.detail_view_pager);
        imageList = mPostInfo.getPimage().split("@");
        mImageViewPager.setAdapter(new ImagePagerAdapter(imageList,DetailPostActivity.this));
        mImageViewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    //导航小白点
    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup= (ViewGroup) findViewById(R.id.detail_point_view_Group);
        imageViews = new ImageView[imageList.length];
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

    class GetDataThread extends Thread{
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.POST_ID_ATTR,mPostInfo.getPid());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.DETAIL_POST_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                mPostInfo.setPimage(jsonObject.getString(Model.POST_IMAGE_ATTR));
                mPostInfo.setPtitle(jsonObject.getString(Model.POST_TITLE_ATTR));
                mPostInfo.setUid(jsonObject.getString(Model.POST_UID_ATTR));
                mPostInfo.setPdaytime(jsonObject.getString(Model.POST_DAY_TIME_ATTR));
                mPostInfo.setPcontent(jsonObject.getString(Model.POST_CONTENT_ATTR));
                mPostInfo.setUheight(jsonObject.getString(Model.POST_UHEIGHT_ATTR));
                mPostInfo.setUweight(jsonObject.getString(Model.POST_UWEIGHT_ATTR));
                mPostInfo.setUsex(jsonObject.getString(Model.POST_USEX_ATTR));
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), PublishActivity.SendHandler.SUCCESS);
                }else {
                    showMessage(jsonObject.getString("mes"), PublishActivity.SendHandler.FAILURE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void showMessage(String message,int status){
            Message msg=Message.obtain(handler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class GetDataHandler extends Handler {

        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    initViewPager();
                    initScrollView();
                    initPointer();
                    break;
                case FAILURE:
                    Toast.makeText(DetailPostActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
