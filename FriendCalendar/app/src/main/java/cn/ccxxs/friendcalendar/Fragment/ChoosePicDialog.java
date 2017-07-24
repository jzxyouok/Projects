package cn.ccxxs.friendcalendar.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.MainActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import cn.ccxxs.friendcalendar.Setting.SettingsActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static cn.ccxxs.friendcalendar.Constants.userid;

/**
 * Created by ccxxs on 2017/6/10.
 */

public class ChoosePicDialog extends BottomSheetDialogFragment {
    @Bind(R.id.pic_camera)
    TextView pic_camera;
    @Bind(R.id.pic_gallary)
    TextView pic_gallary;
    int FLAG = 0;
    static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpeg";
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    Handler mhandler;
    public ChoosePicDialog() {
    }
    public static ChoosePicDialog getInstance() {
        return new ChoosePicDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_choosepic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.pic_camera)
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory()+File.separator+"avatar.jpg")));
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.pic_gallary)
    public void openGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri avatarUri = data.getData();
                    cropPhoto(avatarUri);
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/Pictrues/avatar.jpg");
                    cropPhoto(Uri.fromFile(temp));
                }
                break;
            case 3:
                if(imageUri != null){
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        File file = savePicture(bitmap);
                        //上传图片
                        NetUtils.UploadFile(NetUtils.UploadUrl,file);
                        //更新数据库头像地址
                        NetUtils.updateAvatar(Integer.toString(userid), NetUtils.myhost + "avatar/" +userid + ".jpg", new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
//                                Toast.makeText(getContext(),"更新头像成功",Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                mhandler = new Handler(Looper.getMainLooper());
                                mhandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity mainActivity = (MainActivity) MainActivity.main;
                                        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                                        mainActivity.refreshAvatar();
                                        settingsActivity.refreshAvatar();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
//        intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, 3);
    }


    public File savePicture(Bitmap bitmap) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/FriendCalendar");
        if (!dir.exists()){
            dir.mkdirs();
        }
        String filepath = Environment.getExternalStorageDirectory() + "/FriendCalendar/"+userid+".jpg";
        File file = new File(filepath);
        Log.w("filename", "savePicture: "+file.getName() );
        String path = file.getAbsolutePath();
        if (file.isFile() && file.exists()){
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filepath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
        FLAG = 1;
        return file;
    }

}
