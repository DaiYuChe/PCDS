package com.example.user01.gcmtest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView regId;
    private EditText message;
    private Button send;
    private EditText ed1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("雲端推播(GCM)");
        MagicLenGCM magicLenGCM = new MagicLenGCM(this);
        magicLenGCM.openGCM();
        Log.i("regId", magicLenGCM.getRegistrationId());
    }

    public void buzzer(View view){
        startActivity(new Intent(this, ControlRaspiBuzzer.class));
    }

    public void SystemState(View view){
        startActivity(new Intent(this, ControlSystemState.class));
    }
    public void control_led(View view){
        startActivity(new Intent(this, ControlLED.class));
    }
    public void playMusic(View view){
        startActivity(new Intent(this, PlayMusic.class));
    }

}
