package com.kailang.bubblechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.protobuf.ByteString;
import com.hb.dialog.dialog.LoadingDialog;
import com.kailang.bubblechat.network.client.NettyClient;
import com.kailang.bubblechat.network.codec.ChatMessage;
import com.kailang.bubblechat.service.ClientService;
import com.kailang.bubblechat.utils.LoginUtil;
import com.wildma.pictureselector.FileUtils;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {
    private ImageView ivHead;
    private EditText etName;
    private Button btLogin, btChose;
    private byte[] icon;
    private LoadingDialog loadingDialog;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ivHead = findViewById(R.id.login_iv_head);
        etName = findViewById(R.id.login_et_name);
        btChose = findViewById(R.id.login_bt_chose);
        btLogin = findViewById(R.id.login_bt_login);

        //startService(new Intent(this, ClientService.class));
        //ClientService clientService = new ClientService();

        Glide.with(this).load(R.mipmap.ic_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(ivHead);


        btChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector
                        .create(LoginActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                        .selectPicture(true, 150, 150, 1, 1);
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = etName.getText().toString().trim();

                if (userName != null && !userName.isEmpty()) {

                    loadingDialog = new LoadingDialog(LoginActivity.this);
                    loadingDialog.setMessage("loading");
                    loadingDialog.setCancelable(false);//点击其他地方弹窗不消失
                    loadingDialog.show();
                    final int userID = LoginUtil.getUUID(userName);

                    if(icon==null){
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_image);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        icon = baos.toByteArray();
                    }

                    Intent intent = new Intent(LoginActivity.this,ClientService.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("userID",userID);
                    intent.putExtra("userIcon",icon);
                    startService(intent);

                    runClient(1, userID, userName);
                } else {
                    Toast.makeText(LoginActivity.this, "非法用户名", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void runClient(final int times, final int userID, final String userName) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {

                } else if (times <= 2) {
                    loadingDialog.setMessage("正尝试第" + times + "次连接服务器");
                    runClient(times + 1, userID, userName);
                } else if (times > 2) {
                    loadingDialog.setMessage("服务器可能被干掉了？");
                    //Log.e("xxxx", NettyClient.getInstance().isConnectSuccess() + " ");
                    isLogin = true;
                    loadingDialog.cancel();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else loadingDialog.cancel();
            }
        }, times * 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
                if (pictureBean.isCut()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(pictureBean.getPath());

                    //head.setImageBitmap(bitmap);
                    Glide.with(this).load(bitmap)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(ivHead);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    icon = baos.toByteArray();
                } else {
                    ivHead.setImageURI(pictureBean.getUri());
                }
                FileUtils.deleteAllCacheImage(this);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}