package com.example.lbstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 测试git分支
     */
    public LocationClient mLocationClient;
    private MapView mapView;
    private TextView positionText;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  新加的相比第一行代码，不加报错
         */
        mLocationClient.setAgreePrivacy(true);
        try {
            mLocationClient=new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView=(MapView)findViewById(R.id.bd_mapView);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText=findViewById(R.id.position_text_view);
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String []permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate=false;
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData=locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void requestLocation(){
        initLoaction();
        mLocationClient.start();
    }

    private void initLoaction(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                case 1:
                    if(grantResults.length>0){
                        for(int result:grantResults){
                            if(result!=PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                        }
                        requestLocation();
                    }else{
                        Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                default:
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation.getLocType()==BDLocation.TypeGpsLocation||bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    StringBuilder currentPosition=new StringBuilder();
//                    currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
//                    currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
//                    currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
//                    currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
//                    currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
//                    currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
//                    currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
//                    currentPosition.append("定位方式：");
//                    if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
//                        currentPosition.append("GPS");
//                    }else if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
//                        currentPosition.append("网络");
//                    }
//                    positionText.setText(currentPosition);
//                }
//            });
        }
    }
}