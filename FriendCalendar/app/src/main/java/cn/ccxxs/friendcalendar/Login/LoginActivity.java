package cn.ccxxs.friendcalendar.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ccxxs.friendcalendar.Constants;
import cn.ccxxs.friendcalendar.MainActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import cn.leancloud.chatkit.LCChatKit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    Handler mhanlder;
    private static final int REQUEST_SIGNUP = 0;
    @Bind(R.id.input_username)
    EditText usernameText;
    @Bind(R.id.input_password) EditText passwordText;
    @Bind(R.id.btn_login)
    Button loginButton;
    @Bind(R.id.link_signup)
    TextView signupLink;
    @Bind(R.id.backgroundAnimation)
    ImageView backgroundAnimation;
    int Flag = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Glide.with(this)
                .load(R.drawable.backani)
                .into(backgroundAnimation);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //隐藏Actionbar
        getSupportActionBar().hide();
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        // 登录验证逻辑
        NetUtils.Login(NetUtils.getLoginUrl(), username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonObject jsonO = (JsonObject)(new JsonParser().parse(respdata));
                String state = jsonO.get("state").getAsString();
                //返回1登录成功，0登录失败
                if (state.equals("1")){
                    //在SharedPreferences中存储登陆信息
                    SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    final int userid = jsonO.get("userid").getAsInt();
                    editor.putInt("userid",userid);
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("loginState",true);
                    editor.commit();
                    //登录聊天室
                    LoginToChatRoom(username);
                    while (Flag != 1){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mhanlder = new Handler(Looper.getMainLooper());
                    mhanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }
                    });

                } else if(state.equals("0")){
                    mhanlder = new Handler(Looper.getMainLooper());

                    mhanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameText.setError("请输入有效用户名");
            valid = false;
        } else {
            usernameText.setError(null);
        }


        return valid;
    }
    public void LoginToChatRoom(String username) {
        LCChatKit.getInstance().open(username, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (null == e) {
                    Constants.avimClient = avimClient;
                    Flag = 1;
                } else {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
