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

    private SendHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        handler=new SendHandler();
        mPubEditText= (EditText) findViewById(R.id.pub_tittle);
        mPubTittle= (EditText) findViewById(R.id.pub_edit_text);

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
            HttpPostUtil httpPostUtil=new HttpPostUtil();
            Map<String,String> params=new HashMap<String, String>();
            params.put(Model.TITLE,mPubTittle.getText().toString());
            params.put(Model.CONTENT,mPubEditText.getText().toString());
            params.put(Model.UID,"1");
            String result=HttpPostUtil.sendPostMessage(params,"utf-8",Model.PUBLISH_PATH);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("status").equals("0")){

                    Intent intent=new Intent();
                    intent.setClass(PublishActivity.this,MainActivity.class);
                    startActivity(intent);
                    showMessage(jsonObject.getString("mes"));
                    finish();
                }else {
                    showMessage(jsonObject.getString("mes"));
                }
            } catch (JSONException e) {
                showMessage(e.toString());
                e.printStackTrace();
            }

        }

        private void showMessage(String message){
            Message msg=Message.obtain(handler,SendHandler.MESSAGE);
            msg.obj=message;
            msg.sendToTarget();
            msg.setTarget(handler);
        }
    }

    class SendHandler extends Handler{

        public static final int MESSAGE=0x0001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==MESSAGE){
                Toast.makeText(PublishActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
