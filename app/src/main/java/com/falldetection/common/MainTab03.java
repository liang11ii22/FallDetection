package com.falldetection.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.falldetection.R;
public class MainTab03 extends Fragment implements View.OnClickListener {
	private Button btn;
	private View homeLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		homeLayout = inflater.inflate(R.layout.main_tab_03, container, false);
		return homeLayout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		btn = (Button) homeLayout.findViewById(R.id.fitbit);
		btn.setFocusable(true);
		btn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.fitbit) {
			Intent intent = new Intent(getActivity().getApplicationContext(), FitbitActivity.class);
			startActivity(intent);
		}

	}
}
