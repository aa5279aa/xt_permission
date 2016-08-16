package com.xt.permission.permissionhepler;

/**
 * Created by xiangleiliu on 2016/8/15.
 */
public interface PermissionListener {

    /**
     * DENIED
     */
    public void onDeniedPermission(String[] grantedPermissions, String[] deniedPermissions, int requestId);


    /**
     * GRANTED
     */
    public void onGrantedPermission(String[] grantedPermissions, int requestId);


    /**
     * NOSHOW
     */
    public void onNoShowPermission(String[] grantedPermissions, String[] deniedPermissions, String[] noShowPermissions, int requestId);

}
