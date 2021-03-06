package com.example.clothshop.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothshop.Activity.Refactor.DetailRefreshLayout;
import com.example.clothshop.Activity.Refactor.DetailScrollView;
import com.example.clothshop.Activity.Refactor.FullyLinearLayoutManager;
import com.example.clothshop.Activity.Refactor.RecyclerViewDivider;
import com.example.clothshop.Adapter.RecyclerAdapter;
import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.CommentsInfo;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.Adapter.CommentRecyclerAdapter;
import com.example.clothshop.Adapter.ImagePagerAdapter;
import com.example.clothshop.utils.HttpPostUtil;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DetailPostActivity extends AppCompatActivity implements DetailScrollView.OnScrollChangedListener{

    private ViewPager mImageViewPager;
    private DetailScrollView mDetailScrollView;
    private String[] imageArray;
    private ImageView imageView;
    private ImageView[] imageViews;
    private PostInfo mPostInfo;
    private ArrayList<CommentsInfo> mCommentsInfoList;

    private DetailRefreshLayout mSwipeRefreshLayout;

    //包裹点点的LinearLayout
    private ViewGroup mPointGroup;
    private ImagePagerAdapter mImagePagerAdapter;

    private ImageView mAuthorAvatar;
    private TextView mDetailTitle;
    private TextView mDetailContent;
    private TextView mDetailUname;
    private TextView mDetailUage;
    private TextView mDetailUweight;
    private TextView mDetailUheight;
    private TextView mDetailDateTime;
    private TextView mDetailUsex;
    private ShineButton mLoveButton;
    private ShineButton mCollectionButton;
    private TextView mLoveTextView;
    private TextView mCollectTextView;
    private LinearLayout mLoveLayout;
    private LinearLayout mCollectLayout;
    private EditText mCommentEditText;
    private Button mCommentSendButton;
    private DatabaseUtil mDatabaseUtil;

    private Toolbar mToolBar;

    private RecyclerView mDetailCommentRecyclerView;
    private GetDataHandler handler;
    private GetDataThread mGetDataThread;
    private GetCommentHanlder mCommentHandler;
    private CommentRecyclerAdapter mCommentadapter;
    private FullyLinearLayoutManager mLayoutManager;
    private SendCommentHandler mSendCommentHandler;
    private AddLCHandler mAddLCHandler;
    private Button linkButton;

    private ArrayList<String> thingName=new ArrayList<String>();//用于物品名称数组
    private ArrayList<String> linkName=new ArrayList<String>();//用于链接名称数组ng
    private String[] all;//一组的物品名称与url的对应。
    private String[] tem;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        initToolbar();
        getData();
        initLayout();
        getCommentData();
        loveAndCollect();
        initSendComment();
        linkButton=(Button)findViewById(R.id.link_button);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test4();
            }
        });
    }
    public void test4() {

        thingName.clear();
        linkName.clear();
        int key=0;
        if(mPostInfo.getLink()==null || mPostInfo.getLink().equals("")){
            Toast.makeText(DetailPostActivity.this,"没有链接",Toast.LENGTH_LONG).show();
        }
        else {
            all = mPostInfo.getLink().split("  ");
            Log.e("link",mPostInfo.getLink());
            for (int i = 0; i < all.length; i++) {
                tem = all[i].split(" ");
                thingName.add(tem[0]);
                linkName.add(tem[1]);
            }

        //初始化一个界面
            String[] sthingName = (String[]) thingName.toArray(new String[0]);
        new AlertDialog.Builder(this).setTitle("衣物直达界面")
                .setItems(sthingName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 第一个参数 dialog int which 那个条目
                        Intent fIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkName.get(which)));
                        fIntent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                        startActivity(fIntent);
                    }
                }).show();
        }
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
        //toolbar
        mToolBar = (Toolbar) findViewById(R.id.detail_post_toolbar);
        mToolBar.setTitle(getResources().getString(R.string.detail_post_title));
        mToolBar.setAlpha(0);  //先设置透明
        setSupportActionBar(mToolBar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            //设为 false
//           actionBar.setDisplayShowTitleEnabled(false);  //是否隐藏标题
            actionBar.setDisplayHomeAsUpEnabled(true);     //是否显示返回按钮
        }
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initLayout(){
        //viewPager
        mImageViewPager= (ViewPager) findViewById(R.id.detail_view_pager);
        imageArray =new String[]{};
        mImagePagerAdapter=new ImagePagerAdapter(imageArray,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mPointGroup=(ViewGroup)findViewById(R.id.detail_point_view_Group);
        mAuthorAvatar= (ImageView) findViewById(R.id.detail_post_author_avatar);

        mDetailTitle=(TextView) findViewById(R.id.detail_title);
        mDetailContent=(TextView) findViewById(R.id.detail_content);
        mDetailUname=(TextView) findViewById(R.id.detail_uname);
        mDetailUage=(TextView) findViewById(R.id.detail_uage);
        mDetailUweight=(TextView) findViewById(R.id.detail_uweight);
        mDetailUheight=(TextView) findViewById(R.id.detail_uheight);
        mDetailDateTime=(TextView) findViewById(R.id.detail_date_time);
        mDetailUsex=(TextView)findViewById(R.id.detail_usex);
        mLoveButton=(ShineButton) findViewById(R.id.love_button);
        mCollectionButton=(ShineButton) findViewById(R.id.collect_button);
        mLoveTextView= (TextView) findViewById(R.id.love_text_view);
        mCollectTextView= (TextView) findViewById(R.id.collect_text_view);
        mLoveLayout= (LinearLayout) findViewById(R.id.love_layout);
        mCollectLayout= (LinearLayout) findViewById(R.id.collect_layout);
        mDetailScrollView=(DetailScrollView) findViewById(R.id.detail_scroll_view);
        mDetailScrollView.setmViewPager(mImageViewPager);
        mDetailScrollView.setOnScrollChangedListener(this);
    }

    private void setPostInfoView(){
        mDetailTitle.setText(mPostInfo.getPtitle());
        mDetailContent.setText(mPostInfo.getPcontent());
        mDetailUweight.setText(getResources().getString(R.string.edit_weight)+mPostInfo.getUweight()+"kg");
        mDetailUheight.setText(getResources().getString(R.string.edit_height)+mPostInfo.getUheight()+"cm");
        mDetailDateTime.setText(mPostInfo.getPdaytime());
        mDetailUage.setText(getResources().getString(R.string.edit_age)+mPostInfo.getUage());
        mDetailUsex.setText(getResources().getString(R.string.edit_sex)+mPostInfo.getUsex());
        mDetailUname.setText(mPostInfo.getUname());
    }
    private void setViewPager(){
        imageArray = mPostInfo.getPimage().split("@");
        mImagePagerAdapter=new ImagePagerAdapter(imageArray,DetailPostActivity.this);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImageViewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    /**
     * 初始化导航小白点，根据getData的图片数目设置小白点数目
     */
    private void initPointer() {
        //有多少个界面就new多长的数组
        mPointGroup.removeAllViews();
        imageViews = new ImageView[imageArray.length];
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
    private void setLoveAndCollection(){
        if (mPostInfo.isMyLove()){
            mLoveButton.setChecked(true);
            mLoveTextView.setText("已赞 "+mPostInfo.getLoveNum());
        }else {
            mLoveButton.setChecked(false);
            mLoveTextView.setText("赞 "+mPostInfo.getLoveNum());
        }
        if (mPostInfo.isMyCollection()){
            mCollectionButton.setChecked(true);
            mCollectTextView.setText("已收藏");
        }else {
            mCollectionButton.setChecked(false);
            mCollectTextView.setText("收藏");
        }
        //点击监听
        mLoveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loveClick();
            }
        });
        mCollectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionClick();
            }
        });
        //shineButton 的监听 不监听会出现点击button，只有动画效果
        mLoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loveClick();
            }
        });
        mCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionClick();
            }
        });
    }

    private void loveClick(){
        int loveNum=Integer.valueOf(mPostInfo.getLoveNum()).intValue();
        if (mPostInfo.isMyLove() && loveNum>0){
            mPostInfo.setLoveNum(Integer.toString(loveNum-1));
            mPostInfo.setMyLove(!mPostInfo.isMyLove());
            mLoveButton.setChecked(false,true);
            mLoveTextView.setText("赞 "+mPostInfo.getLoveNum());
            mDatabaseUtil.deleteFav(mPostInfo.getPid(), DatabaseUtil.ATTR_LOVE);
        }else if(!mPostInfo.isMyLove()){
            mPostInfo.setLoveNum(Integer.toString(loveNum+1));
            mPostInfo.setMyLove(!mPostInfo.isMyLove());
            mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_LOVE);
            mLoveButton.setChecked(true,true);
            mLoveTextView.setText("已赞 "+mPostInfo.getLoveNum());
        }
        AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_LOVE);
        mAddLCThread.start();
    }

    private void collectionClick(){
        if (mPostInfo.isMyCollection()){
            mPostInfo.setMyCollection(false);
            mCollectionButton.setChecked(false,true);
            mCollectTextView.setText("收藏");
            mDatabaseUtil.deleteFav(mPostInfo.getPid(),DatabaseUtil.ATTR_COLLECTION);
        }else if(!mPostInfo.isMyCollection()){
            mPostInfo.setMyCollection(true);
            mCollectionButton.setChecked(true,true);
            mCollectTextView.setText("已收藏");
            mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_COLLECTION);
        }
        //itemHolder.collectionButton.setText(mDatas.get(position).isMyCollection()?mContext.getString(R.string.collected):mContext.getString(R.string.collection));
        AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_COLLECTION);
        mAddLCThread.start();
    }
    /*
     * 获取帖子（post）的数据
     */
    private void getData(){
        mDatabaseUtil=DatabaseUtil.getInstance(DetailPostActivity.this);
        mPostInfo=new PostInfo();
        Intent intent=getIntent();
        mPostInfo.setPid(intent.getStringExtra("pid"));
        mPostInfo.setMyCollection(mDatabaseUtil.isCollected(mPostInfo.getPid()));
        mPostInfo.setMyLove(mDatabaseUtil.isLoved(mPostInfo.getPid()));
        Log.e("love_test","postinfo:"+mPostInfo.isMyLove()+"  "+mPostInfo.isMyCollection());
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
        mLayoutManager = new FullyLinearLayoutManager(DetailPostActivity.this);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //mLayoutManager.setAutoMeasureEnabled(true);
        //给RecyclerView设置布局管理器
        mDetailCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentsInfoList=new ArrayList<CommentsInfo>();
        mCommentadapter=new CommentRecyclerAdapter(DetailPostActivity.this,mCommentsInfoList);
        mDetailCommentRecyclerView.setAdapter(mCommentadapter);
        mDetailCommentRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        if (mPostInfo.getPid().isEmpty()){
            // TODO: 2017/4/6 other oeration? 
            return;
        }
        mCommentHandler=new GetCommentHanlder();
        GetCommentThread mGetCommentThread=new GetCommentThread();
        // TODO: 2017/4/6 refresh?
        mGetCommentThread.start();

    }

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
        float height = mImageViewPager.getHeight();  //获取图片的高度
        if (oldt < height){
            float f=Float.valueOf(oldt/height).floatValue();
            mToolBar.setAlpha(Math.max(2*f,0));   // 0~255 透明度
        }else {
            mToolBar.setAlpha(1);
        }
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
                if (jsonObject.getString("status").equals("1")){
                    showMessage(jsonObject.getString("mes"), GetCommentHanlder.FAILURE);
                    return;
                }
                JSONArray jsonArray=jsonObject.getJSONArray("comment");
                mCommentsInfoList.clear();
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jo= (JSONObject)jsonArray.get(i);
                    CommentsInfo commentsInfo=new CommentsInfo();
                    commentsInfo.setCcontent(jo.getString(Model.COMMENT_CONTENT));
                    commentsInfo.setCtime(jo.getString(Model.COMMENT_TIME));
                    commentsInfo.setUname(jo.getString(Model.COMMENT_UNAME));
                    Drawable draw1;
                    if (jo.getString(Model.POST_USEX_ATTR)==null || jo.getString(Model.POST_USEX_ATTR).isEmpty()){
                        draw1 = getResources().getDrawable(R.drawable.avatar);
                    }else if (jo.getString(Model.POST_USEX_ATTR).equals("男")){
                        draw1 = getResources().getDrawable(R.drawable.avatar_male);
                    }else if (jo.getString(Model.POST_USEX_ATTR).equals("女")){
                        draw1 = getResources().getDrawable(R.drawable.avatar_female);
                    }else {
                        draw1 = getResources().getDrawable(R.drawable.avatar);
                    }
                    commentsInfo.setUavatar(draw1);
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
                    mCommentadapter.notifyItemInserted(mCommentsInfoList.size()-1);
                    //LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) mDetailCommentRecyclerView.getLayoutParams();
                    //lp.height= RecyclerView.LayoutParams.WRAP_CONTENT;
                    //mDetailCommentRecyclerView.setLayoutParams(lp);
                    break;
                case FAILURE:
                    mCommentadapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取帖子数据
     */
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
                mPostInfo.setLink(jsonObject.getString(Model.POST_LINK_ATTR));
                Drawable draw1;
                if (mPostInfo.getUsex()==null || mPostInfo.getUsex().isEmpty()){
                    draw1 = getResources().getDrawable(R.drawable.avatar);
                }else if (mPostInfo.getUsex().equals("男")){
                    draw1 = getResources().getDrawable(R.drawable.avatar_male);
                }else if (mPostInfo.getUsex().equals("女")){
                    draw1 = getResources().getDrawable(R.drawable.avatar_female);
                }else {
                    draw1 = getResources().getDrawable(R.drawable.avatar);
                }
                mPostInfo.setUavatar(draw1);
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
                    if (mPostInfo.isMyLove()){
                        mLoveTextView.setText("已赞 "+mPostInfo.getLoveNum());
                    }else {
                        mLoveTextView.setText("赞 "+mPostInfo.getLoveNum());
                    }

                    mAuthorAvatar.setImageDrawable(mPostInfo.getUavatar());
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
    /**
     * 发送评论
     */
    public void initSendComment(){
        mSendCommentHandler = new SendCommentHandler();
        mCommentEditText= (EditText) findViewById(R.id.comment_edit_text);
        mCommentSendButton= (Button) findViewById(R.id.send_comment_button);
        mCommentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendCommentThread sendCommentThread=new SendCommentThread(mCommentEditText.getText().toString());
                sendCommentThread.start();
            }
        });
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
    /**
     * LC:love & collection
     */
    private void loveAndCollect(){
        setLoveAndCollection();
        mAddLCHandler=new AddLCHandler();
        mLoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //local
                int loveNum=Integer.valueOf(mPostInfo.getLoveNum()).intValue();
                if (mPostInfo.isMyLove() && loveNum>0){
                    mPostInfo.setLoveNum(Integer.toString(loveNum-1));
                    mPostInfo.setMyLove(!mPostInfo.isMyLove());
                    mDatabaseUtil.deleteFav(mPostInfo.getPid(),DatabaseUtil.ATTR_LOVE);
                }else if(!mPostInfo.isMyLove()){
                    mPostInfo.setLoveNum(Integer.toString(loveNum+1));
                    mPostInfo.setMyLove(!mPostInfo.isMyLove());
                    mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_LOVE);
                }
                setLoveAndCollection();
                AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_LOVE);
                mAddLCThread.start();
            }
        });
        mCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostInfo.isMyCollection()){
                    mPostInfo.setMyCollection(!mPostInfo.isMyCollection());
                    mDatabaseUtil.deleteFav(mPostInfo.getPid(),DatabaseUtil.ATTR_COLLECTION);
                }else if(!mPostInfo.isMyCollection()){
                    mPostInfo.setMyCollection(!mPostInfo.isMyCollection());
                    mDatabaseUtil.insertFav(mPostInfo, DatabaseUtil.ATTR_COLLECTION);
                }
                setLoveAndCollection();
                AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_COLLECTION);
                mAddLCThread.start();
            }
        });
    }

    class AddLCThread extends Thread{
        String attribute;
        public AddLCThread(String attr) {
            this.attribute=attr;
        }
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.COMMENT_UID,Model.MYUSER.getUserid());
            params.put(Model.COMMENT_PID,mPostInfo.getPid());
            String result="";
            if (attribute.equals(DatabaseUtil.ATTR_LOVE)){
                params.put(Model.LOVE, String.valueOf(mPostInfo.isMyLove()?1:0));
                result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.ADD_LOVE_PATH);
            }else if (attribute.equals(DatabaseUtil.ATTR_COLLECTION)){
                params.put(Model.COLLECTION, String.valueOf(mPostInfo.isMyCollection()?1:0));
                result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.ADD_COLLECTION_PATH);
            }
            Log.e("sendLC",params.toString());
//            if (result.equals("")){ //为空返回
//                showMessage("",GetCommentHanlder.FAILURE);
//                return;
//            }
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"), AddLCHandler.SUCCESS);
                }else {
                    showMessage(jsonObject.getString("mes"), AddLCHandler.FAILURE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(e.toString(),GetCommentHanlder.FAILURE);
            }
        }
        private void showMessage(String message,int status){
            Message msg=Message.obtain(mAddLCHandler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(mAddLCHandler);
        }
    }

    public class AddLCHandler extends Handler{
        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(DetailPostActivity.this, "success", Toast.LENGTH_SHORT).show();
                    break;
                case FAILURE:
                    Toast.makeText(DetailPostActivity.this, "failure", Toast.LENGTH_SHORT).show();
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
