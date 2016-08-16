package com.xt.permission.permissionhepler;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by wlzhao on 16/5/27.
 * Update by lxl on 16/08/15
 */
public class PermissionActivity extends Activity {
    private static Object lock = new Object();
    private static ArrayList<PermissionRequestSetting> listenerList = new ArrayList<PermissionRequestSetting>();

//    //方式一不关心返回值
//    public static void startJobWithPermission(Context context, Runnable job, String... permissions) {
//        startJobWithPermission(context, job, null, -1, permissions);
//    }

    //方式二只关心回调
    public static void startJobWithPermission(Context context, PermissionListener listener, int requestCode, String... permissions) {
        startJobWithPermission(context, listener, requestCode, true, null, permissions);
    }

    public static void startJobWithPermission(Context context, PermissionListener listener, int requestCode, boolean showSettingDialog, String... permissions) {
        startJobWithPermission(context, listener, requestCode, showSettingDialog, null, permissions);
    }

    public static void startJobWithPermission(Context context, PermissionListener listener, int requestCode, boolean showSettingDialog, Dialog settingDialog, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || permissions == null) {
            checkResultForGranted(permissions, listener, requestCode);
            return;
        }
        ArrayList<String> unAuthorities = PermissionUtil.getUnGrantedPermission(context, permissions);
        if (unAuthorities.isEmpty()) {
            checkResultForGranted(permissions, listener, requestCode);
            return;
        }

        //以上的请求不需要加入队列
        if (listener != null) {
            PermissionRequestSetting permissionSetting = new PermissionRequestSetting();
            permissionSetting.requestCode = requestCode;
            permissionSetting.permissions = permissions;
            permissionSetting.listener = listener;
            listenerList.add(permissionSetting);
        } else {
//            final Handler handler = new Handler();
//            JobThread jobThread = JobThread.newInstance(context, job, handler, permissions);
//            jobThread.start();
        }

        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putStringArrayListExtra("permissions", unAuthorities);
        intent.putExtra("requestCode", requestCode);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private static void checkResultForGranted(String[] permissions, PermissionListener listener, int requestCode) {
        if (listener != null) {
            listener.onGrantedPermission(permissions, requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> permissions = getIntent().getStringArrayListExtra("permissions");
        int requestCode = getIntent().getExtras().getInt("requestCode");
        String[] permissionArr = new String[permissions.size()];
        permissions.toArray(permissionArr);
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            finish();
        }
        //this only requst and don't care whether first requst
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<String>();
        List<String> grantedPermissions = new ArrayList<String>();
        //因为requestCode可能存在重复，重复的申请permissions不一样，所以这里不使用grantResults
        final Iterator<PermissionRequestSetting> iterator = listenerList.iterator();
        while (iterator.hasNext()) {
            final PermissionRequestSetting next = iterator.next();
            if (next.requestCode != requestCode) {
                continue;
            }
            //remove listener setting
            iterator.remove();
            deniedPermissions.clear();
            grantedPermissions.clear();
            final PermissionListener listener = next.listener;
            for (String permission : next.permissions) {
                //这里不需要再次验证的
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(permission);
                } else {
                    deniedPermissions.add(permission);
                }
            }
            //回调
            if (deniedPermissions.size() == 0) {
                listener.onGrantedPermission(grantedPermissions.toArray(permissions), requestCode);
                continue;
            }
            final List<String> cannotShowPermissionList = PermissionUtil.getCannotShowPermissionList(this, deniedPermissions);
            if (cannotShowPermissionList.size() == 0) {
                //user select no but don't select not asking
                listener.onDeniedPermission(grantedPermissions.toArray(permissions), deniedPermissions.toArray(permissions), requestCode);
            } else {
                listener.onNoShowPermission(grantedPermissions.toArray(permissions), deniedPermissions.toArray(permissions), cannotShowPermissionList.toArray(permissions), requestCode);
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    static class PermissionRequestSetting {
        PermissionListener listener;
        String[] permissions;
        int requestCode;
    }


    //in first version,don't use this class
    private static class JobThread extends Thread {
        WeakReference<Context> weak;
        Runnable job;
        String[] permissions;
        Handler handler;

        public static JobThread newInstance(Context context, Runnable runnable, Handler handler, String... permissions) {
            JobThread jobThread = new JobThread();
            jobThread.weak = new WeakReference<Context>(context);
            jobThread.job = runnable;
            jobThread.permissions = permissions;
            jobThread.handler = handler;
            return jobThread;
        }

        public void run() {
            synchronized (lock) {
                try {
                    lock.wait();
                    final Context context = weak.get();
                    if (context == null) {
                        return;
                    }
                    List<String> unAuthorities = PermissionUtil.getUnGrantedPermission(context, permissions);
                    if (unAuthorities.isEmpty()) {
                        handler.post(job);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
