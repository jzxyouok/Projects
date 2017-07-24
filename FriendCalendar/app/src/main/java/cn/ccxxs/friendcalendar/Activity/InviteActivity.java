package cn.ccxxs.friendcalendar.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InviteActivity extends AppCompatActivity {
    @Bind(R.id.activity_info_photo) ImageView activityPhoto;
    @Bind(R.id.activity_info_title)
    TextView activityTitle;
    @Bind(R.id.creater)
    TextView creater;
    @Bind(R.id.activity_info_desc) TextView activityDesc;
    @Bind(R.id.activity_info_date) TextView activitydate;
    @Bind(R.id.activity_info_location) TextView activityLocation;
    @Bind(R.id.join) TextView join;
    TheActivity theActivity = null;
    int FLAG = 0;
    String userid;
    String activityid;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
    Handler mhanlder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                userid = uri.getQueryParameter("userid");
                activityid = uri.getQueryParameter("activityid");
            }
        }
        mhanlder = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSActivity(userid,activityid);
    }
    @OnClick(R.id.join)
    public void joinActiity(){
        joinSActivity(theActivity);
    }

    private void setData(TheActivity theActivity) {
        String starttime = simpleDateFormat.format(new Date(Long.parseLong(theActivity.getStarttime())));
        String endtime = simpleDateFormat.format(new Date(Long.parseLong(theActivity.getEndtime())));
        String date = starttime+" - "+endtime;
        activityTitle.setText(theActivity.getTitle());
        activityDesc.setText(theActivity.getDesc());
        activitydate.setText(date);
        activityLocation.setText(theActivity.getLocation());
    }

    private void getSActivity(String userid,String activityid) {
        NetUtils.getSActivity(NetUtils.GetSActivityUrl, userid,activityid,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonArray jsonA = (JsonArray)(new JsonParser().parse(respdata));
                Iterator iterator = jsonA.iterator();
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
                        String lat = "0";
                        String lng = "0";
                        if (jsonObject.get("lat")!=null||jsonObject.get("lng")!=null){
                            lat = jsonObject.get("lat").getAsString();
                            lng = jsonObject.get("lng").getAsString();
                        }
                        theActivity = new TheActivity(id,username,title,starttime,endtime,desc,location,conversationid,remindtime,"",lat,lng,"1");
                        mhanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                setData(theActivity);
                            }
                        });
                    } catch (JsonIOException e) {
                        e.printStackTrace();
                    }
                }
                FLAG = 1;
            }
        });
    }

    //添加互动
    public void joinSActivity(TheActivity theActivity) {
        final ProgressDialog progressDialog = new ProgressDialog(InviteActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("加入活动中......");
        progressDialog.show();
        //获取userid，添加活动时加入
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        int useridint = sp.getInt("userid",1 );
        String userid = Integer.toString(useridint);
        //网路请求向数据库添加活动
        NetUtils.addActivity(NetUtils.getAddActivityUrl(),userid,theActivity.getTitle(), theActivity.getStarttime(), theActivity.getEndtime(), theActivity.getLocation(), theActivity.getDesc(),theActivity.getConversationid(),theActivity.getRemindtime(),theActivity.getLat(),theActivity.getLng(),theActivity.getIsprivate(),new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonObject jsonO = (JsonObject)(new JsonParser().parse(respdata));
                String state = jsonO.get("state").getAsString();
                //返回1添加成功，0添加失败
                if (state.equals("1")){
                    mhanlder = new Handler(Looper.getMainLooper());
                    mhanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            mhanlder = new Handler(Looper.getMainLooper());
                            mhanlder.post(new Runnable() {
                                @Override
                                public void run() {
                                    join.setBackgroundColor(Color.parseColor("#F3CD70"));
                                    join.setText("加入成功");
                                    Toast.makeText(getBaseContext(), "加入活动成功", Toast.LENGTH_SHORT).show();
                                    onAddSuccess();
                                }
                            });

                        }
                    });
                } else if(state.equals("0")){
                    mhanlder = new Handler(Looper.getMainLooper());
                    mhanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            onAddFailed();
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "加入活动失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //添加成功
    public void onAddSuccess() {
//        finish();
    }
    //添加失败
    public void onAddFailed() {
        Toast.makeText(this,"加入失败，请重新添加",Toast.LENGTH_SHORT);
    }

}
