package com.example.clothshop.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.clothshop.Activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.UUID;

import static android.provider.ContactsContract.CommonDataKinds.StructuredName.PREFIX;

/**
 * Created by 一凡 on 2017/3/21.
 */

public class HttpPostUtil {

    //// TODO: 2017/3/21 这是什么？
    public static String[] sessionId = null;
    public static String urlTemp;
    public HttpURLConnection urlConnection;

    static String BOUNDARY = java.util.UUID.randomUUID().toString();
    static String PREFIX = "--", LINEND = "\r\n";
    static String MULTIPART_FROM_DATA = "multipart/form-data";
    static String CHARSET = "UTF-8";

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
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }
        return "";
    }


    public static String sendPostMessageWithImg(Map<String, String> params, String encode, String url_path,File[] pictureFile){
        URL url;

        try {
            url = new URL(url_path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setReadTimeout(10 * 1000); // 缓存的最长时间
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);


            DataOutputStream os =  new DataOutputStream(conn.getOutputStream());

             //用StringBuilder拼接报文，用于上传图片数据
            // TODO: 2017/4/1 for 循环，多张照片的name？
            for(int i=0;i<pictureFile.length;i++){
                StringBuilder sb = new StringBuilder();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"picture[]\"; filename=\"" + pictureFile[i].getName() + "\"" + LINEND);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb.append(LINEND);
                os.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(pictureFile[i]);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len); //写入图片数据
                }
                is.close();
                os.write(LINEND.getBytes());
            }


            StringBuilder text = new StringBuilder();
            for(Map.Entry<String,String> entry : params.entrySet()) { //在for循环中拼接报文，上传文本数据
                text.append(PREFIX);
                text.append(BOUNDARY);
                text.append(LINEND);
                text.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
                text.append(entry.getValue());
                text.append(LINEND);
            }
            os.write(text.toString().getBytes("utf-8")); //写入文本数据

            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            os.write(end_data);
            os.flush();
            os.close();

            // 得到响应码
            int res = conn.getResponseCode();
            if (res == 200) {
                return changeInputStream(conn.getInputStream(), encode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
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
                Log.e("net_result",result);
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
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getImage(Map<String, String> params, String encode, String url_path){
        URL url;
        StringBuffer buffer = new StringBuffer();
        Bitmap bitmap=null;

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
            Log.e("post_params",params.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
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
                urlConnection.connect();
                InputStream is=urlConnection.getInputStream();
                //解析为图片
                //changeInputStream(is,"utf-8");
//                String result=changeInputStream(is,"utf-8");
//                Log.e("image_post_getImage_re",result);
//                JSONObject jsonObject=new JSONObject(result);
//                Log.e("image_post_getImage",jsonObject.toString());
//                String st=jsonObject.getString("image");
//                bitmap = BitmapFactory.decodeByteArray(Base64.decode(st, Base64.DEFAULT), 0,
//                                Base64.decode(st, Base64.DEFAULT).length);
                bitmap=BitmapFactory.decodeStream(is);
                is.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
