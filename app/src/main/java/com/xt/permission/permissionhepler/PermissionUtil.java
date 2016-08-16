package com.xt.permission.permissionhepler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangleiliu on 2016/8/15.
 */
public class PermissionUtil {


    public static void showGotoSetting(final Dialog dialog, final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public static PermissionSettingDialog.Builder createSettingDialog(final Activity act, String titile, String message, DialogInterface.OnClickListener clickListener) {
        PermissionSettingDialog.Builder builder = new PermissionSettingDialog.Builder(act);
        builder.setTitle(titile);
        builder.setMessage(message);
        builder.setPositiveButton("设置", clickListener);
        builder.setNegativeButton("取消", clickListener);
        return builder;
    }

    public static DialogInterface.OnClickListener getDefaultListener(final Activity act) {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss();
                    gotoPermissionSetting(act);
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.dismiss();
                }
            }
        };
    }

    //对小米的机型不适配，以后改进
    public static void gotoPermissionSetting(Activity act) {
        Uri packageURI = Uri.parse("package:" + act.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        act.startActivity(intent);
    }

    public static List<String> getCannotShowPermissionList(Activity act, List<String> permissions) {
        List<String> canShowList = new ArrayList<String>();
        for (String permission : permissions) {
            final boolean b = ActivityCompat.shouldShowRequestPermissionRationale(act, permission);
            if (!b) {
                canShowList.add(permission);
            }
        }
        return canShowList;
    }

    public static ArrayList<String> getUnGrantedPermission(Context context, String... permissions) {
        ArrayList<String> unAuthorities = new ArrayList<>();
        for (String permission : permissions) {
            int hasPermission = context.checkSelfPermission(permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                unAuthorities.add(permission);
            }
        }
        return unAuthorities;
    }

    public static StringBuilder getUnShowPermissionsMessage(String[] list) {
        StringBuilder message = new StringBuilder("您已关闭了");
        String permisson;
        boolean hasCALENDAR = false;
        boolean hasCAMERA = false;
        boolean hasCONTACTS = false;
        boolean hasLOCATION = false;
        boolean hasMICROPHONE = false;
        boolean hasPHONE = false;
        boolean hasSENSORS = false;
        boolean hasSMS = false;
        boolean hasSTORAGE = false;

        if (list.length == 1) {
            permisson = list[0];
            if (permisson.contains("CALENDAR")) {
                message.append("日历 ");
            } else if (permisson.contains("CAMERA")) {
                message.append("相机 ");

            } else if (permisson.contains("CONTACTS") || permisson.equals("android.permission.GET_ACCOUNTS")) {
                message.append("通讯录 ");

            } else if (permisson.contains("LOCATION")) {
                message.append("定位 ");

            } else if (permisson.equals("android.permission.RECORD_AUDIO")) {
                message.append("耳麦 ");

            } else if (permisson.contains("PHONE")
                    || permisson.contains("CALL_LOG")
                    || permisson.contains("ADD_VOICEMAIL")
                    || permisson.contains("USE_SIP")
                    || permisson.contains("PROCESS_OUTGOING_CALLS")) {
                message.append("电话 ");

            } else if (permisson.contains("BODY_SENSORS")) {
                message.append("身体传感 ");

            } else if (permisson.contains("SMS")
                    || permisson.contains("RECEIVE_WAP_PUSH")
                    || permisson.contains("RECEIVE_MMS")
                    || permisson.contains("READ_CELL_BROADCASTS")) {
                message.append("短信 ");

            } else if (permisson.contains("STORAGE")) {
                message.append("手机存储 ");

            }
        } else {
            for (int i = 0; i < list.length; i++) {
                permisson = list[i];
                if (permisson.contains("CALENDAR") && hasCALENDAR == false) {
                    message.append("日历");
                    hasCALENDAR = true;
                } else if (permisson.contains("CAMERA") && hasCAMERA == false) {
                    message.append("相机");
                    hasCAMERA = true;
                } else if (permisson.contains("CONTACTS")
                        || permisson.equals("android.permission.GET_ACCOUNTS")
                        && hasCONTACTS == false) {
                    message.append("通讯录");
                    hasCONTACTS = true;
                } else if (permisson.contains("LOCATION") && hasLOCATION == false) {
                    message.append("定位");
                    hasLOCATION = true;
                } else if (permisson.equals("android.permission.RECORD_AUDIO") && hasMICROPHONE == false) {
                    message.append("耳麦");
                    hasMICROPHONE = true;
                } else if (permisson.contains("PHONE")
                        || permisson.contains("CALL_LOG")
                        || permisson.contains("ADD_VOICEMAIL")
                        || permisson.contains("USE_SIP")
                        || permisson.contains("PROCESS_OUTGOING_CALLS") && hasPHONE == false) {
                    message.append("电话");
                    hasPHONE = true;
                } else if (permisson.contains("BODY_SENSORS") && hasSENSORS == false) {
                    message.append("身体传感");
                    hasSENSORS = true;
                } else if (permisson.contains("SMS")
                        || permisson.contains("RECEIVE_WAP_PUSH")
                        || permisson.contains("RECEIVE_MMS")
                        || permisson.contains("READ_CELL_BROADCASTS") && hasSMS == false) {
                    message.append("短信");
                    hasSMS = true;
                } else if (permisson.contains("STORAGE") && hasSTORAGE == false) {
                    message.append("手机存储");
                    hasSTORAGE = true;
                }
                if (i < list.length - 1) {
                    message.append(",");
                }
            }
        }

        message.append("访问权限，为了保证功能的正常使用，请前往系统设置页面开启");
        return message;
    }
}
