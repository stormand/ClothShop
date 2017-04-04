package com.example.clothshop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 一凡 on 2017/4/4.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private String[] mImageList;
    private Context mContext;
    private ImageLoader imageLoader;

    public ImagePagerAdapter(String[] imageList,Context context) {
        this.mImageList = imageList;
        this.mContext=context;
    }

    @Override
    public int getCount() {//必须实现
        return mImageList.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {//必须实现，实例化

        View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_pager_image, container, false);
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.item_detail_image);
        imageLoader=new ImageLoader(mContext);
        imageLoader.DisplayImage(mImageList[position],imageView);
        imageView.setImageResource(R.drawable.test);
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
