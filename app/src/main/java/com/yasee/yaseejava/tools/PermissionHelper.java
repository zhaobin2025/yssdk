package com.yasee.yaseejava.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private final Activity activity;
    private PermissionCallback callback;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    // 🚀 需要请求的权限（根据系统版本适配）
    public String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();

        // 📌 蓝牙权限（Android 12+ 需要 BLUETOOTH_SCAN 和 BLUETOOTH_CONNECT）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+ (API 31+)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            permissions.add(Manifest.permission.BLUETOOTH);
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        // 📌 Wi-Fi 权限（Android 13+ 需要 NEARBY_WIFI_DEVICES）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33+)
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+ (API 29+)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION); // Wi-Fi 扫描需要
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // 🚀 其他常用权限
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE);

        // 文件权限
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        return permissions.toArray(new String[0]);
    }

    // 🚀 检查是否已授予所有权限
    public boolean hasAllPermissions() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 🚀 请求权限
    public void requestPermissions(@NonNull PermissionCallback callback) {
        this.callback = callback;
        String[] permissions = getRequiredPermissions();
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            callback.onPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    // 🚀 处理权限请求回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied(permissions);
            }
        }
    }

    // 🚀 回调接口
    public interface PermissionCallback {
        void onPermissionGranted();  // 权限全部允许
        void onPermissionDenied(String[] permissions);   // 有权限被拒绝
    }
}