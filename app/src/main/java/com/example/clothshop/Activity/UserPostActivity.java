package com.example.clothshop.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.Adapter.UserPostRecyclerAdapter;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPostActivity extends AppCompatActivity {

    private RecyclerView mRecyclerview;
    private static RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GetDataHandler handler=new GetDataHandler();
    private static List<PostInfo> mUserPostDataList=null;
    private int lastVisibleItem;
    private boolean refreshLock=false;
    private static DeleteHandler deleteHandler=new DeleteHandler();
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
        type=getIntent().getStringExtra("type");
        initRecyclerView();
    }
    private void initRecyclerView(){
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.user_post_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDataThread topPullThread=new GetDataThread(GetDataHandler.TOP_PULL,Model.MYUSER.getUserid());
                topPullThread.start();
            }
        });

        mRecyclerview= (RecyclerView) findViewById(R.id.user_post_recycler_view);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(UserPostActivity.this);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        //给RecyclerView设置布局管理器
        mRecyclerview.setLayoutManager(mLayoutManager);

        mUserPostDataList=new ArrayList<PostInfo>();
        mAdapter = new UserPostRecyclerAdapter(UserPostActivity.this,mUserPostDataList,type);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.addItemDecoration(new SpaceItemDecoration());
        //创建适配器，并且设置
        if (mUserPostDataList.isEmpty()){
            mSwipeRefreshLayout.setRefreshing(true);
            GetDataThread newPageThread=new GetDataThread(GetDataHandler.TOP_PULL,Model.MYUSER.getUserid());
            newPageThread.start();
        }

        mRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem=0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mLayoutManager instanceof LinearLayoutManager) {
                    //获取最后一个可见view的位置
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    GetDataThread getDataThread=new GetDataThread(GetDataHandler.BOTTOM_PULL,Model.MYUSER.getUserid(),mUserPostDataList.get(lastVisibleItem).getPid());
                    getDataThread.start();
                    handler.sendEmptyMessageDelayed(0, 3000);
                }
            }
        });
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        int space= Model.LISTMARGIN;
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space*3/2;
            outRect.left=space;
            outRect.right=space;
        }
    }

    class GetDataThread extends Thread{

        private int dataType;
        private String uid;
        private String pid;

        GetDataThread(int type,String uid){
            this.dataType=type;
            this.uid=uid;

        }

        GetDataThread(int type,String uid,String pid){
            this.dataType=type;
            this.uid=uid;
            this.pid=pid;
        }

        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String,String>();
            params.put(Model.POST_UID_ATTR,uid);
            params.put("type", type);
            if (pid!=null){
                params.put(Model.POST_UID_ATTR,pid);
            }
            String result= HttpPostUtil.sendPostMessage(params,"utf-8",Model.USER_POST_PATH);
            getData(result);
        }

        private void getData(String result){
            List<PostInfo> paramsList=new ArrayList<PostInfo>();
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("post");
                for (int i=0;i<jsonArray.length();i++){
                    PostInfo postInfo=new PostInfo();
                    JSONObject jo= (JSONObject) jsonArray.get(i);
                    postInfo.setPtitle(jo.getString(Model.POST_TITLE_ATTR));
                    postInfo.setPid(jo.getString(Model.POST_ID_ATTR));
                    postInfo.setPdaytime(jo.getString(Model.POST_DAY_TIME_ATTR));
                    postInfo.setLoveNum(jo.getString(Model.POST_LOVE_NUM));
                    postInfo.setPimage(jo.getString(Model.POST_IMAGE_ATTR).split("@")[0]);
                    paramsList.add(postInfo);
                }
                Message msg=Message.obtain(handler,dataType);
                msg.obj=paramsList;
                //点击新tab的涟漪效果会提前结束，如果不延迟发送
                if (dataType== GetDataHandler.NEW_PAGE){
                    handler.sendMessageDelayed(msg,300);
                }else {
                    msg.sendToTarget();
                }

            } catch (JSONException e) {
                Message msg=Message.obtain(handler, GetDataHandler.ERROR);
                msg.obj=e.toString();
                msg.sendToTarget();
                e.printStackTrace();
            }
        }
    }

    class GetDataHandler extends Handler {

        public static final int NEW_PAGE=0x0004;
        public static final int TOP_PULL=0x0001;
        public static final int BOTTOM_PULL=0x0002;
        public static final int ERROR=0x0003;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NEW_PAGE:
                case TOP_PULL:
                    mUserPostDataList.clear();
                    mUserPostDataList.addAll((ArrayList<PostInfo>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BOTTOM_PULL:
                    mUserPostDataList.addAll((ArrayList<PostInfo>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                    Toast.makeText(UserPostActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public static class DeleteThread extends Thread{
        String pid;
        String type;
        int position;
        Context mContext;
        public DeleteThread(String pid,String type,int position,Context context) {
            this.pid=pid;
            this.type=type;
            this.position=position;
            this.mContext=context;
        }

        @Override
        public void run() {
//            SharedPreferences sp=getSharedPreferences(Model.SP_NAME_PASSWD,MODE_PRIVATE);
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.COMMENT_UID,Model.MYUSER.getUserid());
            params.put(Model.COMMENT_PID,pid);
            params.put("type",type);
//            params.put(Model.USER_PASSWORD_ATTR,sp.getString(Model.USER_PASSWORD_ATTR,"error"));
            String result= HttpPostUtil.sendPostMessage(params,"utf-8",Model.DELETE_POST_PATH); //// TODO: 2017/4/22 delete post
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    if (type.equals("user_collection")){
                        DatabaseUtil databaseUtil=DatabaseUtil.getInstance(mContext);
                        databaseUtil.deleteFav(pid,DatabaseUtil.ATTR_COLLECTION);
                    }
                    Message msg=Message.obtain(deleteHandler, deleteHandler.SUCCESS);
                    msg.arg1=position;
                    msg.obj=type;
                    msg.sendToTarget();
                }else {
                    Message msg=Message.obtain(deleteHandler, deleteHandler.ERROR);
                    msg.sendToTarget();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("delete error",e.toString());
                Message msg=Message.obtain(deleteHandler, deleteHandler.ERROR);
                msg.sendToTarget();
            }
        }
    }

    static class DeleteHandler extends Handler{
        public static final int SUCCESS=0x0004;
        public static final int ERROR=0x0003;


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
                    mUserPostDataList.remove(msg.arg1);
                    mAdapter.notifyItemRemoved(msg.arg1);

                    break;
                case ERROR:

                    break;
                default:
                    break;
            }

        }
    }
}
