package com.seu.smarthome.ui.intro;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.seu.smarthome.R;
import com.seu.smarthome.model.User;
import com.seu.smarthome.ui.base.BaseActivity;
import com.seu.smarthome.util.BitmapUtils;
import com.seu.smarthome.util.LogUtils;
import com.seu.smarthome.util.OkHttpUtils;
import com.seu.smarthome.util.StrUtils;
import com.seu.smarthome.widgt.WSwitch;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyEditInfo extends BaseActivity {
    private static final String TAG = "AtyEditInfo";

    public static final String INTENT_EDIT = "intent_edit";
    public static final String INTENT_INFO = "intent_info";
    private boolean mEdit;
    private User mUser;
    //private boolean

    private static final int REQUEST_IMAGE = 0xef;
    private final int REQUEST_CROP = 400;

    private String mAvatarPath;
    private Bitmap avatarBitmap;

    SimpleDraweeView mDrawAvatar;
    EditText etName;
    WSwitch swGender;
    EditText etPhone;
    EditText etWeChat;
    EditText etQQ;
    TextView etCommit;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_editinfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_editinfo_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        bindViews();

        /*mEdit = getIntent().getBooleanExtra(INTENT_EDIT,false);
        if(mEdit){
            String info = getIntent().getStringExtra(INTENT_INFO);
            try {
                mUser = User.fromJSON(new JSONObject(info));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showUserInfo();
        }*/
    }

    private void bindViews(){
        mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_editinfo_avatar);
        etName = (EditText) findViewById(R.id.aty_editinfo_edittext_name);
        swGender = (WSwitch) findViewById(R.id.aty_editinfo_switch_gender);
        etPhone = (EditText) findViewById(R.id.aty_editinfo_phone);
        etWeChat = (EditText) findViewById(R.id.aty_editinfo_wechat);
        etQQ = (EditText) findViewById(R.id.aty_editinfo_qq);
        etCommit = (TextView) findViewById(R.id.aty_editinfo_commit);


        mDrawAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AtyEditInfo.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        etCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void showUserInfo(){
        mDrawAvatar.setImageURI(Uri.parse(StrUtils.thumForID(mUser.ID + "")));
        etName.setText(mUser.username);
        boolean male = getResources().getString(R.string.male).equals(mUser.gender);
        swGender.setOn(true);
        etPhone.setText(mUser.phone);
        etWeChat.setText(mUser.wechat);
        etQQ.setText(mUser.qq);
    }

    private void commit(){
        //LogUtils.d(TAG, swGender.isChecked()+"");
        if(etName.getText().toString().length()==0){
            makeToast(R.string.name_not_empty);
            return;
        }
        if(etPhone.getText().toString().length()==0){
            makeToast(R.string.phone_not_empty);
            return;
        }

        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("name", etName.getText().toString());
        param.put("gender", swGender.isOn() ? getResources().getString(R.string.male) : getResources().getString(R.string.female));
        param.put("phone", etPhone.getText().toString());
        if(etQQ.getText().length()!=0){
            param.put("qq",etQQ.getText().toString());
        }
        if(etWeChat.getText().length()!=0){
            param.put("wechat", etWeChat.getText().toString());
        }
        mProgressDialog = ProgressDialog.show(AtyEditInfo.this, null, getResources().getString(R.string.committing));
        OkHttpUtils.post(StrUtils.EDIT_PROFILE_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyEditInfo.this, s);
                if (j == null) {
                    return;
                }
                if (mAvatarPath == null) {
                    uploadImageReturned();
                } else {
                    uploadAvatar();
                }
            }
        });
    }

    private void uploadAvatar(){
        ArrayMap<String,String> p = new ArrayMap<>();
        p.put("token",StrUtils.token());
        p.put("type","0");
        p.put("number","0");
        OkHttpUtils.uploadBitmap(StrUtils.UPLOAD_AVATAR_URL, p, avatarBitmap, StrUtils.MEDIA_TYPE_IMG, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                uploadImageReturned();
            }

            @Override
            public void onResponse(String s) {
                uploadImageReturned();
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.evictFromCache(Uri.parse(StrUtils.thumForID(mUser.ID + "")));
            }
        });
    }
    private void uploadImageReturned(){
        if(mEdit){
            finish();
        }else {
            Intent i = new Intent(AtyEditInfo.this, AtyLogin.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(AtyLogin.INTENT_CLEAR, true);
            startActivity(i);
        }
    }

    private void makeToast(int string_id){
        Toast.makeText(this, string_id,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_IMAGE){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mAvatarPath = paths.get(0);
            performCrop(mAvatarPath);
            //mDrawAvatar.setImageURI(Uri.parse("file://"+mAvatarPath));
        } else if (requestCode == REQUEST_CROP){
            Bundle extras = data.getExtras();
            avatarBitmap = extras.getParcelable("data");
            mDrawAvatar.setImageBitmap(BitmapUtils.roundBitmap(avatarBitmap));

        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
