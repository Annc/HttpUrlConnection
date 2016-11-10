package com.example.dou.httpurlconnection;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button mButton_1;
    private Button mButton_2;
    private Button mButton_3;
    private Button mButton_4;
    private String mAddress ="http://cloud.bmob.cn/0906a62b462a3082/";
    private String upLoadfileUrl="http://www.maizitime.com:8081/upload_test";
    private String method = "getMemberBySex";
    private static final String ACTIVITY_TAG1="httpGet";
    private static final String ACTIVITY_TAG2="httpPost";
    private String filePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_1 = (Button)findViewById(R.id.btn_1);
        mButton_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGet("boy");

            }
        });

        mButton_2 = (Button)findViewById(R.id.btn_2);
        mButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost("boy");
            }
        });

        mButton_3 = (Button)findViewById(R.id.btn_3);
        mButton_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseFile();
            }

        });

        mButton_4 = (Button)findViewById(R.id.btn_4);
        mButton_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }

        });

    }
    private void Upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UploadUtil.UploadFile(new File(filePath), upLoadfileUrl);
            }
            }).start();
    }
    private void ChooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    filePath = uri.getPath();
                    Log.d("test",filePath);
                }
                //break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPost(final String s){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mAddress + method);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setUseCaches(false);
                    //两个简单的请求头的声明
                    httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    httpURLConnection.connect();

                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

                    String content = "sex=" + s;
                    dataOutputStream.writeBytes(content);
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    if(httpURLConnection.getResponseCode() == 200){
                        InputStream inputStream = httpURLConnection.getInputStream();
                        //读取一行
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuffer stringBuffer = new StringBuffer();

                        String readLine = "";
                        while ((readLine = bufferedReader.readLine()) != null){
                            stringBuffer.append(readLine);
                        }
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        Log.e(ACTIVITY_TAG2,stringBuffer.toString());
                    }else {
                        Log.e(ACTIVITY_TAG2,"fail");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void doGet(String s){
        final String getUrl = mAddress + method + "?sex=" + s;
        //开启异步线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.connect();

                    if(httpURLConnection.getResponseCode() == 200){
                        InputStream inputStream = httpURLConnection.getInputStream();
                        //读取一行
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuffer stringBuffer = new StringBuffer();

                        String readLine = "";
                        while ((readLine = bufferedReader.readLine()) != null){
                            stringBuffer.append(readLine);
                        }
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        Log.e(ACTIVITY_TAG1,stringBuffer.toString());
                    }else {
                        Log.e(ACTIVITY_TAG1,"fail");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
