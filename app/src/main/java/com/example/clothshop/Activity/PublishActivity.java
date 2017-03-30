package com.example.clothshop.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;

import net.yazeed44.imagepicker.util.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    private EditText mPubTittle;
    private EditText mPubEditText;
    private EditText mPubSexEditText;
    private EditText mPubAgeEditText;
    private EditText mPubHeightEditText;
    private EditText mPubWeightEditText;

    private GridView mImageGridView;              //网格显示缩略图
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;
    private int mImageWidth;
    private HashMap<String, Object> mAddIconItem;

    private SendHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        handler=new SendHandler();
        mPubEditText= (EditText) findViewById(R.id.pub_edit_text);
        mPubTittle= (EditText) findViewById(R.id.pub_tittle);
        mPubSexEditText= (EditText) findViewById(R.id.pub_sex_edit_text);
        mPubSexEditText.setText(Model.MYUSER.getUsex());
        mPubAgeEditText= (EditText) findViewById(R.id.pub_age_edit_text);
        mPubAgeEditText.setText(Model.MYUSER.getUage());
        mPubHeightEditText= (EditText) findViewById(R.id.pub_height_edit_text);
        mPubHeightEditText.setText(Model.MYUSER.getUheight());
        mPubWeightEditText= (EditText) findViewById(R.id.pub_weight_edit_text);
        mPubWeightEditText.setText(Model.MYUSER.getUweight());
        mImageWidth=Model.SCREEMWIDTH/5;
        initImageGrid();

    }

    private void initImageGrid(){
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.add_image);
        mImageGridView= (GridView) findViewById(R.id.image_grid_view);
        imageItem = new ArrayList<HashMap<String, Object>>();
        mAddIconItem = new HashMap<String, Object>();
        mAddIconItem.put("itemImage", bmp);
        imageItem.add(mAddIconItem);

        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.item_grid_add_image,
                new String[] { "itemImage"}, new int[] { R.id.add_image_view_item});

        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    int width=Model.SCREEMWIDTH/5;
                    final ViewGroup.LayoutParams lp=i.getLayoutParams();
                    lp.height=mImageWidth;
                    lp.width=mImageWidth;
                    i.setLayoutParams(lp);
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        mImageGridView.setAdapter(simpleAdapter);

                /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        mImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if(position == imageItem.size()-1) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(PublishActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    //You can change many settings in builder like limit , Pick mode and colors
                    new Picker.Builder(PublishActivity.this,new MyPickListener()).build().startActivity();
                    //通过onResume()刷新数据
                }
                else {
                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第"+(position + 1)+" 号图片",
                    //      Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyPickListener implements Picker.PickListener
    {
        @Override
        public void onPickedSuccessfully(String[] paths) {
            Bitmap addbmp=null;
            imageItem.remove(imageItem.size()-1);
        for (int i=0;i<paths.length;i++){
            addbmp=BitmapFactory.decodeFile(paths[i]);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
        }
            imageItem.add(mAddIconItem);
            simpleAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancel(){
        }
    }

    /*
 * Dialog对话框提示用户删除操作
 * position为删除图片位置
 */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.publish_actionbar,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                // TODO: 2017/3/24 send thread
                SendThread sendThread=new SendThread();
                sendThread.start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SendThread extends Thread{
        @Override
        public void run() {
            super.run();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.TITLE_ATTR,mPubTittle.getText().toString());
            params.put(Model.CONTENT_ATTR,mPubEditText.getText().toString());
            params.put(Model.UID_ATTR,Model.MYUSER.getUserid());
            params.put(Model.UAGE_ATTR,mPubAgeEditText.getText().toString());
            params.put(Model.USEX_ATTR,mPubSexEditText.getText().toString());
            params.put(Model.UHEIGHT_ATTR,mPubHeightEditText.getText().toString());
            params.put(Model.UWEIGHT_ATTR,mPubWeightEditText.getText().toString());
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.PUBLISH_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){
                    showMessage(jsonObject.getString("mes"),SendHandler.SUCCESS);
                    finish();
                }else {
                    showMessage(jsonObject.getString("mes"),SendHandler.FAILURE);
                }
            } catch (JSONException e) {
                showMessage(e.toString(),SendHandler.FAILURE);
                e.printStackTrace();
            }

        }

        private void showMessage(String message,int status){
            Message msg=Message.obtain(handler,status);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class SendHandler extends Handler{

        public static final int FAILURE=0x0002;
        public static final int SUCCESS=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(PublishActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    PublishActivity.this.finish();
                    break;
                case FAILURE:
                    Toast.makeText(PublishActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
