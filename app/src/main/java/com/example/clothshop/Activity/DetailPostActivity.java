package com.example.clothshop.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.clothshop.Activity.ScrollView.DetailRefreshLayout;
import com.example.clothshop.Activity.ScrollView.DetailScrollView;
import com.example.clothshop.DB.DBHelper;
import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Fragment.HomeFragment;
import com.example.clothshop.Info.CommentsInfo;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.adapter.CommentRecyclerAdapter;
import com.example.clothshop.adapter.ImagePagerAdapter;
import com.example.clothshop.utils.HttpPostUtil;

import org.apache.http.conn.MultihomePlainSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DetailPostActivity extends AppCompatActivity{

    private ViewPager mImageViewPager;
    private DetailScrollView mDetailScrollView;
    private String[] imageList;
    private ImageView imageView;
    private ImageView[] imageViews;
    private PostInfo mPostInfo;
    private ArrayList<CommentsInfo> mCommentsInfoList;

    private DetailRefreshLayout mSwipeRefreshLayout;

    //包裹点点的LinearLayout
    private ViewGroup mPointGroup;

    private ImagePagerAdapter mImagePagerAdapter;

    private TextView mDetailTitle;
    private TextView mDetailContent;
    private TextView mDetailUname;
    private TextView mDetailUage;
    private TextView mDetailUweight;
    private TextView mDetailUheight;
    private TextView mDetailDateTime;
    private TextView mDetailUsex;

    private Button mLoveButton;
    private Button mCollectionButton;
    private DatabaseUtil mDatabaseUtil;

    private RecyclerView mDetailCommentRecyclerView;

    private GetDataHandler handler;
    private GetDataThread mGetDataThread;

    private GetCommentHanlder mCommentHandler;
    private GetCommentThread mGetCommentThread;
    private CommentRecyclerAdapter mCommentadapter;
    private LinearLayoutManager mLayoutManager;
    private SendCommentThread mSendCommentThread;
    private SendCommentHandler mSendCommentHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        initToolbar();
        getData();
        initLayout();
        getCommentData();
        loveAndCollect();
    }

    private void initToolbar(){
        //refreshlayout
        mSwipeRefreshLayout= (DetailRefreshLayout)findViewById(R.id.detail_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDataThread getDataThread=new GetDataThread();
                getDataThread.start();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initLayout(){
        //viewPager
        mImageViewPager= (ViewPager) findViewById(R.id.detail_view_pager);
        imageList=new String[]{};
        mImagePagerAdapter=new ImagePagerAdapter(imageList,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mPointGroup= (ViewGroup) findViewById(R.id.detail_point_view_Group);
        //text
        mDetailTitle= (TextView) findViewById(R.id.detail_title);
        mDetailContent= (TextView) findViewById(R.id.detail_content);
        mDetailUname= (TextView) findViewById(R.id.detail_uname);
        mDetailUage= (TextView) findViewById(R.id.detail_uage);
        mDetailUweight= (TextView) findViewById(R.id.detail_uweight);
        mDetailUheight= (TextView) findViewById(R.id.detail_uheight);
        mDetailDateTime= (TextView) findViewById(R.id.detail_date_time);
        mDetailUsex=(TextView) findViewById(R.id.detail_usex);
        //love&collection
        mLoveButton= (Button) findViewById(R.id.love_button);
        mCollectionButton= (Button) findViewById(R.id.collection_button);
        //scrollView
        mDetailScrollView= (DetailScrollView) findViewById(R.id.detail_scroll_view);
        mDetailScrollView.setmViewPager(mImageViewPager);

    }

    private void setPostInfoView(){
        mDetailTitle.setText(mPostInfo.getPtitle());
        mDetailContent.setText(mPostInfo.getPcontent());
        mDetailUweight.setText(mPostInfo.getUweight());
        mDetailUheight.setText(mPostInfo.getUheight());
        mDetailDateTime.setText(mPostInfo.getPdaytime());
        mDetailUage.setText(mPostInfo.getUage());
        mDetailUsex.setText(mPostInfo.getUsex());
        mDetailUname.setText(mPostInfo.getUname());
    }

    private void setViewPager(){
        imageList = mPostInfo.getPimage().split("@");
        mImagePagerAdapter=new ImagePagerAdapter(imageList,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImageViewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    /**
     * 初始化导航小白点，根据getData的图片数目设置小白点数目
     */
    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup.removeAllViews();
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

    private void setLCText(){
        if(mPostInfo.isMyLove()){
            mLoveButton.setText(mPostInfo.getLoveNum());
            //mLoveButton.setTextColor(101010); // TODO: 2017/4/11 change the color
        }else{
            mLoveButton.setText(mPostInfo.getLoveNum());
            //mLoveButton.setTextColor(0);
        }
        if (mPostInfo.isMyCollection()){
            mCollectionButton.setText(getString(R.string.collected));
        }else {
            mCollectionButton.setText(getString(R.string.collection));
        }
    }

    private void loveAndCollect(){
        setLCText();
        mLoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //local
                int loveNum=Integer.valueOf(mPostInfo.getLoveNum()).intValue();
                if (mPostInfo.isMyLove() && loveNum>0){
                    mPostInfo.setLoveNum(Integer.toString(loveNum-1));
                    mDatabaseUtil.deleteFav(mPostInfo.getPid(),DatabaseUtil.ATTR_LOVE);
                }else if(!mPostInfo.isMyLove()){
                    mPostInfo.setLoveNum(Integer.toString(loveNum+1));
                    mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_LOVE);
                }
                mPostInfo.setMyLove(!mPostInfo.isMyLove());
                setLCText();

            }
        });
        mCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostInfo.isMyCollection()){
                    mDatabaseUtil.deleteFav(mPostInfo.getPid(),DatabaseUtil.ATTR_COLLECTION);
                }else if(!mPostInfo.isMyCollection()){
                    mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_COLLECTION);
                }
                mPostInfo.setMyCollection(!mPostInfo.isMyCollection());
                setLCText();
            }
        });
    }

    /**
     * 获取帖子（post）的数据
     */
    private void getData(){
        mDatabaseUtil=DatabaseUtil.getInstance(DetailPostActivity.this);
        mPostInfo=new PostInfo();
        Intent intent=getIntent();
        mPostInfo.setPid(intent.getStringExtra("pid"));
        mPostInfo.setMyCollection(mDatabaseUtil.isCollected(mPostInfo.getPid()));
        mPostInfo.setMyLove(mDatabaseUtil.isLoved(mPostInfo.getPid()));
        handler=new GetDataHandler();
        mGetDataThread=new GetDataThread();
        mSwipeRefreshLayout.setRefreshing(true);
        mGetDataThread.start();
    }

    /**
     * 获取评论数据
     */
    private void getCommentData(){
        mDetailCommentRecyclerView= (RecyclerView) findViewById(R.id.detail_comment_recycler_view);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(DetailPostActivity.this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //mLayoutManager.setAutoMeasureEnabled(true);
        //给RecyclerView设置布局管理器
        mDetailCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentsInfoList=new ArrayList<CommentsInfo>();
        mCommentadapter=new CommentRecyclerAdapter(DetailPostActivity.this,mCommentsInfoList);
        mDetailCommentRecyclerView.setAdapter(mCommentadapter);
        if (mPostInfo.getPid().isEmpty()){
            // TODO: 2017/4/6 other oeration? 
            return;
        }
        mCommentHandler=new GetCommentHanlder();
        mGetCommentThread=new GetCommentThread();
        // TODO: 2017/4/6 refresh?
        mGetCommentThread.start();

    }

    class GetCommentThread extends Thread{
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.POST_ID_ATTR,mPostInfo.getPid());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.GET_COMMENT_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), GetCommentHanlder.FAILURE);
                    return;
                }
                JSONArray jsonArray=jsonObject.getJSONArray("comment");
                mCommentsInfoList.clear();
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jo= (JSONObject) jsonArray.get(i);
                    CommentsInfo commentsInfo=new CommentsInfo();
                    commentsInfo.setCcontent(jo.getString(Model.COMMENT_CONTENT));
                    commentsInfo.setCtime(jo.getString(Model.COMMENT_TIME));
                    commentsInfo.setUname(jo.getString(Model.COMMENT_UNAME));
                    mCommentsInfoList.add(commentsInfo);
                }
                showMessage(jsonObject.getString("mes"),GetCommentHanlder.SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(e.toString(),GetCommentHanlder.FAILURE);
            }
        }
        private void showMessage(String message,int status){
            Message msg=Message.obtain(mCommentHandler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(mCommentHandler);
        }
    }

    public class GetCommentHanlder extends Handler{
        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    CommentRecyclerAdapter adapter=new CommentRecyclerAdapter(DetailPostActivity.this,mCommentsInfoList);
                    mDetailCommentRecyclerView.setAdapter(adapter);
                    break;
                case FAILURE:
                    CommentRecyclerAdapter adapter1=new CommentRecyclerAdapter(DetailPostActivity.this,mCommentsInfoList);
                    mDetailCommentRecyclerView.setAdapter(adapter1);
                    break;
                default:
                    break;
            }
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
                mPostInfo.setUname(jsonObject.getString(Model.POST_UNAME_ATTR));
                mPostInfo.setPdaytime(jsonObject.getString(Model.POST_DAY_TIME_ATTR));
                mPostInfo.setPcontent(jsonObject.getString(Model.POST_CONTENT_ATTR));
                mPostInfo.setUheight(jsonObject.getString(Model.POST_UHEIGHT_ATTR));
                mPostInfo.setUweight(jsonObject.getString(Model.POST_UWEIGHT_ATTR));
                mPostInfo.setUsex(jsonObject.getString(Model.POST_USEX_ATTR));
                mPostInfo.setUage(jsonObject.getString(Model.POST_UAGE_ATTR));
                mPostInfo.setPid(jsonObject.getString(Model.POST_ID_ATTR));
                mPostInfo.setLoveNum(jsonObject.getString(Model.POST_LOVE_NUM));
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), GetDataHandler.SUCCESS);
                }else {
                    showMessage(jsonObject.getString("mes"), GetDataHandler.FAILURE);
                }
            } catch (JSONException e) {
                showMessage(e.toString(), GetDataHandler.FAILURE);
                e.printStackTrace();
            }


        }

        /**
         * 获取帖子数据的thread
         * @param message
         * @param status
         */
        private void showMessage(String message,int status){
            Message msg=Message.obtain(handler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    public class GetDataHandler extends Handler {

        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    mSwipeRefreshLayout.setRefreshing(false);
                    setPostInfoView();
                    setViewPager();
                    initPointer(); //获取数据后初始化小白点
                    mLoveButton.setText(mPostInfo.getLoveNum());
                    mSwipeRefreshLayout.setmViewPager(mImageViewPager);
                    mSwipeRefreshLayout.setmDetailScrollView(mDetailScrollView);
                    break;
                case FAILURE:
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(DetailPostActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    }

    public void sendComment(String content){
        mSendCommentHandler = new SendCommentHandler();
        mSendCommentThread=new SendCommentThread(content);
        mSendCommentThread.start();
    }

    class SendCommentThread extends Thread{
        private String cContent;
        public SendCommentThread(String content) {
            this.cContent=content;
        }

        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.COMMENT_CONTENT,cContent);
            params.put(Model.COMMENT_UID,Model.MYUSER.getUserid());
            params.put(Model.COMMENT_PID,mPostInfo.getPid());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.SEND_COMMENT_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), SendCommentHandler.SUCCESS);
                }else {
                    showMessage(jsonObject.getString("mes"), SendCommentHandler.FAILURE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(e.toString(),GetCommentHanlder.FAILURE);
            }
        }
        private void showMessage(String message,int status){
            Message msg=Message.obtain(mSendCommentHandler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(mSendCommentHandler);
        }
    }

    public class SendCommentHandler extends Handler{
        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
                    GetCommentThread getCommentThread=new GetCommentThread();
                    getCommentThread.start();
                    break;
                case FAILURE:
                    break;
                default:
                    break;
            }
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
