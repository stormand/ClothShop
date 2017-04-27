package com.example.clothshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clothshop.Activity.ImageActivity;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.squareup.picasso.Picasso;

/**
 * Created by 一凡 on 2017/4/4.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private String[] mImageArray;
    private Context mContext;

    public ImagePagerAdapter(String[] imageArray,Context context) {
        this.mImageArray = imageArray;
        this.mContext=context;
    }

    @Override
    public int getCount() {//必须实现
        return mImageArray.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {//必须实现，实例化

        View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_pager_image, container, false);
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.item_detail_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ImageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArray("imageArray",mImageArray);
                bundle.putInt("position",position);
                intent.putExtra("showImage",bundle);
                mContext.startActivity(intent);

            }
        });
        StringBuilder sb=new StringBuilder();
        if (mImageArray[position].length()>2){
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(mImageArray[position].substring(2));
            String imageurl = sb.toString();
            Picasso.with(mContext).load(imageurl).placeholder(R.drawable.load_image).error(R.drawable.error_image).resize(800, 800).into(imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(imageView);
        }

        ((ViewPager) container).addView(imageLayout,0);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
