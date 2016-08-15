package com.xt.permission.permissionhepler;

/**
 * Created by xiangleiliu on 2016/8/15.
 */
public interface PermissionListener {


    /**
     * DENIED
     */
    public void onDeniedPermission();

    /**
     * GRANTED
     */
    public void onGrantedPermission();

    /**
     * NOSHOW
     */
    public void onNoShowPermission();

}
