package com.yasee.yaseejava.tools;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AndroidPermissionManager {
    private final Activity activity;
    private final PermissionCallback callback;
    private final ActivityResultLauncher<String[]> permissionLauncher;
    private final ActivityResultLauncher<Intent> settingsLauncher;

    public interface PermissionCallback {
        void onPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
    }

    public AndroidPermissionManager(AppCompatActivity activity, PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;

        // 初始化权限申请
        permissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::handlePermissionResult
        );

        // 初始化系统设置启动器
        settingsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkPermissions() // 返回后重新检查权限
        );
    }

    /** 检查是否已授予权限 */
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    /** 申请所有权限 */
    public void requestPermissions() {
        List<String> permissions = new ArrayList<>();

        // 1️⃣ 位置权限
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // 2️⃣ 蓝牙权限（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        // 3️⃣ 申请前台权限
        if (!permissions.isEmpty()) {
            permissionLauncher.launch(permissions.toArray(new String[0]));
        } else {
            requestBackgroundLocation(); // 直接申请后台权限（如果前台权限已授予）
        }
    }

    /** 处理权限申请结果 */
    private void handlePermissionResult(Map<String, Boolean> results) {
        List<String> deniedPermissions = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            if (!entry.getValue()) {
                deniedPermissions.add(entry.getKey());
            }
        }

        if (deniedPermissions.isEmpty()) {
            requestBackgroundLocation(); // 前台权限授予后申请后台权限
        } else {
            callback.onPermissionsDenied(deniedPermissions);
        }
    }

    /** 申请后台位置权限 */
    private void requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION});
            }, 500);
        } else {
            callback.onPermissionsGranted();
        }
    }

    /** 处理 "不再询问" 情况 */
    public void showSettingsDialog() {
        Toast.makeText(activity, "请在设置中手动开启权限", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        settingsLauncher.launch(intent);
    }

    /** 检查权限是否已全部授予 */
    private void checkPermissions() {
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requiredPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (!hasPermission(permission)) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.isEmpty()) {
            callback.onPermissionsGranted();
        } else {
            callback.onPermissionsDenied(deniedPermissions);
        }
    }
}