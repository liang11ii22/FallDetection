package com.falldetection.common;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.falldetection.R;
import com.falldetection.util.LocationStorUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback
{


    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private GoogleApiClient mGoogleApiClient;

    private int currentIndex;
    private float tmp = 0;

    /**
     * top button
     */
    private LinearLayout mTabHome;
    private LinearLayout mTabProfile;
    private LinearLayout mTabContact;
    private LinearLayout mTabSetting;

    private ImageButton imgButtonHome;
    private ImageButton imgButtonProfile;
    private ImageButton imgButtonContact;
    private ImageButton imgButtonSetting;

    private LocationStorUtils data;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new LocationStorUtils(this);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        initView();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount()
            {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0){
                return mFragments.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.requestTransparentRegion(mViewPager);
    }


    protected void resetTabBtn()
    {
        imgButtonHome.setImageResource(R.drawable.home);
        imgButtonProfile.setImageResource(R.drawable.profile);
        imgButtonContact.setImageResource(R.drawable.map);
        imgButtonSetting.setImageResource(R.drawable.setting);
    }

    private void initView()
    {

        mTabHome = (LinearLayout) findViewById(R.id.id_tab_top_home);
        mTabProfile = (LinearLayout) findViewById(R.id.id_tab_top_profile);
        mTabContact = (LinearLayout) findViewById(R.id.id_tab_top_contact);
        mTabSetting = (LinearLayout) findViewById(R.id.id_tab_top_setting);

        mTabHome.setOnClickListener(this);
        mTabProfile.setOnClickListener(this);
        mTabContact.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);

        imgButtonHome = ((ImageButton) mTabHome.findViewById(R.id.btn_tab_top_home));
        imgButtonProfile = ((ImageButton) mTabProfile.findViewById(R.id.btn_tab_top_profile));
        imgButtonContact = ((ImageButton) mTabContact.findViewById(R.id.btn_tab_top_contact));
        imgButtonSetting = ((ImageButton) mTabSetting.findViewById(R.id.btn_tab_top_setting));

        HomeTab tab01 = new HomeTab();
        ProfileTab tab02 = new ProfileTab();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        SettingTab tab04 = new SettingTab();

        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(mapFragment);
        mFragments.add(tab04);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTabBtn();
        switch (position)
        {
            case 0:
                imgButtonHome.setImageResource(R.drawable.home);
                break;
            case 1:
                imgButtonProfile.setImageResource(R.drawable.profile);
                break;
            case 2:
                imgButtonContact.setImageResource(R.drawable.map);
                break;
            case 3:
                imgButtonSetting.setImageResource(R.drawable.setting);
                break;
        }

        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_tab_top_home){
            mViewPager.setCurrentItem(0);
        }else if(v.getId() == R.id.id_tab_top_profile){
            mViewPager.setCurrentItem(1);
        }else if(v.getId() == R.id.id_tab_top_contact){
            mViewPager.setCurrentItem(2);
        }else if(v.getId() == R.id.id_tab_top_setting){
                mViewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {
        if (mGoogleApiClient.isConnected()) {
            String msg = "Location = "
                    + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            }else{
            Toast.makeText(getApplicationContext(), "no gps", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if(placeLikelihood.getLikelihood() > tmp){
                        data.store(placeLikelihood.getPlace().getName().toString());
                        tmp = placeLikelihood.getLikelihood();
                    }
                }
                likelyPlaces.release();

            }
        });
//        try {
//            Geocoder geo = new Geocoder(this, Locale.getDefault());
//            List<Address> addresses = geo.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
//            if (addresses.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "addressed is Empty", Toast.LENGTH_SHORT).show();
//            } else {
//                if (addresses.size() > 0) {
//                    Toast.makeText(getApplicationContext(), addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(getApplicationContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}
