package com.example.clothshop.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Info.UserInfo;
import com.example.clothshop.Model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 一凡 on 2017/4/10.
 */

public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";
    public static final String ATTR_COLLECTION = "iscollection"; //收藏
    public static final String ATTR_LOVE = "islove";               //点赞
    private static DatabaseUtil instance;

    /**
     * 数据库帮助类
     **/
    private DBHelper dbHelper;

    public synchronized static DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseUtil(context);
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    private DatabaseUtil(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 销毁
     */
    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        instance = null;
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }


    public void deleteFav(String pid,String attribute) {
        boolean deleteLove=attribute==ATTR_LOVE ? true : false;
        boolean deleteCollection=attribute==ATTR_COLLECTION ? true : false;
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + pid + "'";
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int loved = cursor.getInt(cursor.getColumnIndex(ATTR_LOVE));
            int collected = cursor.getInt(cursor.getColumnIndex(ATTR_COLLECTION));
            if (deleteLove && loved==1){
                if (collected==0){
                    dbHelper.delete(DBHelper.TABLE_NAME, where, null); //既不收藏也不点赞，删除
                    Log.e("love_test","delete all "+loved+"  "+collected);
                }else {
                    ContentValues cv = new ContentValues(); //只更改点赞
                    cv.put(DBHelper.FavTable.IS_LOVE, 0);
                    dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
                    Log.e("love_test","delete love "+loved+"  "+collected);
                }
            }else if (deleteCollection && collected==1){
                if (loved==0){
                    dbHelper.delete(DBHelper.TABLE_NAME, where, null);  //既不收藏也不点赞，删除
                }else {
                    ContentValues cv = new ContentValues();  //只更改收藏
                    cv.put(DBHelper.FavTable.IS_COLLECTION, 0);
                    dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
    }


    public boolean isLoved(String pid) {
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + pid + "'";
        Log.i(TAG,"------------------------------------"+where);
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
                Log.i(TAG,"------------------------------------"+"到这里了");
                return true;
            }
        }
        return false;
    }

    public boolean isCollected(String pid) {
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + pid + "'";
        Log.i(TAG,"------------------------------------"+where);
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_COLLECTION)) == 1) {
                Log.i(TAG,"------------------------------------"+"到这里了");
                return true;
            }
        }
        return false;
    }

    public boolean insertFav(PostInfo postInfo,String attribute) {
        long uri = 0;
        Cursor cursor = null;
        String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
                + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + postInfo.getPid() + "'";
        cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues conv = new ContentValues();
            if (attribute.equals(ATTR_COLLECTION)){
                conv.put(DBHelper.FavTable.IS_COLLECTION, 1);
            }else if(attribute.equals(ATTR_LOVE)){
                conv.put(DBHelper.FavTable.IS_LOVE, 1);
                Log.e("love_test","add love 1 "+postInfo.isMyLove());
            }
            dbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.FavTable.USER_ID, Model.MYUSER.getUserid());
            cv.put(DBHelper.FavTable.OBJECT_ID, postInfo.getPid());
            cv.put(DBHelper.FavTable.IS_LOVE, postInfo.isMyLove() == true ? 1 : 0);
            cv.put(DBHelper.FavTable.IS_COLLECTION, postInfo.isMyCollection() == true ? 1 : 0);
            Log.e("love_test","add both "+postInfo.isMyLove()+" "+postInfo.isMyCollection());
            uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }

        if (uri!=0){
            Log.i(TAG,"insertFav-->"+uri);
            return true;
        }else {
            return false;
        }

    }

    /**
     * 设置内容的收藏状态
     *
     * @param
     * @param lists
     */
    public List<PostInfo> setFav(List<PostInfo> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                PostInfo content = (PostInfo) iterator.next();
                String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
                        + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getPid() + "'";
                cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_COLLECTION)) == 1) {
                        content.setMyCollection(true);
                    } else {
                        content.setMyCollection(false);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
                        content.setMyLove(true);
                    } else {
                        content.setMyLove(false);
                    }
                }
            }
        }
        if (cursor != null) {
            cursor.close();
            dbHelper.close();
        }
        return lists;
    }

    /**
     * 设置内容的收藏状态
     *
     * @param
 //    * @param lists
     */
//    public List<PostInfo> setFavInFav(List<PostInfo> lists) {
//        Cursor cursor = null;
//        if (lists != null && lists.size() > 0) {
//            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
//                PostInfo content = (PostInfo) iterator.next();
//                content.setMyCollection(true);
//                String where = DBHelper.FavTable.USER_ID + " = '" + Model.MYUSER.getUserid()
//                        + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getPid() + "'";
//                cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1) {
//                        content.setMyLove(true);
//                    } else {
//                        content.setMyLove(false);
//                    }
//                }
//            }
//        }
//        if (cursor != null) {
//            cursor.close();
//            dbHelper.close();
//        }
//        return lists;
//    }


    public ArrayList<PostInfo> queryFav(String attribute) {
        ArrayList<PostInfo> contents = null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        contents = new ArrayList<PostInfo>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            PostInfo content = new PostInfo();
            UserInfo user=new UserInfo();
            content.setMyCollection(cursor.getInt(3) == 1 ? true : false);
            content.setMyLove(cursor.getInt(4) == 1 ? true : false);

            String id=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.OBJECT_ID));
            String nameId=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.USER_ID));
            String fav=cursor.getString(cursor.getColumnIndex(attribute));

            if (fav.equals("1")){//被收藏了

                Log.i("queryFav","idddd----->"+id);
                Log.i("queryFav", "String---->" + nameId);
                Log.i("queryFav", "Fav------->" + fav);//1或0

                content.setUid(nameId);
                content.setPid(id);
                contents.add(content);
            }
            for (int i=0;i<contents.size();i++){
                Log.i("queryFav",i+1+"--->"+contents.get(i).getPid());
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return contents;
    }

    public ArrayList<PostInfo> query() {
        ArrayList<PostInfo> contents = null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        contents = new ArrayList<PostInfo>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            PostInfo content = new PostInfo();
            UserInfo user=new UserInfo();
            content.setMyCollection(cursor.getInt(3) == 1 ? true : false);
            content.setMyLove(cursor.getInt(4) == 1 ? true : false);

            String id=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.OBJECT_ID));
            String nameId=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.USER_ID));
            String col=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.IS_COLLECTION));
            String lo=cursor.getString(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE));

                content.setUid(nameId);
                content.setPid(id);
                content.setMyCollection(col.equals("1")?true:false);
                content.setMyLove(lo.equals("1")?true:false);

                contents.add(content);

            for (int i=0;i<contents.size();i++){
                Log.i("queryFav",i+1+"--->"+contents.get(i).getPid());
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return contents;
    }

}
