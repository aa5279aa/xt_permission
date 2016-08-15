package com.xt.permission.permissionhepler;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wlzhao on 16/5/27.
 * Update by lxl on 16/08/15
 */
public class PermissionActivity extends Activity {
    private static Object lock = new Object();

    public static void startJobWithPermission(Context context, Runnable job, String... permissions) {
        startJobWithPermission(context, job, null, permissions);
    }

    public static void startJobWithPermission(Context context, Runnable job, PermissionListener listener, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || permissions == null) {
            actionForGranted(job, listener);
            return;
        }
        ArrayList<String> unAuthorities = getUnGrantedPermission(context, permissions);
        if (unAuthorities.isEmpty()) {
            actionForGranted(job, listener);
            return;
        }
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putStringArrayListExtra("permissions", unAuthorities);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        final Handler handler = new Handler();
        JobThread jobThread = JobThread.newInstance(context, job, handler, permissions);
        jobThread.start();
    }

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
                    List<String> unAuthorities = getUnGrantedPermission(context, permissions);
                    if (unAuthorities.isEmpty()) {
                        handler.post(job);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ArrayList<String> getUnGrantedPermission(Context context, String... permissions) {
        ArrayList<String> unAuthorities = new ArrayList<>();
        for (String permission : permissions) {
            int hasPermission = context.checkSelfPermission(permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                unAuthorities.add(permission);
            }
        }
        return unAuthorities;
    }

    private static void actionForGranted(Runnable job, PermissionListener listener) {
        if (job != null) {
            job.run();
        }
        if (listener != null) {
            listener.onGrantedPermission();
        }
    }

    private static void actionForDenied() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> permissions = getIntent().getStringArrayListExtra("permissions");
        String[] permissionArr = new String[permissions.size()];
        permissions.toArray(permissionArr);
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            Log.i("LXL", "Error");
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //this only requst and don't care whether first requst
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
    }

    private List<String> getCannotShowPermissionList(String[] permissions) {
        List<String> list = new ArrayList<String>();
        for (String permission : permissions) {
            final boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            if (!b) {
                list.add(permission);
            }
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            finish();
            synchronized (lock) {
                lock.notify();
            }
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final List<String> cannotShowPermissionList = getCannotShowPermissionList(permissions);
            if (cannotShowPermissionList.size() == 0) {
                //user select no but don't select not asking
                actionForDenied();
                Toast.makeText(this, "用户拒绝了权限申请", Toast.LENGTH_LONG).show();
            } else {
                //user select no and select not asking
                final StringBuilder unShowPermissionsMessage = PermissionUtil.getUnShowPermissionsMessage(cannotShowPermissionList);
                PermissionUtil.showMessage_GotoSetting(unShowPermissionsMessage.toString(), this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (lock) {
            lock.notify();
        }
    }
}
