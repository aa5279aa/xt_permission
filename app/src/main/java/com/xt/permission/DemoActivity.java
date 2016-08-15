package com.xt.permission;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.permission.permissionhepler.PermissionActivity;


public class DemoActivity extends Activity {

    TextView mWirteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWirteText = (TextView) findViewById(R.id.check_write);

        mWirteText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PermissionActivity.startJobWithPermission(DemoActivity.this, runnable, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //do action
            Toast.makeText(DemoActivity.this, "权限获取成功", Toast.LENGTH_SHORT).show();
        }
    };

}
