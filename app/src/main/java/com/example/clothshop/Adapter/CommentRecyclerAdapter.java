package com.example.clothshop.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clothshop.Activity.DetailPostActivity;
import com.example.clothshop.Info.CommentsInfo;
import com.example.clothshop.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by 一凡 on 2017/4/6.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter {
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView

    private DetailPostActivity mContext;
    private List<CommentsInfo> mDatas;
    WeakReference<Activity> weak;


    public CommentRecyclerAdapter(DetailPostActivity context,List<CommentsInfo> datas) {
        this.mContext=context;
        this.mDatas=datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_comment_recycler,parent,false);
            return new CommentRecyclerAdapter.ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mCommentContent.setText(mDatas.get(position).getCcontent());
            itemHolder.mCommentDateTime.setText(mDatas.get(position).getCtime());
            itemHolder.mCommentUname.setText(mDatas.get(position).getUname());
            itemHolder.mCommentUavatar.setImageDrawable(mDatas.get(position).getUavatar());
    }


    @Override
    public int getItemCount() {
            return mDatas.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public TextView mCommentUname;
        public TextView mCommentDateTime;
        public TextView mCommentContent;
        public ImageView mCommentUavatar;

        public ItemHolder(View itemView) {
            super(itemView);
            mCommentUavatar= (ImageView) itemView.findViewById(R.id.comment_uavatar);
            mCommentUname = (TextView) itemView.findViewById(R.id.comment_uname);
            mCommentDateTime= (TextView) itemView.findViewById(R.id.comment_date_time);
            mCommentContent= (TextView) itemView.findViewById(R.id.comment_content);
        }
    }
}
