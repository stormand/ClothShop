package com.example.clothshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clothshop.Activity.DetailPostActivity;
import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 主页homeFragment 的 recyclerview 的adapter
 * Created by 一凡 on 2017/3/24.
 */

public class UserPostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<PostInfo> mDatas;
    private DatabaseUtil mDatabaseUtil;


    public UserPostRecyclerAdapter(Context mContext, List<PostInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mDatabaseUtil=DatabaseUtil.getInstance(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_user_post_recycler,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.titleTextView.setText(mDatas.get(position).getPtitle());
        itemHolder.timeTextView.setText(mDatas.get(position).getPdaytime());
        itemHolder.loveNumTextView.setText(mDatas.get(position).getLoveNum());
        //读取图片路径
        String imageUrl=mDatas.get(position).getPimage();
        StringBuilder sb=new StringBuilder();
        if (imageUrl.length()>2){ //去掉前面的 ..
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(imageUrl.substring(2));
            imageUrl = sb.toString();
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.empty_image).error(R.drawable.error_image).resize(400,400).into(itemHolder.imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(itemHolder.imageView);
        }



    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView timeTextView;
        public TextView loveNumTextView;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.user_post_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.user_post_title);
            timeTextView= (TextView) itemView.findViewById(R.id.user_post_time);
            loveNumTextView= (TextView) itemView.findViewById(R.id.user_post_love_num);
        }
    }

}
