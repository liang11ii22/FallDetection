package com.falldetection.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.falldetection.R;
import com.falldetection.util.DataStoreUtils;

import java.util.List;

public class AlarmActivity extends Activity {
    private Button btnCancel;
    private Button btnReturn;
    private TimeCount time;
    private DataStoreUtils data;
    private SoundPool mPool;
    private int voiceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        data = new DataStoreUtils(this);
        voiceID = initSoundPool();

        time = new TimeCount(60000, 1000);// create CountDownTimer object
        btnCancel = (Button) findViewById(R.id.button_cancel_alarm);
        btnReturn = (Button) findViewById(R.id.button_return_alarm);
        time.start();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                time.cancel();
                mPool.stop(voiceID);
                mPool.release();
                btnCancel.setVisibility(View.GONE);
                btnCancel.setClickable(false);
                btnReturn.setVisibility(View.VISIBLE);
                btnReturn.setClickable(true);
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * return soundpool id according different version
     * @return
     */
    private int initSoundPool(){
        if(Build.VERSION.SDK_INT>=21){
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);//audio number
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            mPool = builder.build();
        }else{
            mPool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        }

        return mPool.load(getApplicationContext(),R.raw.alarmsound,1);
    }

    public class TimeCount extends CountDownTimer{
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//timeperiod, interval
        }

        @Override
        public void onFinish() {
            btnCancel.setVisibility(View.GONE);
            btnCancel.setClickable(false);
            btnReturn.setVisibility(View.VISIBLE);
            btnReturn.setClickable(true);
            mPool.stop(voiceID);
            mPool.release();
        }
        @Override
        public void onTick(long millisUntilFinished){
            btnCancel.setText(millisUntilFinished / 1000+"s");
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
            mPool.play(voiceID, 1, 1, 0, -1, 1);
        }
    }

    public void sendMessage(){
        String phone_number = data.getPhone();
        String sms_content = "Cancel: Everything is fine!";
        SmsManager smsManager = SmsManager.getDefault();
        if (sms_content.length() > 70) {
            List<String> contents = smsManager.divideMessage(sms_content);
            for (String sms : contents) {
                smsManager.sendTextMessage(phone_number, null, sms, null, null);
            }
        } else {
            smsManager.sendTextMessage(phone_number, null, sms_content, null, null);
        }
        Toast.makeText(getApplicationContext(), "send success", Toast.LENGTH_SHORT).show();
    }

}
