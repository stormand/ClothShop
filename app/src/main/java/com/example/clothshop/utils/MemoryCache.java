package com.example.clothshop.utils;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 一凡 on 2017/4/2.
 */

public class MemoryCache {
    private static final String TAG="MemoryCache";
    private Map<String,Bitmap> cache= Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>());
    private long size=0;
    private long limit=1000000;

    public MemoryCache(){
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }

    public void setLimit(long new_limit){
        limit=new_limit;
    }

    public Bitmap get(String id){
            if (!cache.containsKey(id)){
                return null;
            }
            return cache.get(id);
    }
    public void put(String id,Bitmap bitmap){
        if (cache.containsKey(id)){
            size-=getSizeInBytes(bitmap);
            checkSize();
        }
    }

    private void checkSize(){
        if (size>limit){
            Iterator<Map.Entry<String,Bitmap>>iterator=cache.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,Bitmap>entry=iterator.next();
                size-=getSizeInBytes(entry.getValue());
                iterator.remove();
                if (size<=limit){
                    break;
                }
            }
        }
    }

    private long getSizeInBytes(Bitmap bitmap){
        if (bitmap==null){
            return 0;
        }
        return bitmap.getRowBytes()*bitmap.getHeight();
    }

    public void clear(){
        cache.clear();
    }
}
