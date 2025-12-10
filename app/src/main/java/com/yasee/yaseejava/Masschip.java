package com.yasee.yaseejava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yasee.yasee.Notify;
import com.yasee.yasee.Yasee;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.models.Check;
import com.yasee.yasee.core.models.Cmd;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yasee.core.models.ParmsModel;
import com.yasee.yasee.core.tools.Products;
import com.yasee.yasee.protocols.wifi.WifiDevice;
import com.yasee.yaseejava.databinding.ActivityMasschipBinding;

import java.util.List;

public class Masschip extends AppCompatActivity {

    private ActivityMasschipBinding binding;

    private WifiDevice wifiDevice = new WifiDevice("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Notify.getSingle().listen(_ni);
        Notify.getSingle().listen(_link);

        binding = ActivityMasschipBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());


        List<Check> checks = Products.supportChecks(wifiDevice);
        List<Cmd> cmds = checks.get(0).getCmds();


//        binding.masschipStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Check check = checks.get(0);
//                wifiDevice.send(check,cmds.get(0).id,new ParmsModel());
//            }
//        });
//        binding.masschipLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PlatformMasschip.getSingle().connect();
//            }
//
//        });
//        binding.masschipBattery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String deviceId = PlatformMasschip.getSingle().queryDeviceId();
//                Log.d("白细胞: 设备ID ", deviceId);
//            }
//
//        });
//        binding.masschipStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String state = PlatformMasschip.getSingle().queryState();
//                Log.d("白细胞: 状态 ", state);
//            }
//        });

        binding.masschipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check check = checks.get(0);
                ParmsModel pm = new ParmsModel();
                pm.wifiName = "EMASSCHIP-579A";
                wifiDevice.send(check,cmds.get(0).id,pm);
            }
        });
        binding.masschipReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check check = checks.get(0);
                wifiDevice.send(check,cmds.get(1).id,new ParmsModel());
            }
        });
        binding.masschipClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.masschipResult.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notify.getSingle().remove(_ni);
        Notify.getSingle().remove(_link);
    }

    NotifyInterface _ni = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceData;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleNotifyData ssss = (NotifyResp.BleNotifyData) data.data;
            String _s = ssss.dataToJson();
            String text = String.format("%s\n start============\n设备:%s\n步骤:%s\n指令可视化数据:%s\nend================\n", binding.masschipResult.getText(), ((NotifyResp.BleNotifyData<?, ?>) data.data).device.getModel(),((NotifyResp.BleNotifyData<?, ?>) data.data).step.name(), _s);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.masschipResult.setText(text);
                }
            });
        }
    };


    NotifyInterface _link = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceLink;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleLink ssss = (NotifyResp.BleLink) data.data;
            String text = String.format("%s\n start============\n设备:%s\n步骤:%s\n指令可视化数据:%s\nend================\n", binding.masschipResult.getText(), ssss.device.getModel(),"连接状态", ssss.process.name());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.masschipResult.setText(text);
                }
            });
        }
    };


}