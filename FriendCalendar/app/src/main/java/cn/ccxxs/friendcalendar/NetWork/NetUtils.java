package cn.ccxxs.friendcalendar.NetWork;

/**
 * Created by ccxxs on 2017/5/26.
 */

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cn.ccxxs on 2017/5/23.
 */

public class NetUtils {
    public static String localhost = "http://10.0.2.2:8080/";
    public static String myhost = "http://106.14.138.105/";

    public static String loginUrl = myhost+"login";
    public static String SignUpUrl = myhost+"signup";
    public static String AddActivityUrl = myhost+"addActivity";
    public static String GetActivityUrl = myhost+"getActivity";
    public static String GetSActivityUrl = myhost+"getSActivity";
    public static String GetTokenUrl = myhost+ "token";
    public static String GetOTokenUrl = myhost+ "otoken";
    public static String UploadUrl = myhost+"upload";
    public static String UploadImgUrl = myhost+"uploadimg";
    public static String UpdateAvatar = myhost+"updateavatar";
    public static String UpdateImg = myhost+"updateimg";
    public static String GetAvatar = myhost+"getavatar";
    public static String GetOpenActivityUrl = myhost+"getOpenActivity";
    public static OkHttpClient okHttpClient = new OkHttpClient();
    static String getresp;
    static String postresp;
    static String login;

    public static String getLoginUrl() {
        return loginUrl;
    }

    public static String getSignUpUrl() {
        return SignUpUrl;
    }

    public static String getAddActivityUrl() {
        return AddActivityUrl;
    }

    public static String getGetActivityUrl() {
        return GetActivityUrl;
    }

    public static String GetTokenUrl() {
        return GetTokenUrl;
    }
    public static String GetOTokenUrl() {
        return GetOTokenUrl;
    }

    public static void Login(String url, String username,String password,Callback callback) {
        RequestBody requestBody = new FormBody.Builder().add("username",username).add("password",password).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void Signup(String url, String username,String password,String phone,Callback callback) {
        RequestBody requestBody = new FormBody.Builder().add("username",username).add("password",password).add("phone",phone).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public static void addActivity(String url,String userid,String title,String starttime,String end_time,String location, String content,String conversationid,String remindtime,String lat,String lng,String isprivate,Callback callback){
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).add("title",title).add("starttime",starttime).add("endtime",end_time).add("location",location).add("content",content).add("conversationid",conversationid).add("remindtime",remindtime).add("lat",lat).add("lng",lng).add("isprivate",isprivate).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void getActivity(String url,String userid,Callback callback){
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void getOpenActivity(String url,Callback callback){
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void getSActivity(String url,String userid,String activityid,Callback callback){
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).add("activityid",activityid).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void updateAvatar(String userid,String avatar,Callback callback) {
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).add("avatar",avatar).build();
        Request request = new Request.Builder().url(UpdateAvatar).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void updateActivityImg(String userid,String activityid,String img,Callback callback) {
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).add("activityid",activityid).add("img",img).build();
        Request request = new Request.Builder().url(UpdateImg).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void getAvatar(String userid,Callback callback){
        RequestBody requestBody = new FormBody.Builder().add("userid",userid).build();
        Request request = new Request.Builder().url(GetAvatar).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void download(final String url, final String saveDir) {
        Request request = new Request.Builder().url(url).build();
        Log.w("didnt", "initAvatar: File didn't exist"+saveDir);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[1024];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = new File(saveDir).getAbsolutePath();

                try {
                    is = response.body().byteStream();
                    File file = new File(savePath);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (Exception e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
    //上传文件
    public static void UploadFile(String url,File file){
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("application/octet-stream", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient  = httpBuilder
                //设置超时
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
            @Override
            public void onFailure(Call arg0, IOException e) {
            }

        });
    }


}
