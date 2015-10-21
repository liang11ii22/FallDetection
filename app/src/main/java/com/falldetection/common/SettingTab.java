package com.falldetection.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.falldetection.R;
public class SettingTab extends Fragment implements View.OnClickListener {
	private Button btn;
	private View settingLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		settingLayout = inflater.inflate(R.layout.main_tab_setting, container, false);


		return settingLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		btn = (Button) settingLayout.findViewById(R.id.test);
		btn.setOnClickListener(this);
		btn.setFocusable(true);
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.test) {
			Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getActivity().getApplicationContext(), BluetoothActivity.class);
			startActivity(intent);
		}

	}
}
