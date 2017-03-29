package com.example.clothshop.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clothshop.Info.PostInfo;
import com.example.clothshop.Model.Model;
import com.example.clothshop.R;
import com.example.clothshop.utils.HttpPostUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    private EditText mPubTittle;
    private EditText mPubEditText;
    private EditText mPubSexEditText;
    private EditText mPubAgeEditText;
    private EditText mPubHeightEditText;
    private EditText mPubWeightEditText;

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
