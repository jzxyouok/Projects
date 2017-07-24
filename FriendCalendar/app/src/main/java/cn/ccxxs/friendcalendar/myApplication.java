package cn.ccxxs.friendcalendar;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by ccxxs on 2017/6/8.
 */

public class myApplication extends Application {
    private static myApplication myApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        AVOSCloud.setDebugLogEnabled(true);
        LCChatKit.getInstance().init(getApplicationContext(), "F6aqhwy0aoGQK6gDK8DnNaCM-gzGzoHsz", "f8rtIF6jyhF4GpOrfaGtL7Tj");
        AVIMClient.setAutoOpen(false);
    }
    public static Context getApp() {
        return myApplication;
    }

}
