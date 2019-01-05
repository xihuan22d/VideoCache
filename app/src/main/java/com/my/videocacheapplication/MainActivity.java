package com.my.videocacheapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_WRITE = 1;//申请权限的请求码
    private Button buttonTest;
    private TextView textView;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {
        buttonTest = (Button) findViewById(R.id.buttonTest);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE);
                    } else {
                        doTest();
                    }
                } else {
                    doTest();
                }
            }
        });
    }


    private void doTest() {
        String proxyUrl = TheApplication.getProxy().getProxyUrl("http://ossassets.oss-cn-hangzhou.aliyuncs.com/test/1.mp4?" + System.currentTimeMillis());
        String proxyUrl2 = TheApplication.getProxy().getProxyUrl("https://ossassets.oss-cn-hangzhou.aliyuncs.com/test/1.mp4?" + System.currentTimeMillis());
        HttpTool.httpDownTest(buttonTest, textView, MainActivity.this, proxyUrl);
        HttpTool.httpDownTest(buttonTest, textView1, MainActivity.this, proxyUrl2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doTest();
        }
    }
}
