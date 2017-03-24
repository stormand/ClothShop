package com.example.clothshop.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by 一凡 on 2017/3/21.
 */

public class HttpPostUtil {

    //// TODO: 2017/3/21 这是什么？
    public static String[] sessionId = null;

    public static String PATH = "http://122.112.247.7:8080/Order/Main/";
    public static String IMAGE_PATH = PATH + "Image.php";
    public static String REGISTER_PATH = PATH + "Register.php";
    public static String LOGIN_PATH = PATH + "Login.php";
    public static String PUBLISH_PATH = PATH + "SaveMessage.php";
    public static String MENU_PATH = PATH + "ShowMenu.php";
    public static String ORDER_PATH = PATH + "AddOrder.php";
    public static String urlTemp;

    public HttpURLConnection urlConnection;

    /**
     * @param params
     * @param encode
     * @param url_path
     * @return
     */
    public static String sendPostMessage(Map<String, String> params, String encode, String url_path){
        URL url;
        StringBuffer buffer = new StringBuffer();


        try {
            url = new URL(url_path);

            //转码操作
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                }
                buffer.deleteCharAt(buffer.length() - 1);
                // TODO: 2017/3/21 为什么删去最后一个？
            }
            System.out.println("->" + buffer.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            if (sessionId != null) {
                urlConnection.setRequestProperty("cookie", sessionId[0]);
            }
            //获得上传信息的字节大小和长度
            byte[] mydata = buffer.toString().getBytes();
            //请求体类型是文本类型
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(mydata.length));
            //获得输出流，向服务器输出数据
            OutputStream outputStream=urlConnection.getOutputStream();
            outputStream.write(mydata,0,mydata.length);
            outputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                return changeInputStream(urlConnection.getInputStream(), encode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将一个输入流转换为制定编码的字符串
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream, String encode) {
        ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result="";
        if (inputStream !=null){
            try {
                while((len=inputStream.read(data))!=-1){
                    outPutStream.write(data,0,len);
                }
                result=new String(outPutStream.toByteArray(),encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取图片
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try {
            myFileURL=new URL(url);
            //获得连接
            HttpURLConnection conn= (HttpURLConnection) myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);

            String session_value=conn.getHeaderField("Set-Cookie");
            sessionId=session_value.split(";");

            conn.connect();
            //得到数据流

            InputStream is=conn.getInputStream();
            //解析为图片
            //changeInputStream(is,"utf-8");
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
