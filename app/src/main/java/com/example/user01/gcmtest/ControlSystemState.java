package com.example.user01.gcmtest;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;  //加入widget類別

public class ControlSystemState extends Activity {

    Thread HttpThread;
    EditText ed1;
    EditText ed2;
    EditText ed3;
    TextView tv1;

    private String uri = "http://203.72.0.26/~nhu1403/Python_SystemChoose.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_system_state);
        setTitle("雲端推播(GCM)");
        tv1 = (TextView)findViewById(R.id.textView4);
    }

    //必須利用Handler來改變主執行緒的UI值
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //msg.getData會傳回Bundle，Bundle類別可以由getString(<KEY_NAME>)取出指定KEY_NAME的值
            tv1.setText(msg.getData().getString("response"));
        }
    };

    public void reboot(View view){
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.SystemState = "0010000";
        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();
    }

    public void StopReboot(View view){
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.SystemState = "0000000";
        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();
    }

    //按鈕的Click事件
    public void shutdown(View view) {
        //產生新的HttpThread物件
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.SystemState = "0001000";
        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();
    }

    public void StopShutdown(View view){
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.SystemState = "0000000";
        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();
    }
    //宣告一個新的類別並擴充Thread
    class HttpThread extends Thread {

        //宣告變數並指定預設值
        public String SystemState = "NoData" ;
        public String Url = "http://203.72.0.26/~nhu1403/Python_SystemChoose.php";

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            //宣告一個新的Bundle物件，Bundle可以在多個執行緒之間傳遞訊息
            Bundle myBundle = new Bundle();

            try {
                HttpClient client = new DefaultHttpClient();
                URI website = new URI(this.Url);

                //指定POST模式
                HttpPost request = new HttpPost();

                //POST傳值必須將key、值加入List<NameValuePair>
                List<NameValuePair> parmas = new ArrayList<NameValuePair>();

                //逐一增加POST所需的Key、值
                parmas.add(new BasicNameValuePair("SystemState",this.SystemState));

                //宣告UrlEncodedFormEntity來編碼POST，指定使用UTF-8
                UrlEncodedFormEntity env = new UrlEncodedFormEntity(parmas,HTTP.UTF_8);
                request.setURI(website);

                //設定POST的List
                request.setEntity(env);

                HttpResponse response = client.execute(request);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    myBundle.putString("response",EntityUtils.toString(resEntity));
                }else{
                    myBundle.putString("response","Nothing");
                }

                Message msg = new Message();
                msg.setData(myBundle);
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
