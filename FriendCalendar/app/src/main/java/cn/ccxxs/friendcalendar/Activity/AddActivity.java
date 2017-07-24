package cn.ccxxs.friendcalendar.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.CreateFun.ContentEdit;
import cn.ccxxs.friendcalendar.CreateFun.Map;
import cn.ccxxs.friendcalendar.MainActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import cn.leancloud.chatkit.LCChatKit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static cn.ccxxs.friendcalendar.Constants.username;

public class AddActivity extends AppCompatActivity {
    @Bind(R.id.complete) ImageView complete;
    @Bind(R.id.close) ImageView close;
    @Bind(R.id.titledisplay) EditText titledisplay;
    @Bind(R.id.date) LinearLayout datepick;
    @Bind(R.id.datedisplay) TextView datedisplay;
    @Bind(R.id.location) LinearLayout location;
    @Bind(R.id.locationdisplay) TextView locationdisplay;
    @Bind(R.id.content) LinearLayout content;
    @Bind(R.id.contentdisplay) TextView contentdisplay;
    @Bind(R.id.remind) LinearLayout remind;
    @Bind(R.id.reminddisplay) TextView reminddisplay;
    @Bind(R.id.privacy)
    Switch privacy;

    SimpleDateFormat simpleDateFormat;
    DoubleDateAndTimePickerDialog.Builder doubleBuilder;
    SingleDateAndTimePickerDialog.Builder singleBuilder;

    private long start_time = 0;
    private long end_time = 0;
    private long remind_time = 0;
    String conversationid = "";
    private double latitude = 0;
    private double lngitude = 0;
    private int isprivate = 1;
    List<String> idList;
    Handler mhanlder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //-----
        ButterKnife.bind(this);
        this.simpleDateFormat = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());
        privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isprivate = 1;
                }else {
                    isprivate = 0;
                }
            }
        });
        createTalkRoom();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (doubleBuilder != null)
            doubleBuilder.close();
    }
    @OnClick(R.id.close)
    public void close() {
        finish();
    }
    @OnClick(R.id.complete)
    public void complete() {
        addActivity();
    }
    @OnClick(R.id.date)
    public void datePick() {
        datepick();
    }
    @OnClick(R.id.location)
    public void location() {
        Intent intent=new Intent(getApplicationContext(),Map.class);
        intent.putExtra("locationContent",locationdisplay.getText().toString());
        startActivityForResult(intent,1);
    }
    @OnClick(R.id.content)
    public void content(){
        Intent intent=new Intent(getApplicationContext(),ContentEdit.class);
        intent.putExtra("editingContent",contentdisplay.getText().toString());
        startActivityForResult(intent,2);
    }
    @OnClick(R.id.remind)
    public void remind() {
        timepick();
    }
    //intent返回的数据处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
                if(resultCode == Activity.RESULT_OK){
                    String result=data.getStringExtra("contentresult");

                    if (result.length()>0){
                        contentdisplay.setText(result);
                    }else {
                        contentdisplay.setText("活动内容");
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
        }else if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("locationresult");
                latitude = data.getDoubleExtra("lat",0);
                lngitude = data.getDoubleExtra("lng",0);
                if (result.length()>0){
                    locationdisplay.setText(result);
                }else {
                    locationdisplay.setText("活动地点");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    //日期选择
    public void datepick() {
        final Date now = new Date();
        final Calendar calendarMin = Calendar.getInstance();
        final Calendar calendarMax = Calendar.getInstance();

        calendarMin.setTime(now); // Set min now
        calendarMax.setTime(new Date(now.getTime() + TimeUnit.DAYS.toMillis(150))); // Set max now + 150 days

        final Date minDate = calendarMin.getTime();
        final Date maxDate = calendarMax.getTime();
        doubleBuilder = new DoubleDateAndTimePickerDialog.Builder(this)
                .backgroundColor(getResources().getColor(R.color.tab_enable))
                .mainColor(getResources().getColor(R.color.colorPrimary))
                .minutesStep(15)
                .mustBeOnFuture()
                .minDateRange(minDate)
                .maxDateRange(maxDate)
                //.defaultDate(now)
                .tab0Date(now)
                .tab1Date(new Date(now.getTime() + TimeUnit.HOURS.toMillis(1)))
                .title("活动时间")
                .tab0Text("开始")
                .tab1Text("结束")
                .listener(new DoubleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(List<Date> dates) {
                        final StringBuilder stringBuilder = new StringBuilder();
                        for (Date date : dates) {
                            stringBuilder.append(simpleDateFormat.format(date)).append("\n");
                        }
                        start_time = dates.get(0).getTime();
                        end_time = dates.get(1).getTime();
                        Log.w("start_time", "onDateSelected: "+start_time );
                        Log.w("end_time", "onDateSelected: "+end_time );
                        datedisplay.setText(stringBuilder.toString());
                    }
                });
        doubleBuilder.display();
    }
    public void timepick() {
        final Date now = new Date();
        singleBuilder = new SingleDateAndTimePickerDialog.Builder(this)
                .backgroundColor(getResources().getColor(R.color.tab_enable))
                .mainColor(getResources().getColor(R.color.colorPrimary))
                .minutesStep(15)
                //.defaultDate(now)
                .title("提醒时间")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        String time = simpleDateFormat.format(date);
                        remind_time = date.getTime();
                        reminddisplay.setText(time);
                    }
                });
        singleBuilder.display();
    }

    //添加活动
    public void addActivity() {
        if (!validate()) {
            onAddFailed();
            Toast.makeText(this,"请重新填写活动信息",Toast.LENGTH_SHORT);
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(AddActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("创建活动中......");
        progressDialog.show();
        String title = titledisplay.getText().toString();
//        String date = datedisplay.getText().toString();
        //开始时间
        String starttime = Long.toString(start_time);
        String endtime = Long.toString(end_time);
        String location = locationdisplay.getText().toString();
        String content = contentdisplay.getText().toString();
        //获取userid，添加活动时加入
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        int useridint = sp.getInt("userid",1 );
        String userid = Integer.toString(useridint);
        //提醒时间
        String remindtime = Long.toString(remind_time);
        String lat = Double.toString(latitude);
        String lng = Double.toString(lngitude);
        String isprivatestr = Integer.toString(isprivate);
        //网路请求向数据库添加活动
        NetUtils.addActivity(NetUtils.getAddActivityUrl(),userid,title, starttime, endtime, location, content,conversationid,remindtime,lat,lng,isprivatestr,new Callback() {
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
                            onAddSuccess();
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "添加活动成功", Toast.LENGTH_SHORT).show();
                            MainActivity mainActivity = (MainActivity) MainActivity.main;
                            mainActivity.refreshList();
                        }
                    });
                } else if(state.equals("0")){
                    mhanlder = new Handler(Looper.getMainLooper());
                    mhanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            onAddFailed();
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "添加活动失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //添加成功
    public void onAddSuccess() {
        finish();
    }
    //添加失败
    public void onAddFailed() {
        Toast.makeText(this,"添加失败，请重新添加",Toast.LENGTH_SHORT);
    }

    public boolean validate() {
        boolean valid = true;
        String title = titledisplay.getText().toString();
        String date = datedisplay.getText().toString();
        String location = locationdisplay.getText().toString();
        String content = contentdisplay.getText().toString();

        if (title.length()==0) {
            Toast.makeText(this,"请填写活动标题",Toast.LENGTH_SHORT);
            valid = false;
        }
        if (date.equals("活动时间") || date.length()==0 || start_time == 0 || end_time == 0) {
            Toast.makeText(this,"请选择活动时间",Toast.LENGTH_SHORT);
            valid = false;
        }
        if (location.equals("活动地点") || location.length()==0) {
            Toast.makeText(this,"请添加活动地点",Toast.LENGTH_SHORT);
            valid = false;
        }
        if (content.equals("活动内容") || location.length()==0) {
            Toast.makeText(this,"请添加活动内容",Toast.LENGTH_SHORT);
            valid = false;
        }
        return valid;
    }
    public void createTalkRoom() {
        idList = new ArrayList<>();
        String random = Long.toString(new Date(System.currentTimeMillis()).getTime());
        idList.add(random);
        if (LCChatKit.getInstance().getClient() == null){
            LCChatKit.getInstance().open(username, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                        createRoom();
                    } else {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            createRoom();
        }
    }
    public void createRoom() {
        LCChatKit.getInstance().getClient().createConversation(idList, "HAHA", null,false,true, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
                conversationid = avimConversation.getConversationId();
            }
        });
    }

}
