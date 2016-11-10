package com.example.dou.httpurlconnection;

import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Dou on 2016/5/5.
 */
public class UploadUtil {
    private static final int TIME_OUT = 10*1000;
    private static final String CHARSET = "UTF-8";
    public static int UploadFile(File file,String requestUrl){
        int res = 0;

        String BUNDER = UUID.randomUUID().toString();
        String PERFIX = "--";
        String LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(TIME_OUT);
            httpURLConnection.setConnectTimeout(TIME_OUT);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", CHARSET);
            httpURLConnection.setRequestProperty("Connection","Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE+";boundary="+BUNDER);

            if(file != null){
                Log.d("test", "start file");
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PERFIX);
                sb.append(BUNDER);
                sb.append(LINE_END);

                sb.append("Content-Disposition: form-data; name=\"file\"; filename= \""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; Charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);

                dataOutputStream.write(sb.toString().getBytes());

                InputStream inputStream = new FileInputStream(file);
                //FileInputStream inputStream = new FileInputStream("\\storage\\sdcard0\\DCIM\\test.txt");
                Log.d("test",""+ inputStream);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes)) != -1){
                    dataOutputStream.write(bytes,0,len);
                    Log.d("test","upload= "+ len);
                }
                inputStream.close();

                dataOutputStream.write(LINE_END.getBytes());
                String end = PERFIX + BUNDER + PERFIX + LINE_END;

                dataOutputStream.write(end.getBytes());
                dataOutputStream.flush();
                Log.d("test","upload end");


                res = httpURLConnection.getResponseCode();
                if(res == 200){
                    Log.d("test","upload ok\n");
                    InputStream inputStream1 = httpURLConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    while((len = inputStream1.read(bytes)) != -1){
                        stringBuffer.append(new String(bytes, 0, len, "UTF-8"));
                    }
                    Log.d("test","req " + stringBuffer.toString());
                }

                else {
                    Log.d("test","upload fail\n");
                }

            }

        } catch (MalformedURLException e) {
            Log.d("test","yichang1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("test","yichang2");
            e.printStackTrace();
        }

        return res;
    }
}
