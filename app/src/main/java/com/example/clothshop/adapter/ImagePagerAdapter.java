package com.example.clothshop.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.squareup.picasso.Picasso;

/**
 * Created by 一凡 on 2017/4/4.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private String[] mImageList;
    private Context mContext;

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
        StringBuilder sb=new StringBuilder();
        if (mImageList[position].length()>2){
            sb.append(Model.IMAGE_SAVE_PATH)
                    .append(mImageList[position].substring(2));
            String imageurl = sb.toString();
            Picasso.with(mContext).load(imageurl).placeholder(R.drawable.empty_image).error(R.drawable.error_image).resize(80, 80).into(imageView);
        }else {
            Picasso.with(mContext).load(R.drawable.empty_image).into(imageView);
            //aaa
        }


        //imageView.setImageResource(R.drawable.test);
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
