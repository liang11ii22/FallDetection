package com.falldetection.common;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.falldetection.R;
import com.falldetection.bluetooth.BluetoothArduinoService;
import com.falldetection.logger.Log;
import com.falldetection.util.DataStoreUtils;
import com.falldetection.util.LocationStorUtils;

import java.util.List;

public class ProfileTab extends Fragment{

	private static final int REQUEST_BLUETOOTH = 1;

	EditText phone_number_editText;
	EditText name_editText;
	EditText age_editText;
	EditText height_editText;
	EditText weight_editText;
	Button btnStart;
	Button btnStop;
	RadioGroup sex_rGroup;
	View rootView;

	DataStoreUtils data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return  rootView = inflater.inflate(R.layout.main_tab_profile, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		data = new DataStoreUtils(getActivity());

		phone_number_editText = (EditText) rootView.findViewById(R.id.phone_number_editText);
		name_editText = (EditText) rootView.findViewById(R.id.name_editText);
		age_editText = (EditText) rootView.findViewById(R.id.age_editText);
		height_editText = (EditText) rootView.findViewById(R.id.height_editText);
		weight_editText = (EditText) rootView.findViewById(R.id.weight_editText);

		btnStart = (Button) rootView.findViewById(R.id.btn_monitor);
		btnStop = (Button) rootView.findViewById( R.id.btn_stop);
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				bluetoothAdapter.disable();
				btnStop.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
			}
		});
		sex_rGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);

		MonitorStart();
	}

	private boolean validation(){
		if (phone_number_editText.getText().toString().equals("")) {
			Toast.makeText(getActivity().getApplicationContext(), "please input phone number!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (name_editText.getText().toString().equals("")) {
			Toast.makeText(getActivity().getApplicationContext(), "please input your name!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (age_editText.getText().toString().equals("")) {
			Toast.makeText(getActivity().getApplicationContext(), "please input your age!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (height_editText.getText().toString().equals("")) {
			Toast.makeText(getActivity().getApplicationContext(), "please input your height!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (weight_editText.getText().toString().equals("")) {
			Toast.makeText(getActivity().getApplicationContext(), "please input your weight!", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			return true;
		}

	}
	/**
	 * start monitor
	 */
	private void MonitorStart(){
		btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(getActivity().getApplicationContext(), BluetoothActivity.class);
//				startActivity(intent);
//				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//				if (!mBluetoothAdapter.isEnabled()) {
//					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//					startActivityForResult(enableIntent, 3);
//				}
				if (turnOnBluetooth()) {
					if (validation()) {
						String gender = null;
						for (int i = 0; i < sex_rGroup.getChildCount(); i++) {
							RadioButton rd = (RadioButton) sex_rGroup.getChildAt(i);

							if (rd.isChecked()) {
								gender = rd.getText().toString().trim();
								break;
							}
						}
						data.store(name_editText.getText().toString().trim(),
								phone_number_editText.getText().toString(),
								age_editText.getText().toString(),
								height_editText.getText().toString(),
								weight_editText.getText().toString(),
								gender);

//					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//					if (!mBluetoothAdapter.isEnabled()) {
//						Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//						startActivityForResult(enableIntent, 3);
//					}
//					String phone_number = data.getPhone();
//					List<String> content = data.getAll();
//					StringBuffer sms_content = new StringBuffer("HELP:");
//					for (int i = 0; i < content.size(); i++) {
//						sms_content.append(content.get(i)).append(" ");
//					}
//					SmsManager smsManager = SmsManager.getDefault();
//					if (sms_content.length() > 70) {
//							List<String> contents = smsManager.divideMessage(sms_content.toString());
//							for (String sms : contents) {
//								smsManager.sendTextMessage(phone_number, null, sms, null, null);
//							}
//					} else {
//							smsManager.sendTextMessage(phone_number, null, sms_content.toString(), null, null);
//					}
//					Toast.makeText(getActivity(), "send success", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});


	}

	 public boolean turnOnBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter != null)
		{
			return bluetoothAdapter.enable();
		}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_BLUETOOTH:
				if (resultCode == Activity.RESULT_OK) {
					btnStart.setVisibility(View.GONE);
					btnStop.setVisibility(View.VISIBLE);
				}
				break;
		}
	}
}
