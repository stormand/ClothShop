package com.example.clothshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clothshop.Activity.DetailPostActivity;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 一凡 on 2017/3/24.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<PostInfo> mDatas;
    private int mImageWidth;


    public RecyclerAdapter(Context mContext, List<PostInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mImageWidth= Model.SCREEMWIDTH-2*Model.LISTMARGIN;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_recycler_refresh, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.textView.setText(mDatas.get(position).getPtitle());

        String imageUrl=mDatas.get(position).getPimage();
        StringBuilder sb=new StringBuilder();
        if (imageUrl.length()>2){
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(imageUrl.substring(2));
            imageUrl = sb.toString();
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.empty_image).error(R.drawable.error_image).resize(200,200).into(itemHolder.imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(itemHolder.imageView);
        }

            //imageLoader.DisplayImage(imageUrl,itemHolder.imageView);
        // TODO: 2017/4/3 不用new？ 
        itemHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, DetailPostActivity.class);
                intent.putExtra("pid",mDatas.get(position).getPid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.home_list_image_item);
            textView = (TextView) itemView.findViewById(R.id.home_list_title_item);
            ViewGroup.LayoutParams lp=imageView.getLayoutParams();
            lp.width= mImageWidth;
            lp.height=mImageWidth;
            imageView.setLayoutParams(lp);
        }
    }

}
