package com.example.clothshop.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clothshop.Activity.DetailPostActivity;
import com.example.clothshop.Activity.UserPostActivity;
import com.example.clothshop.DB.DatabaseUtil;
import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * 主页homeFragment 的 recyclerview 的adapter
 * Created by 一凡 on 2017/3/24.
 */

public class UserPostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<PostInfo> mDatas;
    private DatabaseUtil mDatabaseUtil;
    private String type;


    public UserPostRecyclerAdapter(Context mContext, List<PostInfo> mDatas,String type) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mDatabaseUtil=DatabaseUtil.getInstance(mContext);
        this.type=type;

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
        itemHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(position);
            }
        });
        //读取图片路径
        String imageUrl=mDatas.get(position).getPimage();
        StringBuilder sb=new StringBuilder();
        if (imageUrl.length()>2){ //去掉前面的 ..
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(imageUrl.substring(2));
            imageUrl = sb.toString();
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.load_image).error(R.drawable.error_image).resize(400,400).into(itemHolder.imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(itemHolder.imageView);
        }
        itemHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,DetailPostActivity.class);
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
        public TextView titleTextView;
        public ImageView imageView;
        public TextView timeTextView;
        public TextView loveNumTextView;
        public ConstraintLayout constraintLayout;
        public Button deleteButton;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.user_post_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.user_post_title);
            timeTextView= (TextView) itemView.findViewById(R.id.user_post_time);
            loveNumTextView= (TextView) itemView.findViewById(R.id.user_post_love_num);
            deleteButton= (Button) itemView.findViewById(R.id.user_post_delete_button);
            constraintLayout= (ConstraintLayout) itemView.findViewById(R.id.user_post_layout);
        }
    }

    private void deleteDialog(final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认删除?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){ //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserPostActivity.DeleteThread deleteThread=new UserPostActivity.DeleteThread(mDatas.get(position).getPid(),type,position,mContext);
                deleteThread.start();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

}
