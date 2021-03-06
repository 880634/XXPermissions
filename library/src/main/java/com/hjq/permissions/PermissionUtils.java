package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.R.attr.permission;

/**
 * Created by HJQ on 2018-6-15.
 */
public final class PermissionUtils {

    /**
     * 不能被实例化
     */
    private PermissionUtils() {}

    /**
     * 跳转到应用权限设置页面
     *
     * @param context           上下文对象
     */
    public static void gotoPermissionSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //创建一个新栈存放，用户点击授予权限，会导致返回后不会重新创建Activity
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param context           上下文对象
     * @param permissions       需要请求的权限组
     */
    public static boolean isHasPermission(Context context, String... permissions) {
        return getFailPermissions(context, permissions).isEmpty();
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    public static String[] getPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions    需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, String[] permissions) {

        //如果是安卓6.0以下版本就返回一个长度为零的数组
        if(!PermissionUtils.isOverMarshmallow()) {
            return new ArrayList<>();
        }

        ArrayList<String> failPermissions = new ArrayList<>();
        for (String permission : permissions) {
            //把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                failPermissions.add(permission);
            }
        }
        return failPermissions;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

            //把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions;
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity                  Activity对象
     * @param requestPermissions        请求的权限组
     */
    static void checkPermissions(Activity activity, String[] requestPermissions) {
        String[] permissions = PermissionUtils.getPermissions(activity);
        if (permissions != null && permissions.length != 0) {
            List<String> manifest = Arrays.asList(permissions);
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new IllegalArgumentException(permission + ": Permissions are not registered in the manifest file");
                }
            }
        }else {
            throw new IllegalArgumentException("Permissions are not registered in the manifest file");
        }
    }
}
