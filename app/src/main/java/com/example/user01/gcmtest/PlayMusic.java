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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;  //加入widget類別
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
public class PlayMusic extends Activity {

    TextView tv1;
    private static final String TAG = "MediaRecorderActivity";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Button playBtn;
    private Button recBtn;
    private TextView tv;
    private boolean isRecording;
    private boolean isPlaying;
    private String filePath;
    private TextView resMsg;
    private String uri = "http://203.72.0.26/~nhu1403/python_PlayMusic.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        setTitle("雲端推播(GCM)");
        resMsg = (TextView) findViewById(R.id.Msg);
        this.insert2Tv("onCreate...", false);
        this.playBtn = (Button) this.findViewById(R.id.playBtn);
        this.recBtn = (Button) this.findViewById(R.id.recBtn);
        this.recBtn.setText("錄音");
        this.playBtn.setText("播放");
        this.playBtn.setEnabled(false);

        tv1 = (TextView)findViewById(R.id.music_tv);
    }
    //必須利用Handler來改變主執行緒的UI值
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //msg.getData會傳回Bundle，Bundle類別可以由getString(<KEY_NAME>)取出指定KEY_NAME的值
            tv1.setText(msg.getData().getString("response"));
        }
    };

    public void playMusic(View view){
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.PlayState = "0000010";

        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();

    }

    //按鈕的Click事件
    public void closeMusic(View view) {
        //產生新的HttpThread物件
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.PlayState = "0000000";
        myThread.Url=uri.toString();
        //開始執行緒
        myThread.start();
    }
    //宣告一個新的類別並擴充Thread
    class HttpThread extends Thread {

        //宣告變數並指定預設值
        public String PlayState = "NoData" ;
        public String Url = "http://203.72.0.26/~nhu1403/python_PlayMusic.php";

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
                parmas.add(new BasicNameValuePair("PlayState",this.PlayState));

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
    public void onClick(View v) {
        this.insert2Tv("onClick...", false);
        switch (v.getId()) {
            case R.id.playBtn:
                if (this.isPlaying) {
                    this.stopPlay();
                } else {
// 開始播放
                    this.mediaPlayer = new MediaPlayer();
                    try {
                        this.mediaPlayer.setDataSource(this.filePath);
                        this.mediaPlayer.prepare();
                        this.mediaPlayer.start();
// 播放結束呼叫停止播放
                        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                stopPlay();
                            }
                        });
                        this.isPlaying = true;
                        this.playBtn.setText("停止播放");
// 撥播中不可錄音
                        this.recBtn.setEnabled(false);
                        this.insert2Tv("播放中...");
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                break;
            case R.id.recBtn:
                if (this.isRecording) {
// 停止錄音
                    this.mediaRecorder.stop();
// 一定要釋放
                    this.mediaRecorder.release();
                    this.isRecording = false;
                    this.recBtn.setText("錄音");
// 錄音停止可播放
                    this.playBtn.setEnabled(true);
                    this.insert2Tv("錄音完成");
                } else {
// 開始錄音
                    this.mediaRecorder = new MediaRecorder();
// 從麥克風收音
                    this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
// 輸出 3gp 格式
                    this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
// 編碼格式
                    this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
// 輸出位置
                    this.filePath = this.createFilePath();
                    Log.i("filePath", this.createFilePath());
                    this.mediaRecorder.setOutputFile(this.filePath);
                    try {
                        this.mediaRecorder.prepare();
                        this.mediaRecorder.start();
                        this.isRecording = true;
                        this.recBtn.setText("停止錄音");
// 錄音中不可播放
                        this.playBtn.setEnabled(false);
                        this.insert2Tv("錄音中...");
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                break;
        }
    }
    private void stopPlay() {
// 停止播放
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.isPlaying = false;
        this.playBtn.setText("播放");
// 撥播停止可錄音
        this.recBtn.setEnabled(true);
        this.insert2Tv("停止播放");
    }
    public void upload(View view){
        //startActivity(new Intent(this, UpLoadFileToSever.class));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectServer mFileUpload = new ConnectServer();
                mFileUpload.setOnFileUploadListener(new ConnectServer.OnFileUploadListener() {
                    @Override
                    public void onFileUploadSuccess(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resMsg.setText(msg);

                            }
                        });

                    }

                    @Override
                    public void onFileUploadFail(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resMsg.setText(msg);
                            }
                        });
                    }
                });
                mFileUpload.doFileUpload(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/RecoderAPP/RecoderTest.amr");
            }
        }).start();
    }

    private String createFilePath() {
        //File sdCardDir = Environment.getExternalStorageDirectory();
        File sdCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File RecoderAPP = new File(sdCardDir + "/RecoderAPP");
        if (!RecoderAPP.exists()) {
            RecoderAPP.mkdir();
        }

        //"RecoderAPP"
        String fileName = "RecoderTest.amr";
        String path = new File(RecoderAPP, fileName).getAbsolutePath();
        this.insert2Tv("輸出位置：" +  path, false);
        return path;
    }

    private void insert2Tv(String msg) {
        this.insert2Tv(msg, true);
    }

    private void insert2Tv(String msg, boolean ui) {
        if (this.tv == null) {
            this.tv = (TextView) this.findViewById(R.id.tv);
        }
        if (ui) {
            this.tv.setText(msg);
        }
        Log.d(TAG, msg);
    }
}
