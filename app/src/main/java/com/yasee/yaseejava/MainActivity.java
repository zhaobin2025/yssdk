package com.yasee.yaseejava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.yasee.yasee.Notify;
import com.yasee.yasee.Yasee;
import com.yasee.yasee.core.configs.LogConfig;
import com.yasee.yasee.core.configs.SerialConfig;
import com.yasee.yasee.core.models.AdvertisementData;
import com.yasee.yasee.protocols.ble.BleDevice;
import com.yasee.yasee.core.enums.DeviceProcess;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.configs.BleConfig;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yaseejava.databinding.ActivityMainBinding;
import com.yasee.yaseejava.tools.PermissionHelper;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<BleDevice> binds = new ArrayList<>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private PermissionHelper permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Yasee.getSingle().setContext(getApplicationContext());
        Yasee.getSingle().logConfig = new LogConfig(true,true,true,true);

        permissionManager = new PermissionHelper(MainActivity.this);
        permissionManager.requestPermissions(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "所有权限已授予", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(String[] pps) {
                Toast.makeText(MainActivity.this, "以下权限被拒绝: " + pps, Toast.LENGTH_SHORT).show();
            }
        });
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Yasee.getSingle().setContext(getApplicationContext());
                Yasee.getSingle().bleConfig = new BleConfig(50);
                Yasee.getSingle().scan();
            }
        });

        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建PopupMenu
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenu().add("tmd");
                popupMenu.getMenu().add("hlw");
                popupMenu.getMenu().add("wl");
                popupMenu.getMenu().add("yc");
                // 设置菜单项的点击监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Fragment navHostFragment = getSupportFragmentManager().getFragments().get(0);

                        if (navHostFragment instanceof NavHostFragment) {
                            ScanFragment currentFragment = (ScanFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                            if (currentFragment != null) {
                                Log.d("CurrentFragment", "当前 Fragment 是: " + currentFragment.getClass().getSimpleName());
                                currentFragment.filter(item.getTitle());
                            }
                        }
                        return false;
                    }
                });

                // 显示PopupMenu
                popupMenu.show();
            }
        });


        Notify.getSingle().listen(_state);
        // 远程绑定设备
        binding.linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mac = binding.macInput.getText().toString();
                BleDevice bd =  new BleDevice(mac,"Y917-"+mac.substring(12), new AdvertisementData(new HashMap<>()));
                bd.connect();
            }
        });


        try {
            testSerial();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent _binds = new Intent(MainActivity.this, SerialActivity.class);
            startActivity(_binds);
            return true;
        } else if (id == R.id.action_masschip) { // 白细胞
            Intent _mc = new Intent(MainActivity.this, Masschip.class);
            startActivity(_mc);
            return true;
        } else if (id == R.id.action_ecg) { // ECG图
            Intent _mz = new Intent(MainActivity.this, MzActivity.class);
            startActivity(_mz);
            return true;
        } else if (id == R.id.action_ocr) { // OCR 扫描
            Intent _mz = new Intent(MainActivity.this, Ocr.class);
            startActivity(_mz);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    NotifyInterface _state = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceLink;
        }
        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleLink bleLink = (NotifyResp.BleLink) data.data;
            if (bleLink.process != DeviceProcess.linked) return;
            Bundle _bd = new Bundle();
            _bd.putString("mac",((BleDevice) ((NotifyResp.BleLink) data.data).device).getMac());
        }
    };


    /*
    * test serial
    * */
    void testSerial() throws IOException {

    }

}