package cn.ccxxs.friendcalendar.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.Activity.ActivityNearby;
import cn.ccxxs.friendcalendar.Constants;
import cn.ccxxs.friendcalendar.R;
import cn.ccxxs.friendcalendar.Setting.SettingsActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import ru.egslava.blurredview.BlurredImageView;

import static cn.ccxxs.friendcalendar.Constants.userid;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    @Bind(R.id.backgroundImg)
    BlurredImageView backgroundImg;
    @Bind(R.id.circleavatar)
    CircleImageView circleavatar;
    @Bind(R.id.username)
    TextView usernameDisplay;
    @Bind(R.id.activity_nearby)
    CardView activity_nearby;

    public ProfileFragment() {
        // Required empty public constructor
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        if (Constants.Avatar != null){
            if (Constants.Avatar.length()>0){
                Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).error(R.drawable.avatar).into(circleavatar);
//                Glide.with(MainActivity.main).load("http://106.14.138.105/avatar/"+userid+".jpg").into(backgroundImg);
                Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).error(R.drawable.avatar).bitmapTransform(new BlurTransformation(getContext(), 20)).into(backgroundImg);
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        usernameDisplay.setText(Constants.username);

//        Glide.with(getContext()).load("http://106.14.138.105/avatar/1.jpg").into(backgroundImg);
    }


    @OnClick(R.id.circleavatar)
    public void openSetting(){
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.activity_nearby)
    public void nearby(){
        Intent intent = new Intent(getContext(), ActivityNearby.class);
        startActivity(intent);
    }

    public void refreshAvatar() {
        Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").placeholder(R.drawable.avatar).error(R.drawable.avatar).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(circleavatar);
        Glide.with(this).load("http://106.14.138.105/avatar/"+userid+".jpg").placeholder(R.drawable.avatar).error(R.drawable.avatar).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).bitmapTransform(new BlurTransformation(getContext(), 20)).into(backgroundImg);
    }
}
