package com.falldetection.common;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.falldetection.R;
import com.falldetection.fitbit.FitbitActivity;

public class SettingTab extends Fragment implements CompoundButton.OnCheckedChangeListener {
	private Switch blueSwitch;
	private TextView gpsSwitch;
	private View settingLayout;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		settingLayout = inflater.inflate(R.layout.main_tab_setting, container, false);


		return settingLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		blueSwitch = (Switch) getActivity().findViewById(R.id.switch_bluetooth);
		gpsSwitch = (TextView) getActivity().findViewById(R.id.switch_gps);

		blueSwitch.setOnCheckedChangeListener(this);
		gpsSwitch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()){
			case R.id.switch_bluetooth:
				if (isChecked) {
					mBluetoothAdapter.enable();//turn on bluetooth
					break;
				} else {
					mBluetoothAdapter.disable();// turn off bluetooth
					break;
				}
		}
	}
}
