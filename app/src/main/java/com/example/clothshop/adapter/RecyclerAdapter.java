package com.example.clothshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.clothshop.utils.HttpPostUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页homeFragment 的 recyclerview 的adapter
 * Created by 一凡 on 2017/3/24.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<PostInfo> mDatas;
    private int mImageWidth;
    private DatabaseUtil mDatabaseUtil;


    public RecyclerAdapter(Context mContext, List<PostInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mImageWidth= Model.SCREEMWIDTH-2*Model.LISTMARGIN;
        mDatabaseUtil=DatabaseUtil.getInstance(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_recycler_refresh,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.titleTextView.setText(mDatas.get(position).getPtitle());
        itemHolder.userNameTextView.setText(mDatas.get(position).getUname());

        //读取图片路径，用picasso显示图片
        String imageUrl=mDatas.get(position).getPimage();
        StringBuilder sb=new StringBuilder();
        if (imageUrl.length()>2){ //去掉前面的 ..
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(imageUrl.substring(2));
            imageUrl = sb.toString();
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.empty_image).error(R.drawable.error_image).resize(800,800).into(itemHolder.imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(itemHolder.imageView);
        }
        itemHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, DetailPostActivity.class);
                intent.putExtra("pid",mDatas.get(position).getPid());
                mContext.startActivity(intent);
            }
        });
        //点赞和收藏
        mDatas.get(position).setMyCollection(mDatabaseUtil.isCollected(mDatas.get(position).getPid()));
        mDatas.get(position).setMyLove(mDatabaseUtil.isLoved(mDatas.get(position).getPid()));
        itemHolder.loveButton.setText(mDatas.get(position).getLoveNum());
        itemHolder.loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loveNum=Integer.valueOf(mDatas.get(position).getLoveNum()).intValue();
                if (mDatas.get(position).isMyLove() && loveNum>0){
                    mDatas.get(position).setLoveNum(Integer.toString(loveNum-1));
                    mDatas.get(position).setMyLove(!mDatas.get(position).isMyLove());
                    mDatabaseUtil.deleteFav(mDatas.get(position).getPid(), DatabaseUtil.ATTR_LOVE);
                }else if(!mDatas.get(position).isMyLove()){
                    mDatas.get(position).setLoveNum(Integer.toString(loveNum+1));
                    mDatas.get(position).setMyLove(!mDatas.get(position).isMyLove());
                    mDatabaseUtil.insertFav(mDatas.get(position), DatabaseUtil.ATTR_LOVE);
                }
                itemHolder.loveButton.setText(mDatas.get(position).getLoveNum());
                AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_LOVE,mDatas.get(position));
                mAddLCThread.start();
            }
        });
        itemHolder.collectionButton.setText(mDatas.get(position).isMyCollection()?mContext.getString(R.string.collected):mContext.getString(R.string.collection));
        itemHolder.collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatas.get(position).isMyCollection()){
                    mDatas.get(position).setMyCollection(false);
                    mDatabaseUtil.deleteFav(mDatas.get(position).getPid(),DatabaseUtil.ATTR_COLLECTION);
                }else if(!mDatas.get(position).isMyCollection()){
                    mDatas.get(position).setMyCollection(true);
                    mDatabaseUtil.insertFav(mDatas.get(position), DatabaseUtil.ATTR_COLLECTION);
                }
                itemHolder.collectionButton.setText(mDatas.get(position).isMyCollection()?mContext.getString(R.string.collected):mContext.getString(R.string.collection));
                AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_COLLECTION,mDatas.get(position));
                mAddLCThread.start();
            }
        });

    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView userNameTextView;
        public ImageView userAvatar;
        public Button loveButton;
        public Button collectionButton;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.home_list_image_item);
            titleTextView = (TextView) itemView.findViewById(R.id.home_list_title_item);
            userNameTextView= (TextView) itemView.findViewById(R.id.home_list_user_name);
            userAvatar= (ImageView) itemView.findViewById(R.id.home_list_avatar);
            loveButton= (Button) itemView.findViewById(R.id.home_list_love_button);
            collectionButton= (Button) itemView.findViewById(R.id.home_list_collect_button);
        }
    }

    class AddLCThread extends Thread{
        String attribute;
        PostInfo mPostInfo;
        public AddLCThread(String attr,PostInfo postInfo) {
            this.attribute=attr;
            this.mPostInfo=postInfo;
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
                result= HttpPostUtil.sendPostMessage(params,"utf-8",Model.ADD_LOVE_PATH);
            }else if (attribute.equals(DatabaseUtil.ATTR_COLLECTION)){
                params.put(Model.COLLECTION, String.valueOf(mPostInfo.isMyCollection()?1:0));
                result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.ADD_COLLECTION_PATH);
            }
//            if (result.equals("")){ //为空返回
//                showMessage("",GetCommentHanlder.FAILURE);
//                return;
//            }
//            try {
//                JSONObject jsonObject=new JSONObject(result);
//                if (jsonObject.getString("status").equals("0")){
//                    showMessage(jsonObject.getString("mes"), DetailPostActivity.AddLCHandler.SUCCESS);
//                }else {
//                    showMessage(jsonObject.getString("mes"), DetailPostActivity.AddLCHandler.FAILURE);
//                }


        }
    }

}
