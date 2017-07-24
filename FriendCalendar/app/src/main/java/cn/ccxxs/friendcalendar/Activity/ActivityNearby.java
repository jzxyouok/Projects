package cn.ccxxs.friendcalendar.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ccxxs on 2017/6/15.
 */

public class ActivityNearby extends AppCompatActivity implements OnGetGeoCoderResultListener {
    TextureMapView mMapView = null;
    private LocationClient locationClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private BaiduMap baiduMap;
    boolean isFirstLoc = true; // 是否首次定位

    private LatLng currentPt;

    GeoCoder mSearch = null; // ，也可去掉地图模块独立使用
    private BDLocation currentLoction;
    ArrayList<TheActivity> dataFromNet = new ArrayList<>();
    List<OverlayOptions>  overlays = new ArrayList<OverlayOptions>();

    Handler mhanlder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_nearby);

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        //获取数据
        getActivityList();
        //获取百度地图对象
        baiduMap = mMapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);

        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 5000; //5秒发送一次
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setNeedDeviceDirect(true); //返回的定位结果包含手机机头方向
        locationClient.setLocOption(option);
        locationClient.start(); //启动位置请求
        locationClient.requestLocation();//发送请求

//        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                currentPt = latLng;
//            }
//
//            @Override
//            public boolean onMapPoiClick(MapPoi mapPoi) {
//                return false;
//            }
//        });
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Bundle bundle = marker.getExtraInfo();
                mhanlder = new Handler(Looper.getMainLooper());
                mhanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        String userid = bundle.getString("userid");
                        int activityid = bundle.getInt("activityid");
                        Uri uri = Uri.parse("friendcalendar://invite?userid="+userid+"&activityid="+activityid);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location1);
            //创建一个图层选项
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            reverseSearch(latlng);
            OverlayOptions options = new MarkerOptions().position(latlng).icon(bitmapDescriptor);
            baiduMap.addOverlay(options);
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(latlng)
                    .zoom(15)
                    .build();
//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//改变地图状态
            baiduMap.setMapStatus(mMapStatusUpdate);
            locationClient.stop();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public void reverseSearch(LatLng latLng)
    {
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng));
    }

    /**
     * 正向地理编码和反向地理编码
     * @param result
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        baiduMap.clear();
        baiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.add)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        baiduMap.clear();
        baiduMap.addOverlay(
                new MarkerOptions()
                        .position(result.getLocation())                                     //坐标位置
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location1))  //图标
                        .title(result.getAddress())                                         //标题
        );
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        //在地图上加载活动
        UpdateMap();
    }

    //刷新活动列表
    private void getActivityList() {
        NetUtils.getOpenActivity(NetUtils.GetOpenActivityUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonArray jsonA = (JsonArray)(new JsonParser().parse(respdata));
                Iterator iterator = jsonA.iterator();
                dataFromNet.clear();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.marker, null);
                TextView titleTv = (TextView) view.findViewById(R.id.cover_nameId);
                while (iterator.hasNext()){
                    JsonObject jsonObject = (JsonObject) iterator.next();
                    try {
                        int id = jsonObject.get("id").getAsInt();
                        String username = jsonObject.get("username").getAsString();
                        String title = jsonObject.get("title").getAsString();
                        String starttime = jsonObject.get("starttime").getAsString();
                        String endtime = jsonObject.get("endtime").getAsString();
                        String desc = jsonObject.get("content").getAsString();
                        String location = jsonObject.get("location").getAsString();
                        String conversationid = jsonObject.get("conversationid").getAsString();
                        String remindtime = jsonObject.get("remindtime").getAsString();
                        String activityimg = "";
                        if (jsonObject.get("activityimg")!=null){
                            activityimg = jsonObject.get("activityimg").getAsString();
                        }
                        String lat = "0";
                        String lng = "0";
                        if (jsonObject.get("lat")!=null||jsonObject.get("lng")!=null){
                            lat = jsonObject.get("lat").getAsString();
                            lng = jsonObject.get("lng").getAsString();
                        }
                        int userid = jsonObject.get("userid").getAsInt();
                        titleTv.setText(title);
                        BitmapDescriptor bd1 = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(view));
                        Bundle activityinfo = new Bundle();
                        activityinfo.putString("userid", String.valueOf(userid));
                        activityinfo.putInt("activityid", id);
                        overlays.add(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).icon(bd1).title(title).extraInfo(activityinfo));
                        dataFromNet.add(new TheActivity(id,username,title,starttime,endtime,desc,location,conversationid,remindtime,activityimg,lat,lng,"1"));
                    } catch (JsonIOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void UpdateMap() {
            baiduMap.addOverlays(overlays);
    }
    private Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
}
