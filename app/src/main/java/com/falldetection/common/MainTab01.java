package com.falldetection.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.falldetection.R;

import java.util.List;

public class MainTab01 extends Fragment {

	EditText phone_number_editText;
	EditText sms_content_editText;
	Button send_sms_button;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return  rootView = inflater.inflate(R.layout.main_tab_01, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		phone_number_editText = (EditText) rootView.findViewById(R.id.phone_number_editText);
		sms_content_editText = (EditText) rootView.findViewById(R.id.sms_content_editText);
		send_sms_button = (Button) rootView.findViewById(R.id.send_sms_button);

		send();
	}


	/**
	 * send message
	 */
	private void send(){
		send_sms_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String phone_number = phone_number_editText.getText().toString().trim();
				String sms_content = sms_content_editText.getText().toString().trim();
				if(phone_number.equals("")) {
					Toast.makeText(getActivity(), "please input phone number!", Toast.LENGTH_LONG).show();
				} else {
					SmsManager smsManager = SmsManager.getDefault();
					if(sms_content.length() > 70) {
						List<String> contents = smsManager.divideMessage(sms_content);
						for(String sms : contents) {
							smsManager.sendTextMessage(phone_number, null, sms, null, null);
						}
					} else {
						smsManager.sendTextMessage(phone_number, null, sms_content, null, null);
					}
					Toast.makeText(getActivity(), "send finished", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
