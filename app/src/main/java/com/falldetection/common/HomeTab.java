package com.falldetection.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.falldetection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeTab extends Fragment implements MyViewPager.OnSingleTouchListener, MyViewPager.OnPageChangeListener {
	private MyViewPager viewPager;
	private List<ImageView> imageViews;

	private String[] titles;
	private int[] imageResId;
	private List<View> dots;

	private TextView tv_title;
	private int currentItem = 0;
	private int oldPosition = 0;

	private View home;

	private ScheduledExecutorService scheduledExecutorService;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		home = inflater.inflate(R.layout.main_tab_home, container, false);
		return home;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		imageResId = new int[] { R.drawable.a, R.drawable.d, R.drawable.e };
		titles = new String[imageResId.length];
		titles[0] = "fffffffffffff";
		titles[1] = "aaaaaaa";
		titles[2] = "bbbb";

		imageViews = new ArrayList<ImageView>();

		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(getContext());
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}


		dots = new ArrayList<View>();
		dots.add(home.findViewById(R.id.v_dot0));
		dots.add(home.findViewById(R.id.v_dot1));
		dots.add(home.findViewById(R.id.v_dot2));
		dots.add(home.findViewById(R.id.v_dot3));
		dots.add(home.findViewById(R.id.v_dot4));

		tv_title = (TextView) home.findViewById(R.id.tv_title);
		tv_title.setText(titles[0]);


		viewPager = (MyViewPager) home.findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());
		viewPager.addOnPageChangeListener(this);

	}

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public  void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}
	@Override
	public void onSingleTouch() {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		currentItem = position;
		tv_title.setText(titles[position]);
		dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
		dots.get(position).setBackgroundResource(R.drawable.dot_focused);
		oldPosition = position;;
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageResId.length;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
