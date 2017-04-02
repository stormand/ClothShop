package com.example.clothshop.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.example.clothshop.Model.Model;
import com.example.clothshop.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by 一凡 on 2017/4/2.
 */

public class ImageLoader {
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView,String> imageViews= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    //线程池
    ExecutorService executorService;

    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService= Executors.newFixedThreadPool(5);
    }

    // TODO: 2017/4/2 change
    final int stub_id= R.drawable.add_image;

    public void DisplayImage(String url,ImageView imageView){
        imageViews.put(imageView,url);

        Bitmap bitmap=memoryCache.get(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            queuePhoto(url,imageView);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url,ImageView imageView){
        PhotoToLoad p=new PhotoToLoad(url,imageView);
        executorService.submit(new PhotosLoader(p));

    }

    private Bitmap getBitmap(String url){
        File f=fileCache.getFile(url);
        Bitmap b=decodeFile(f);
        if (b!=null){
            return b;
        }
        Bitmap bitmap=null;
        Map<String,String> params=new HashMap<String, String>();
        params.put(Model.IMAGE_ATTR,url);
        bitmap=HttpPostUtil.getImage(params,"utf-8",Model.POST_IMAGE_PATH);
        return bitmap;
    }

    /**
     * 按比例缩小以减少内存损耗。
     * @param f
     * @return
     */
    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o=new BitmapFactory.Options();
            o.inJustDecodeBounds=true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth,height_tmp=o.outHeight;
            int scale=1;
            while (true){
                if (width_tmp/2<REQUIRED_SIZE||height_tmp/2<REQUIRED_SIZE){
                    break;
                }
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            BitmapFactory.Options o2=new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f),null,o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 工作队列
     */
    private class PhotoToLoad{
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u,ImageView i){
            this.url=u;
            this.imageView=i;
        }
    }

    class PhotosLoader implements Runnable{
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        @Override
        public void run() {
            if (imageViewReused(photoToLoad)){
                return;
            }
            Bitmap bitmap=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url,bitmap);
            if (imageViewReused(photoToLoad)){
                return;
            }
            BitmapDisplayer bd=new BitmapDisplayer(bitmap,photoToLoad);
            Activity a= (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if (tag==null||!tag.equals(photoToLoad.url)){
            return true;
        }
        return false;
    }

    class BitmapDisplayer implements Runnable{
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b,PhotoToLoad p){
            this.bitmap=b;
            this.photoToLoad=p;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)){
                return;
            }
            if (bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
            }else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }
    public void clearCache(){
        memoryCache.clear();
        fileCache.clear();
    }

    public static void copyStream(InputStream is,OutputStream os){
        final int buffer_size=1024;
        try {
            byte[] bytes=new byte[buffer_size];
            while (true){
                int count=is.read(bytes,0,buffer_size);
                if (count==-1){
                    break;
                }
                os.write(bytes,0,count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
