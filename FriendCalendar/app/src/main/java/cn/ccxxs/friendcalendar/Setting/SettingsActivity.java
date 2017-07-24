package cn.ccxxs.friendcalendar.Setting;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.Constants;
import cn.ccxxs.friendcalendar.Fragment.ChoosePicDialog;
import cn.ccxxs.friendcalendar.Login.LoginActivity;
import cn.ccxxs.friendcalendar.MainActivity;
import cn.ccxxs.friendcalendar.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.ccxxs.friendcalendar.Constants.userid;
import static cn.ccxxs.friendcalendar.R.id.avatarPlace;

public class SettingsActivity extends AppCompatActivity {
    @Bind(R.id.changeAvatar)
    CardView changeAvatar;
    @Bind(R.id.exit)
    CardView exit;
    @Bind(avatarPlace)
    CircleImageView avatar;
    @Bind(R.id.username)
    TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        if (Constants.Avatar != null){
            if (Constants.Avatar.length()>0){
                Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).error(R.drawable.avatar).into(avatar);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        username.setText(Constants.username);
    }

    @OnClick(R.id.changeAvatar)
    public void changeAvatar(){
        ChoosePicDialog share = ChoosePicDialog.getInstance();
        share.show(getSupportFragmentManager(), "Choose");
    }

    @OnClick(R.id.exit)
    public void exit(){
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loginState", false);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        MainActivity.main.finish();
        finish();
    }
    public void refreshAvatar(){
        Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).error(R.drawable.avatar).into(avatar);
    }

}
