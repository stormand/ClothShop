package com.example.clothshop.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clothshop.Activity.DetailPostActivity;
import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

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
                    R.layout.item_home_recycler,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.titleTextView.setText(mDatas.get(position).getPtitle());
        itemHolder.userNameTextView.setText(mDatas.get(position).getUname());
        itemHolder.userAvatar.setImageDrawable(mDatas.get(position).getUavatar());
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

        if (mDatas.get(position).isMyLove()){
            itemHolder.loveButton.setChecked(true);
            itemHolder.loveTextView.setText("已赞 "+mDatas.get(position).getLoveNum());
        }else {
            itemHolder.loveButton.setChecked(false);
            itemHolder.loveTextView.setText("赞 "+mDatas.get(position).getLoveNum());
        }
        if (mDatas.get(position).isMyCollection()){
            itemHolder.collectionButton.setChecked(true);
            itemHolder.collectTextView.setText("已收藏");
        }else {
            itemHolder.collectionButton.setChecked(false);
            itemHolder.collectTextView.setText("收藏");
        }
        //点击监听
        itemHolder.loveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loveClick(position,itemHolder);
            }
        });
        itemHolder.collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionClick(position,itemHolder);
            }
        });
        //shineButton 的监听 不监听会出现点击button，只有动画效果
        itemHolder.loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loveClick(position,itemHolder);
            }
        });
        itemHolder.collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionClick(position,itemHolder);
            }
        });

    }

    private void loveClick(int position,ItemHolder itemHolder){
        int loveNum=Integer.valueOf(mDatas.get(position).getLoveNum()).intValue();
        if (mDatas.get(position).isMyLove() && loveNum>0){
            mDatas.get(position).setLoveNum(Integer.toString(loveNum-1));
            mDatas.get(position).setMyLove(!mDatas.get(position).isMyLove());
            itemHolder.loveButton.setChecked(false,true);
            itemHolder.loveTextView.setText("赞 "+mDatas.get(position).getLoveNum());
            mDatabaseUtil.deleteFav(mDatas.get(position).getPid(), DatabaseUtil.ATTR_LOVE);
        }else if(!mDatas.get(position).isMyLove()){
            mDatas.get(position).setLoveNum(Integer.toString(loveNum+1));
            mDatas.get(position).setMyLove(!mDatas.get(position).isMyLove());
            mDatabaseUtil.insertFav(mDatas.get(position), DatabaseUtil.ATTR_LOVE);
            itemHolder.loveButton.setChecked(true,true);
            itemHolder.loveTextView.setText("已赞 "+mDatas.get(position).getLoveNum());
        }
        AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_LOVE,mDatas.get(position));
        mAddLCThread.start();
    }

    private void collectionClick(int position,ItemHolder itemHolder){
        if (mDatas.get(position).isMyCollection()){
            mDatas.get(position).setMyCollection(false);
            itemHolder.collectionButton.setChecked(false,true);
            itemHolder.collectTextView.setText("收藏");
            mDatabaseUtil.deleteFav(mDatas.get(position).getPid(),DatabaseUtil.ATTR_COLLECTION);
        }else if(!mDatas.get(position).isMyCollection()){
            mDatas.get(position).setMyCollection(true);
            itemHolder.collectionButton.setChecked(true,true);
            itemHolder.collectTextView.setText("已收藏");
            mDatabaseUtil.insertFav(mDatas.get(position), DatabaseUtil.ATTR_COLLECTION);
        }
        //itemHolder.collectionButton.setText(mDatas.get(position).isMyCollection()?mContext.getString(R.string.collected):mContext.getString(R.string.collection));
        AddLCThread mAddLCThread=new AddLCThread(DatabaseUtil.ATTR_COLLECTION,mDatas.get(position));
        mAddLCThread.start();
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
        public LinearLayout loveLayout;
        public LinearLayout collectLayout;
        public ShineButton loveButton;
        public ShineButton collectionButton;
        public TextView loveTextView;
        public TextView collectTextView;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.home_list_image_item);
            titleTextView = (TextView) itemView.findViewById(R.id.home_list_title_item);
            userNameTextView= (TextView) itemView.findViewById(R.id.home_list_user_name);
            userAvatar= (ImageView) itemView.findViewById(R.id.home_list_avatar);
            loveButton= (ShineButton) itemView.findViewById(R.id.home_list_love_button);
            loveButton.init((Activity) mContext);
            collectionButton= (ShineButton) itemView.findViewById(R.id.home_list_collect_button);
            collectionButton.init((Activity) mContext);
            loveTextView= (TextView) itemView.findViewById(R.id.home_list_love_text_view);
            collectTextView= (TextView) itemView.findViewById(R.id.home_list_collect_text_view);
            loveLayout= (LinearLayout) itemView.findViewById(R.id.home_list_love_layout);
            collectLayout= (LinearLayout) itemView.findViewById(R.id.home_list_collect_layout);
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
