package com.example.clothshop.adapter;

import android.content.Context;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clothshop.R;

import java.util.List;

/**
 * Created by 一凡 on 2017/3/24.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mDatas;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView
    //已经到底了
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //上拉加载状态-默认为0
    private int load_more_status = 0;

    public RecyclerAdapter(Context mContext, List<String> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_recycler_refresh, null);
            return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.textView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_item_recycler_refresh);
        }
    }


    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }
}
