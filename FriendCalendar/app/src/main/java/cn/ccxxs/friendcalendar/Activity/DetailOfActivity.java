package cn.ccxxs.friendcalendar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ccxxs.friendcalendar.Constants;
import cn.ccxxs.friendcalendar.Fragment.AddImgDialog;
import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.R;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import static cn.ccxxs.friendcalendar.Constants.userid;
import static cn.ccxxs.friendcalendar.Constants.username;

public class DetailOfActivity extends AppCompatActivity {
    @Bind(R.id.detail) TextView detail;
    @Bind(R.id.detailActivityCloseBtn) ImageView closeBtn;
    @Bind(R.id.activity_info_photo) ImageView activityPhoto;
    @Bind(R.id.activity_info_title) TextView activityTitle;
    @Bind(R.id.peopleonum) TextView peopleonum;
    @Bind(R.id.creater) TextView creater;
    @Bind(R.id.activity_info_desc) TextView activityDesc;
    @Bind(R.id.activity_info_date) TextView activitydate;
    @Bind(R.id.activity_info_location) TextView activityLocation;
    @Bind(R.id.inviteBtn) LinearLayout inviteBtn;
    @Bind(R.id.discussBtn) LinearLayout discussBtn;
    @Bind(R.id.bmapView)
    TextureMapView bmapView;
    private BaiduMap baiduMap;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
    String conversationid;
    int activityidint;
    String activityimg;
    TheActivity item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        //第一次进入设置详情选中
        Intent intent=getIntent();
        item= (TheActivity) intent.getSerializableExtra("activity");
        conversationid = item.getConversationid();
        activityidint = item.getId();
        String starttime = simpleDateFormat.format(new Date(Long.parseLong(item.getStarttime())));
        String endtime = simpleDateFormat.format(new Date(Long.parseLong(item.getEndtime())));
        String date = starttime+" - "+endtime;
        final String activityid = Integer.toString(item.getId());
        //设置界面数据

        creater.setText(item.getUsername()+" 创建");
        activityTitle.setText(item.getTitle());
        activityDesc.setText(item.getDesc());
        activitydate.setText(date);
        activityLocation.setText(item.getLocation());
        activityimg = item.getActivityimg().trim();
        //加载图片
        if (activityimg.length()>0){
            Log.w("activityimg", "onCreate: "+activityimg );
            Glide.with(this).load(activityimg).error(R.drawable.defaultimg).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(activityPhoto);
        }
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", item.getTitle());
                bundle.putString("summary", item.getDesc());
                bundle.putString("thumb", "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2240042335,3502573557&fm=58");
                bundle.putString("url", "http://106.14.138.105/join?userid="+userid+"&activityid="+activityid);
                shareWechat(bundle);
            }
        });
        discussBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLCChatKit();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddImgDialog share = AddImgDialog.getInstance(activityidint);
                share.show(getSupportFragmentManager(), "Choose");
            }
        });
        //百度地图
        baiduMap = bmapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        LatLng cenpt =  new LatLng(Double.parseDouble(item.getLat()),Double.parseDouble(item.getLng()));
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(15)
                .build();

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.setMapStatus(mMapStatusUpdate);

        baiduMap.addOverlay(
                new MarkerOptions()
                        .position(cenpt)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location1))  //图标
                        .title(item.getLocation())                                         //标题
        );
//        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(cenpt));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void isLCChatKit() {
        if (LCChatKit.getInstance().getClient() == null){
            LCChatKit.getInstance().open(username, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                        LoginToTalk();
                    } else {
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            LoginToTalk();
        }
    }
    public void LoginToTalk() {
        final AVIMConversation conversation = Constants.avimClient.getConversation(conversationid);
            conversation.join(new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    Intent intent = new Intent(getApplicationContext(), LCIMConversationActivity.class);
                    intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
                    startActivity(intent);
                }
            });
    }

    public void shareWechat(Bundle bundle) {
        ShareSDK.initSDK(this);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);// 设置成分享网页
        String title, summary, thumb, url;
        title=bundle.getString("title");
        summary=bundle.getString("summary");
        thumb=bundle.getString("thumb");
        url=bundle.getString("url");
        sp.setTitle(title);
        sp.setText(summary);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setImageUrl(thumb);
        sp.setUrl(url);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }
            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {

            }
            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        wechat.share(sp);
    }
    public void refreshImg() {
        Glide.with(this).load(activityimg).error(R.drawable.defaultimg).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(activityPhoto);
    }
}