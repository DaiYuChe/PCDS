package com.example.user01.gcmtest;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UpLoadFileToSever extends AppCompatActivity {

    private Button upload;
    private TextView resMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("雲端推播(GCM)");
        //setContentView(R.layout.activity_up_load_file_to_sever);
        upload = (Button) findViewById(R.id.upload);
        //resMsg = (TextView) findViewById(R.id.msg);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        mFileUpload.doFileUpload(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIMRecoderAPP/RecoderTest.amr");
                    }
                }).start();
            }
        });
    }
}
