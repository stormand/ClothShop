package com.example.clothshop.adapter;

import android.content.Context;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.textView.setText(mDatas.get(position).getPtitle());
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
            lp.width=mImageWidth;
            lp.height=mImageWidth;
            imageView.setLayoutParams(lp);
        }
    }


}
