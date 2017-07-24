package cn.ccxxs.friendcalendar;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by ccxxs on 2017/6/11.
 */

public class Constants {
    public static AVIMClient avimClient;
    public static int userid;
    public static String username;
    public static List<TheActivity> activityListConstant;
    public static String Avatar;

    public static String  getAvatar(){
        NetUtils.getAvatar(Integer.toString(userid), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonArray jsonA = (JsonArray)(new JsonParser().parse(respdata));
                if (jsonA.size()>0){
                    JsonObject jsonO = (JsonObject) jsonA.get(0);
                    if (jsonO.get("avatar")!= null){
                        Avatar = jsonO.get("avatar").getAsString();
                    }
                }
            }
        });
        return Avatar;
    }
}
