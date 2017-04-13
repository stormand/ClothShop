package com.example.clothshop.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clothshop.Activity.PublishActivity;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.adapter.RecyclerAdapter;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerview;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static List<PostInfo> mHomeList=null;
    private int lastVisibleItem;
    private boolean refreshLock=false;

    private GetDataHandler handler=new GetDataHandler();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initRefreshLayout(view);
        initFab(view);
        initRecyclerView(view);
        return view;
    }

    private void initRefreshLayout(View view){
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.home_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDataThread topPullThread=new GetDataThread(GetDataHandler.TOP_PULL);
                topPullThread.start();
            }
        });
    }
    private void initFab(View view){
        mFab= (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.ISLOGIN){
                    Intent intent=new Intent(getActivity(),PublishActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initRecyclerView(View view){
        mRecyclerview= (RecyclerView) view.findViewById(R.id.recycler_view);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(getContext());
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        //给RecyclerView设置布局管理器
        mRecyclerview.setLayoutManager(mLayoutManager);

        mHomeList=new ArrayList<PostInfo>();
        mAdapter = new RecyclerAdapter(getContext(),mHomeList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.addItemDecoration(new SpaceItemDecoration());
        //创建适配器，并且设置
        if (mHomeList.isEmpty()){
            mSwipeRefreshLayout.setRefreshing(true);
            GetDataThread newPageThread=new GetDataThread(GetDataHandler.TOP_PULL);
            newPageThread.start();
        }

        mRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (mLayoutManager instanceof LinearLayoutManager) {
                    //获取最后一个可见view的位置
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                }
                if (dy > 0 && mFab.isShown()) mFab.hide();
                if (dy < 0 && !mFab.isShown()) mFab.show();

            }



            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE
//                        && lastVisibleItem + 1 == mAdapter.getItemCount()
//                        && !refreshLock) {
//                    mSwipeRefreshLayout.setRefreshing(true);
//                    //实际项目中这里一般是用网络请求获取数据
//                    refreshLock=true;
//                    //为了有刷新的效果，延迟关闭刷新效果
////                    mSwipeRefreshLayout.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            stringList.add("上啦");
////                            mSwipeRefreshLayout.setRefreshing(false);
////                            mAdapter.notifyDataSetChanged();
////                            refreshLock=false;
////                        }
////                    }, 4000);
//
//                }
            }
        });
    }

    public void refresh(){
        if (mLayoutManager.findFirstCompletelyVisibleItemPosition()!=0){
            mRecyclerview.smoothScrollToPosition(0);
        }else {
            mSwipeRefreshLayout.setRefreshing(true);
            GetDataThread topPullThread=new GetDataThread(GetDataHandler.TOP_PULL);
            topPullThread.start();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class GetDataThread extends Thread{

        private int dataType;

        GetDataThread(int type){
            this.dataType=type;
        }

        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String,String>();
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.HOME_PATH);
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
                    postInfo.setUname(jo.getString(Model.USER_NAME_ATTR));
                    postInfo.setPdaytime(jo.getString(Model.POST_DAY_TIME_ATTR));
                    postInfo.setLoveNum(jo.getString(Model.POST_LOVE_NUM));
                    postInfo.setPimage(jo.getString(Model.POST_IMAGE_ATTR).split("@")[0]);
                    paramsList.add(postInfo);
                }

                Message msg=Message.obtain(handler,dataType);
                msg.obj=paramsList;
                msg.sendToTarget();


            } catch (JSONException e) {
                Message msg=Message.obtain(handler,GetDataHandler.ERROR);
                msg.obj=e.toString();
                msg.sendToTarget();
                e.printStackTrace();
            }
        }
    }

    class GetDataHandler extends Handler{

        public static final int TOP_PULL=0x0001;
        public static final int BOTTOM_PULL=0x0002;
        public static final int ERROR=0x0003;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TOP_PULL:
                    mHomeList.clear();
                    mHomeList.addAll((ArrayList<PostInfo>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BOTTOM_PULL:
                    List<PostInfo> list=new ArrayList<PostInfo>();
                    list.addAll(mHomeList);
                    mHomeList.clear();
                    mHomeList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        int space=Model.LISTMARGIN;
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space*3/2;
            outRect.left=space;
            outRect.right=space;
        }
    }
}
