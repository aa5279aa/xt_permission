package com.xt.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xt.permission.permissionhepler.PermissionActivity;
import com.xt.permission.permissionhepler.PermissionListener;
import com.xt.permission.permissionhepler.PermissionSettingDialog;
import com.xt.permission.permissionhepler.PermissionUtil;


public class DemoActivity extends Activity {

    Activity context;
    final int RequestIdWriteStorage = 1001;
    final int RequestIdCamera = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        findViewById(R.id.check_write).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PermissionActivity.startJobWithPermission(DemoActivity.this, new PermissionListener() {

                    @Override
                    public void onDeniedPermission(String[] grantedPermissions, String[] deniedPermissions, int requestId) {
                        Toast.makeText(context, "onDeniedPermission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGrantedPermission(String[] grantedPermissions, int requestId) {
                        Toast.makeText(context, "onGrantedPermission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNoShowPermission(String[] grantedPermissions, String[] deniedPermissions, String[] noShowPermissions, int requestId) {
                        if (noShowPermissions.length > 0) {
                            final StringBuilder unShowPermissionsMessage = PermissionUtil.getUnShowPermissionsMessage(noShowPermissions);
                            final DialogInterface.OnClickListener defaultListener = PermissionUtil.getDefaultListener(context);
                            final PermissionSettingDialog.Builder builder = PermissionUtil.createSettingDialog(context, "权限设置", unShowPermissionsMessage.toString(), defaultListener);
                            final PermissionSettingDialog permissionSettingDialog = builder.create();
                            PermissionUtil.showGotoSetting(permissionSettingDialog, context);
                        }
                    }

                }, RequestIdWriteStorage, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });

        findViewById(R.id.check_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

    }

    PermissionListener permissionListener = new PermissionListener() {

        @Override
        public void onDeniedPermission(String[] grantedPermissions, String[] deniedPermissions, int requestId) {
            Toast.makeText(context, "onDeniedPermission", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGrantedPermission(String[] grantedPermissions, int requestId) {
            Toast.makeText(context, "onGrantedPermission", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoShowPermission(String[] grantedPermissions, String[] deniedPermissions, String[] noShowPermissions, int requestId) {
            Toast.makeText(context, "onNoShowPermission", Toast.LENGTH_SHORT).show();
        }

    };
}
