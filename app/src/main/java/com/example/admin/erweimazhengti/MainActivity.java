package com.example.admin.erweimazhengti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.erweimazhengti.utils.DensityUtil;
import com.example.admin.erweimazhengti.zxing.activity.CaptureActivity;
import com.example.admin.erweimazhengti.zxing.encoding.EncodingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btn_open;
    private Button btn_create;
    private ImageView iv_zxing;
    private TextView tv_result;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvent();
    }

    private void initViews() {
        btn_open = findViewById(R.id.btn_open);
        btn_create = findViewById(R.id.btn_create);
        iv_zxing = findViewById(R.id.iv_zxing);
        tv_result = findViewById(R.id.tv_result);
    }

    private void initEvent() {
        btn_open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                open();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        create();
                    }
                });

            }
        });
    }

    /**
     * 打开二维码扫描
     */
    private void open() {
        config();
        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);
    }

    /**
     * 提高屏幕亮度
     */
    private void config() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 创建二维码并将图片保存在本地
     */
    private void create() {
        int width = DensityUtil.dip2px(this, 200);
        Bitmap bitmap = EncodingUtils.createQRCode("http://www.baidu.com",   //todo 地址
                width, width, BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher));
        iv_zxing.setImageBitmap(bitmap);
        saveBitmap(bitmap);
    }

    /**
     * 将Bitmap保存在本地
     *
     * @param bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        // 首先保存图片      todo
        File appDir = new File(Environment.getExternalStorageDirectory(),
                "zxing_image");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "zxing_image" + ".jpg";   //todo
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + "/sdcard/namecard/")));  //todo
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            tv_result.setText(result);
        }
    }
}
