package com.falldetection.fitbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.falldetection.R;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class FitbitActivity extends Activity {

    static final int GET_PIN_REQUEST = 101;  // The request code
    static OAuthService service;
    public static Token requestToken;
    private StringBuffer sb = new StringBuffer("");
    static TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit);

        tvOutput = (TextView) findViewById(R.id.tvOutput);
        startActivityForResult(new Intent(this, AuthenticationActivity.class), GET_PIN_REQUEST);
    }


    public void btnRetrieveData(View view) {
        EditText etPIN = (EditText) findViewById(R.id.etPIN);
        String gotPIN = etPIN.getText().toString();

        final Verifier v = new Verifier(gotPIN);

        // network operation shouldn't run on main thread
        new Thread(new Runnable() {
            public void run() {
                Token accessToken = service.getAccessToken(requestToken, v);

                OAuthRequest request = new OAuthRequest(Verb.GET,
                        "https://api.fitbit.com/1/user/3PHPHW/activities/heart/date/today/7d.json");
                service.signRequest(accessToken, request); // the access token from step
                // 4
                final Response response = request.send();
//                final TextView tvOutput = (TextView) findViewById(R.id.tvOutput);

                sb.append(response.getBody());
                // Visual output should run on main thread again...
                tvOutput.post(new Runnable() {
                    @Override
                    public void run() {
                        tvOutput.setText(sb);
                    }
                });
            }
        }).start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == GET_PIN_REQUEST) {

            if (resultCode == RESULT_OK) {
                Bundle extras = intent.getExtras();
                if(extras != null){
                    final String pin = extras.getString("PIN");
                    final EditText etPIN = (EditText) findViewById(R.id.etPIN);

                    etPIN.post(new Runnable() {
                        @Override
                        public void run() {
                            etPIN.setText(pin);
                        }
                    });
                }
            }
        }
    }
}
