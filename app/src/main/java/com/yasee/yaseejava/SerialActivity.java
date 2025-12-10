package com.yasee.yaseejava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.PopupMenu;

import com.yasee.yasee.Notify;
import com.yasee.yasee.Yasee;
import com.yasee.yasee.core.abstracts.AbstractsSerialActivity;
import com.yasee.yasee.core.configs.SerialConfig;
import com.yasee.yasee.core.enums.CmdType;
import com.yasee.yasee.core.enums.DeviceProcess;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.models.Check;
import com.yasee.yasee.core.models.Cmd;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yasee.protocols.serial.SerialDevice;
import com.yasee.yaseejava.databinding.ActivitySerialBinding;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SerialActivity extends AbstractsSerialActivity {

    private ActivitySerialBinding binding;
    private SerialDevice serialDevice = new SerialDevice("M10");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySerialBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        SerialDevice sd = new SerialDevice("M10");

        Yasee.getSingle().setSerialConfig(true, new SerialConfig());
        Notify.getSingle().listen(_ni);
        Notify.getSingle().listen(_niLink);


        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.serialDataShow.setText("");
            }
        });


        binding.serialT2o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(SerialActivity.this,v);
                Check check = new Check("三合一",1);
                List<Cmd> cmds = check.getCmds();
                cmds.forEach(cmd -> pm.getMenu().add(0,cmds.indexOf(cmd),0,cmd.desc));
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Cmd cmd = cmds.get(item.getItemId());
                        serialDevice.send(cmd.unsign,false);
                        return false;
                    }
                });
                pm.show();
//                send81();
            }
        });

        binding.serialBf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(SerialActivity.this,v);
                Check check = new Check("血脂",4);
                List<Cmd> cmds = check.getCmds();
                cmds.forEach(cmd -> pm.getMenu().add(0,cmds.indexOf(cmd),0,cmd.desc));
                pm.getMenu().add("对码步骤1");
                pm.getMenu().add("对码步骤2");

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("对码步骤1")) {
                            String hexString = "AA55C9840420000000C0420114081B010101010200011000089c00EB27C700569246CC0455C57b646044416045C100477ec800c2814666e2d1c50080954483c050c10000000000000000009040c600c2814666e2d1c566d604459a998fc2002c5946009871c69ab1ba4529dc3a43a167a33f0000000000000000cdccfdc31fd54a44c5202bc23d8ae3432bf6ef3fc3f566c30ad730442b87bbc233530044bf7d8d400000000000000000000F00140019001E0023000000000000000F00140019001E00230000000000003BCA";
                            // 步骤2: 创建byte数组
                            int length = hexString.length(); // 获取输入字符串的长度
                            byte[] byteArray = new byte[length / 2]; // 创建byte数组，长度为输入字符串长度的一半

                            // 步骤3: 转换字符并存储到byte数组中
                            for (int i = 0; i < length; i += 2) {
                                String subString = hexString.substring(i, i + 2); // 取出两个字符
                                byte byteValue = (byte) Integer.parseInt(subString, 16); // 转换为byte值
                                byteArray[i / 2] = byteValue; // 存储到byte数组中
                            }
                            serialDevice.send(byteArray,false);
                        } else if (item.getTitle().equals("对码步骤2")) {
                            String hexString = "AA55C984042100C000C0c1a8a4bc95d4c13fd04458bb05a38a3f9d80563ff4fdd43b933aa93ff4fd54bcfaed6bbc2d21af3fe02d10bb7424873f7dae463fbc74133c93a9a23fea9532bca69b44bc508da73f04e78c3bb3ea633f1058793f6f12833ac9e5cf3f849ecdbc849ecdbcc9e5cf3f6f12833a1058793fb3ea633f04e78c3b508da73fa69b44bcea9532bc93a9a23fbc74133c7dae463f7424873fe02d10bb2d21af3ffaed6bbcf4fd54bc933aa93ff4fdd43b9d80563f05a38a3fd04458bb95d4c13fc1a8a4bc6473";
                            // 步骤2: 创建byte数组
                            int length = hexString.length(); // 获取输入字符串的长度
                            byte[] byteArray = new byte[length / 2]; // 创建byte数组，长度为输入字符串长度的一半

                            // 步骤3: 转换字符并存储到byte数组中
                            for (int i = 0; i < length; i += 2) {
                                String subString = hexString.substring(i, i + 2); // 取出两个字符
                                byte byteValue = (byte) Integer.parseInt(subString, 16); // 转换为byte值
                                byteArray[i / 2] = byteValue; // 存储到byte数组中
                            }
                            serialDevice.send(byteArray,false);
                        }
                        Cmd cmd = cmds.get(item.getItemId());
                        serialDevice.send(cmd.unsign,false);
                        return false;
                    }
                });
                pm.show();
            }
        });

        binding.serialHc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu pm = new PopupMenu(SerialActivity.this,v);
                Check check = new Check("糖化",4);
                List<Cmd> cmds = check.getCmds();
                cmds.forEach(cmd -> pm.getMenu().add(0,cmds.indexOf(cmd),0,cmd.desc));
                pm.getMenu().add("糖化对码");

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("糖化对码")) {
                            String hexString = "AA5535850CD4CC092B30302E3030303134362D30302E3031333836302B30302E3539353930362D30342E3339373235383233313231320B5D";
                            // 步骤2: 创建byte数组
                            int length = hexString.length(); // 获取输入字符串的长度
                            byte[] byteArray = new byte[length / 2]; // 创建byte数组，长度为输入字符串长度的一半

                            // 步骤3: 转换字符并存储到byte数组中
                            for (int i = 0; i < length; i += 2) {
                                String subString = hexString.substring(i, i + 2); // 取出两个字符
                                byte byteValue = (byte) Integer.parseInt(subString, 16); // 转换为byte值
                                byteArray[i / 2] = byteValue; // 存储到byte数组中
                            }

                            serialDevice.send(byteArray,false);

                            return false;
                        }
                        Cmd cmd = cmds.get(item.getItemId());
                        serialDevice.send(cmd.unsign,false);
                        return false;
                    }
                });
                pm.show();


            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notify.getSingle().remove(_ni);
        Notify.getSingle().remove(_niLink);
    }

    void send83() {
//        SerialTool.getSingle().send(new byte[]{
//                (byte) 0xAA,
//                (byte) 0x55,
//                (byte) 0x04,
//                (byte) 0x83,
//                (byte) 0x01,
//                (byte) 0x00,
//                (byte) 0x84,
//        });
    }
    void send81() {
//        SerialTool.getSingle().send(new byte[]{
//           (byte) 0xAA, (byte) 0x55, (byte) 0x04, (byte) 0x81,
//           (byte) 0xFF, (byte) 0x01, (byte) 0x80,
//        });
    }

    NotifyInterface _ni = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceData;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleNotifyData ssss = (NotifyResp.BleNotifyData) data.data;

            // join [-86, 85, 8, -125, 1, 1, 1, 0, 16, 0, -106]
            if (ssss.step == CmdType.join)
                send83();
            else if (ssss.step == CmdType.wakeup || ssss.step == CmdType.serial_join)
                CompletableFuture.runAsync(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100); // 模拟延时
                        System.out.println("延时后执行任务");
                        send81();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });



            String _sss = "本次"; //binding.sendData.getText().toString();
            HashMap _s = (HashMap) ssss.data;
            String text = String.format("%s\n start============\n  指令类型:%s \n  指令可视化数据:%s\nend================\n", binding.serialDataShow.getText(),ssss.step.name(), _s==null ? "" : _s.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.serialDataShow.setText(text);
                }
            });
        }
    };

    NotifyInterface _niLink = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceLink;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleLink ssss = (NotifyResp.BleLink) data.data;
            binding.serialDataShow.setText(ssss.process == DeviceProcess.rwIsOk ? "连接成功" : "连接失败" );
        }
    };
}