package com.example.clothshop.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clothshop.Activity.MainActivity;
import com.example.clothshop.Activity.PublishActivity;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.adapter.HomeFragmentAdapter;
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
 * {@link HomeAllFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeAllFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAllFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private RecyclerView mRecyclerview;
    private static List<List<PostInfo>> mHomeList= new ArrayList<List<PostInfo>>();;

    private ViewPager viewPager;
    private List<View> views=new ArrayList<>();
    private List<LinearLayoutManager> mLayoutManager=new ArrayList<LinearLayoutManager>();
    private List<RecyclerAdapter> mAdapter= new ArrayList<RecyclerAdapter>();
    private List<SwipeRefreshLayout> mSwipeRefreshLayout =new ArrayList<SwipeRefreshLayout>();

    private FloatingActionButton mFab;

    private GetDataHandler handler;

    private int pageSelect=0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeAllFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeAllFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAllFragment newInstance(String param1, String param2) {
        HomeAllFragment fragment = new HomeAllFragment();
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
        View view = inflater.inflate(R.layout.fragment_home_all, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.home_all_toolbar);
        //toolbar.inflateMenu(R.menu.menu_message);
        mViewPager = (ViewPager) view.findViewById(R.id.home_all_viewpager);
        handler=new GetDataHandler();
        initViewPager(view,0);
        initFab(view);
        return view;
    }

    private void initViewPager(View view, final int page) {

        mTabLayout = (TabLayout) view.findViewById(R.id.home_all_tabs);
        List<String> titles = new ArrayList<>();
        titles.add("全部"); // TODO: 2017/4/13 改成资源引用
        titles.add("身材");
        for(int i=0;i<titles.size();i++){
            View myView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home,null);
            mRecyclerview= (RecyclerView) myView.findViewById(R.id.recycler_view);

            List<PostInfo> data=new ArrayList<PostInfo>();
            mHomeList.add(data);
            RecyclerAdapter adapter=new RecyclerAdapter(getContext(),mHomeList.get(i));
            mAdapter.add(adapter);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mLayoutManager.add(manager);
            mRecyclerview.setLayoutManager(mLayoutManager.get(i));
            mRecyclerview.setAdapter(mAdapter.get(i));
            mRecyclerview.addItemDecoration(new SpaceItemDecoration());
            mRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int lastVisibleItem=0;
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (mLayoutManager.get(pageSelect) instanceof LinearLayoutManager) {
                        //获取最后一个可见view的位置
                        lastVisibleItem = mLayoutManager.get(pageSelect).findLastVisibleItemPosition();
                        Log.e("lastVisibleItem_scroll", Integer.toString(lastVisibleItem)+"  "+mAdapter.get(pageSelect).getItemCount());
                    }
                    if (dy > 0 && mFab.isShown()) mFab.hide();
                    if (dy < 0 && !mFab.isShown()) mFab.show();
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItem + 1 == mAdapter.get(pageSelect).getItemCount()) {
                        mSwipeRefreshLayout.get(pageSelect).setRefreshing(true);
                        GetDataThread getDataThread=new GetDataThread(GetDataHandler.BOTTOM_PULL,pageSelect,mHomeList.get(pageSelect).get(lastVisibleItem).getPid());
                        getDataThread.start();
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                }
            });
            myView.setTag(mRecyclerview);
            views.add(myView);

            //add swiprefreshlayout
            SwipeRefreshLayout swipeRefreshLayout= (SwipeRefreshLayout) myView.findViewById(R.id.home_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    GetDataThread topPullThread=new GetDataThread(GetDataHandler.TOP_PULL,pageSelect);
                    topPullThread.start();
                }
            });
            mSwipeRefreshLayout.add(swipeRefreshLayout);
        }
        if (mHomeList.get(0).isEmpty()){
            mSwipeRefreshLayout.get(0).setRefreshing(true);
            GetDataThread newPageThread=new GetDataThread(GetDataHandler.TOP_PULL,0);
            newPageThread.start();
        }
        viewPager= (ViewPager) view.findViewById(R.id.home_all_viewpager);
        HomeFragmentAdapter myPagerAdapter=new HomeFragmentAdapter(views,titles);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOnPageChangeListener(new viewPageListener());

        TabLayout tabLayout= (TabLayout)view.findViewById(R.id.home_all_tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void refreshViewPager(int page){
        GetDataThread newPageThread=new GetDataThread(GetDataHandler.TOP_PULL,page);
        newPageThread.start();
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

    /**
     * mainactiity中多次点击导航调用此刷新函数
     */
    public void refresh(){
        if (mLayoutManager.get(pageSelect).findFirstCompletelyVisibleItemPosition()!=0){
            mRecyclerview.smoothScrollToPosition(0);
        }else {
            mSwipeRefreshLayout.get(pageSelect).setRefreshing(true);
            GetDataThread topPullThread=new GetDataThread(GetDataHandler.TOP_PULL,pageSelect);
            topPullThread.start();
        }
    }

    class viewPageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            pageSelect=position;
            refreshViewPager(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class GetDataThread extends Thread{

        private int dataType;
        private int page;
        private String pid=null;

        GetDataThread(int type,int page,String pid){
            this.dataType=type;
            this.page=page;
            this.pid=pid;
        }

        GetDataThread(int type,int page){
            this.dataType=type;
            this.page=page;
        }

        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String,String>();
            if (pid!=null){
                params.put(Model.POST_ID_ATTR,pid);
            }
            String result= HttpPostUtil.sendPostMessage(params,"utf-8",Model.HOME_PATH);
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
                msg.arg1=page;
                msg.obj=paramsList;
                msg.sendToTarget();


            } catch (JSONException e) {
                Message msg=Message.obtain(handler,GetDataHandler.ERROR);
                msg.arg1=page;
                msg.obj=e.toString();
                msg.sendToTarget();
                e.printStackTrace();
            }
        }
    }

    class GetDataHandler extends Handler {

        public static final int TOP_PULL=0x0001;
        public static final int BOTTOM_PULL=0x0002;
        public static final int ERROR=0x0003;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TOP_PULL:
                    mHomeList.get(msg.arg1).clear();
                    mHomeList.get(msg.arg1).addAll((ArrayList<PostInfo>) msg.obj);
                    mAdapter.get(msg.arg1).notifyDataSetChanged();
                    break;
                case BOTTOM_PULL:
                    mHomeList.get(msg.arg1).addAll((ArrayList<PostInfo>) msg.obj);
                    mAdapter.get(msg.arg1).notifyDataSetChanged();
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
            mSwipeRefreshLayout.get(msg.arg1).setRefreshing(false);
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

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        int space=Model.LISTMARGIN;
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = space*3/2;
            outRect.left=space;
            outRect.right=space;
        }
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
}
