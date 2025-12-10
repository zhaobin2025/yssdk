package com.yasee.yaseejava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yasee.yasee.core.enums.CmdType;
import com.yasee.yasee.core.models.ParmsModel;
import com.yasee.yasee.core.models.Check;
import com.yasee.yasee.core.models.Cmd;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yasee.Notify;
import com.yasee.yasee.core.tools.Products;
import com.yasee.yasee.protocols.ble.BleDevice;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yaseejava.adapters.ChecksAda;
import com.yasee.yaseejava.databinding.BleInfoBinding;

import java.util.HashMap;
import java.util.List;

public class BleInfo extends Fragment {

    private BleInfoBinding binding;

    ///  是不是展示脉诊
    private boolean _canMz = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = BleInfoBinding.inflate(inflater, container, false);

        Notify.getSingle().listen(_ni);
        Notify.getSingle().listen(_link);
        Notify.getSingle().listen(_history);

        binding.closeBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Devices.getSingle().delWithMac(BleDevice.current.getMac());
                if (BleDevice.current.state == 0) {
                    BleDevice.current.connect();
                } else {
                    binding.closeBle.setText("未连接");
                    BleDevice.current.disConnect(true);
                }
            }
        });

        binding.clearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sendData.setText("");
                binding.ecgCun.clean();
                binding.ecgGuan.clean();
                binding.ecgChi.clean();
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Check> ccs = Products.supportChecks(BleDevice.current);

        // 是否展示 脉诊信息UI
        for (Check cc : ccs) {
            if (cc.name.equals("脉诊")) { _canMz = true; }
        }


        binding.deviceName.append(BleDevice.current.getModel());
        binding.ecgView.setVisibility( _canMz ? View.VISIBLE : View.GONE);
        binding.supportsCheck.setAdapter(new ChecksAda(getActivity(), ccs));
        binding.supportsCheck.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private int _last = -1;
            private int _step = 0;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Check cc = ccs.get(position);
                Log.e("bledemo==cc", cc.name+"==="+cc.handwareCode);

                List<Cmd> cmds = cc.getCmds();

                // 创建PopupMenu
                PopupMenu popupMenu = new PopupMenu(BleInfo.this.getContext(), view);

                // 添加菜单项
                cmds.forEach(cmd -> {
                    Log.e("bledemo==cmd", cmd.desc+"==="+cmds.indexOf(cmd));
                    popupMenu.getMenu().add(0,cmds.indexOf(cmd),0,cmd.desc);
                });
                if (cc.name.equals("心电")) {
                    popupMenu.getMenu().add("30秒");
                    popupMenu.getMenu().add("1分钟");
                    popupMenu.getMenu().add("24小时");
                    popupMenu.getMenu().add("结束1分钟");
                } else if (cc.name.equals("血脂")) {
                    popupMenu.getMenu().add("对码步骤1");
                    popupMenu.getMenu().add("对码步骤2");
                } else if (cc.name.equals("糖化血红蛋白")) {
                    popupMenu.getMenu().add("糖化对码");
                }
                else if (cc.name.equals("同步&删除")) {
                    // 步数 0, 睡眠 1,心率 2, 血压 3, 血氧 呼吸率 hrv 温度 血糖 4, 运动模式历史数据 5,后台提醒记录数据 6
                    popupMenu.getMenu().add("步数(history)");
                    popupMenu.getMenu().add("睡眠(history)");
                    popupMenu.getMenu().add("心率(history)");
                    popupMenu.getMenu().add("血压(history)");
                    popupMenu.getMenu().add("血氧呼吸率hrv温度血糖");
                    popupMenu.getMenu().add("运动模式历史数据");
                }


                // 设置菜单项的点击监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ParmsModel parms = new ParmsModel();
                        Log.e("bledemo==item.id", item.getItemId()+"====");

                        if (item.getTitle().equals("对码步骤1")) {
//                            String hexString = "AA55C9840420000000C0420114081B010101010200011000089c00EB27C700569246CC0455C57b646044416045C100477ec800c2814666e2d1c50080954483c050c10000000000000000009040c600c2814666e2d1c566d604459a998fc2002c5946009871c69ab1ba4529dc3a43a167a33f0000000000000000cdccfdc31fd54a44c5202bc23d8ae3432bf6ef3fc3f566c30ad730442b87bbc233530044bf7d8d400000000000000000000F00140019001E0023000000000000000F00140019001E00230000000000003BCA";
                            String hexString = "AA55C9840420000000C0420A180C020C01010101000010002499006B47C70092C846661A87C5F6984944560E4FC1002203C800B656473327E9C50A77734489413AC1000000000000000033C7ADC500A8AF45003088C48FD27344EC51EFC15C0FB5C39A5D9EC59ACDA945AE279EC323DBE24100000000000000000080ABC466EE2E45CD6CB1C49AB93844AAF1BEC185AB17C49AD99144E13A9BC3CD6CE643A779A1400000000000000000000F00130018001E0023000000000000000F00130018001E00230000000000003F54";
                            // 步骤2: 创建byte数组
                            int length = hexString.length(); // 获取输入字符串的长度
                            byte[] byteArray = new byte[length / 2]; // 创建byte数组，长度为输入字符串长度的一半

                            // 步骤3: 转换字符并存储到byte数组中
                            for (int i = 0; i < length; i += 2) {
                                String subString = hexString.substring(i, i + 2); // 取出两个字符
                                byte byteValue = (byte) Integer.parseInt(subString, 16); // 转换为byte值
                                byteArray[i / 2] = byteValue; // 存储到byte数组中
                            }
                            BleDevice.current.send(byteArray,false);
                        } else if (item.getTitle().equals("对码步骤2")) {
//                            String hexString = "AA55C984042100C000C0c1a8a4bc95d4c13fd04458bb05a38a3f9d80563ff4fdd43b933aa93ff4fd54bcfaed6bbc2d21af3fe02d10bb7424873f7dae463fbc74133c93a9a23fea9532bca69b44bc508da73f04e78c3bb3ea633f1058793f6f12833ac9e5cf3f849ecdbc849ecdbcc9e5cf3f6f12833a1058793fb3ea633f04e78c3b508da73fa69b44bcea9532bc93a9a23fbc74133c7dae463f7424873fe02d10bb2d21af3ffaed6bbcf4fd54bc933aa93ff4fdd43b9d80563f05a38a3fd04458bb95d4c13fc1a8a4bc6473";
                            String hexString = "AA55C984042100C000C0006F01BDE10BD33F52491DBA68227C3F865A53BC8716A93F0AD723BBD7347F3FD7346FBC091BB63F8C4AEABCDC46DB3FD044D8BBBDE3943FE02D903B8A1F533FE71DA7BC17B7B93F6519E2BB7B14963FED0DBEBBDE71923F89D25E3C0C020B3FF5B9DABC8FE4CA3F75021ABC567D9E3F96438BBCC74BB73F99BB16BC5C20993F05A392BC075FC03F6519E2BC3789D93F62A156BC516BAA3FB37B72BB764F863F7CF230BC6A4DA33FC6DC35BCBF0EA43FA69B44BBDCD7893FA69B44BB6EA3893F5E1E";
                            // 步骤2: 创建byte数组
                            int length = hexString.length(); // 获取输入字符串的长度
                            byte[] byteArray = new byte[length / 2]; // 创建byte数组，长度为输入字符串长度的一半

                            // 步骤3: 转换字符并存储到byte数组中
                            for (int i = 0; i < length; i += 2) {
                                String subString = hexString.substring(i, i + 2); // 取出两个字符
                                byte byteValue = (byte) Integer.parseInt(subString, 16); // 转换为byte值
                                byteArray[i / 2] = byteValue; // 存储到byte数组中
                            }
                            BleDevice.current.send(byteArray,false);

                        }
                        else if (item.getTitle().equals("30秒")) {
                            parms.time = 30;
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        } else if (item.getTitle().equals("1分钟")) {
                            parms.time = 60;
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        }
                        else if (item.getTitle().equals("24小时")) {
                            parms.time = 24 * 60 * 60;
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        } else if (item.getTitle().equals("结束1分钟")) {
                            parms.time = 60;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("同步时间")) {
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        } else if (item.getTitle().equals("单位设置")) {
                            parms.distanceUnit = 1;
                            parms.weightUnit = 1;
                            parms.temperatureUnit = 1;
                            parms.timeFormat = 1;
                            parms.bloodSugarUnit = 1;
                            parms.uricAcidUnit = 1;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("语言设置")) {
                            parms.langType = 2;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        } else if (item.getTitle().equals("温度报警设置")) {
                            parms.on_off = true;
                            parms.maxTempInteger = 38;
                            parms.minTempInteger = 36;
                            parms.maxTempFloat = 5;
                            parms.minTempFloat = 0;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("同步数据")) {
                            parms.dataType = 2;
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        } else if (item.getTitle().equals("删除数据")) {
                            parms.dataType = 12;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("开始测量(yc)")) {
                            parms.testType = 4;
                            parms.on_off = true;
                            BleDevice.current.send(cc,cmds.get(0).id,parms);
                        } else if (item.getTitle().equals("监测模式(yc)")) {
                            parms.testType = 4;
                            parms.on_off = false;
                            parms.time = 60;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("结束测量(yc)")) {
                            parms.testType = 4;
                            parms.on_off = false;
                            BleDevice.current.send(cc,cmds.get(2).id,parms);
                        } else if (item.getTitle().equals("设置左右手")) {
                            parms.leftOrRight = 1;
                            BleDevice.current.send(cc,cmds.get(1).id,parms);
                        }
                        else if (item.getTitle().equals("糖化对码")) {
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
                            BleDevice.current.send(byteArray,false);
                        } else if (item.getTitle().equals("步数(history)")) {
                            parms.dataType = 0;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        else if (item.getTitle().equals("睡眠(history)")) {
                            parms.dataType = 1;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        } else if (item.getTitle().equals("心率(history)")) {
                            parms.dataType = 2;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        else if (item.getTitle().equals("血压(history)")) {
                            parms.dataType = 3;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        } else if (item.getTitle().equals("血氧呼吸率hrv温度血糖")) {
                            parms.dataType = 4;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        else if (item.getTitle().equals("运动模式历史数据")) {
                            parms.dataType = 5;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        } else if (item.getTitle().equals("运动控制(start|stop)")) {
                            parms.sportSwitch = 1;
                            parms.sportType = 8;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        else if (item.getTitle().equals("运动实时数据(on|off)")) {
                            parms.sportRealOpen = true;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }  else if (item.getTitle().equals("历史详情(仅限支持的设备:GLM)")) {
                            parms.historyIndex = 1;
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        else if (item.getTitle().equals("开始测量(左)")) {
//                            左手= 1
//                            右手= 0
                            parms.leftOrRight = 1;
//                            parms.on_off = true;
                            Log.e("bledemo==item.id ==>>","=====左手===="+cmds.get(item.getItemId()).id);
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        } else if (item.getTitle().equals("开始测量(右)")) {
                            parms.leftOrRight = 0;
//                            parms.on_off = true;
                            Log.e("bledemo==item.id ==>>","=====右手===="+cmds.get(item.getItemId()).id);
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }else if (item.getTitle().equals("波形开关")) {
                            parms.on_off = true;
                            Log.e("bledemo==item.id ==>>","=====波形开关===="+cmds.get(item.getItemId()).id);

                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        } else {
                            Log.e("bledemo==item.id ==>>","=====终止测量===="+cmds.get(item.getItemId()).id);
                            BleDevice.current.send(cc,cmds.get(item.getItemId()).id,parms);
                        }
                        return false;
                    }
                });

                // 显示PopupMenu
                popupMenu.show();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Notify.getSingle().remove(_ni);
        Notify.getSingle().remove(_link);
        Notify.getSingle().remove(_history);
        binding = null;
    }

    NotifyInterface _ni = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceData;
        }

        @Override
        public void message(NotifyResp data) {
            if(binding== null) return;
            NotifyResp.BleNotifyData ssss = (NotifyResp.BleNotifyData) data.data;

            if (_canMz && ssss.step == CmdType.countDown) {
                int index = Integer.parseInt((String) ((HashMap) ssss.data).get("codeIndex"));
                Float cun = (Float) ((HashMap) ssss.data).get("cun");
                Float guan = (Float) ((HashMap) ssss.data).get("guan");
                Float chi = (Float) ((HashMap) ssss.data).get("chi");
                binding.ecgCun.setBaselineLevel(index);
                binding.ecgGuan.setBaselineLevel(index);
                binding.ecgChi.setBaselineLevel(index);
                binding.ecgCun.addEcgData(cun);
                binding.ecgGuan.addEcgData(guan);
                binding.ecgChi.addEcgData(chi);

                Float cunG = (Float) ((HashMap) ssss.data).get("cunG");
                Float guanG = (Float) ((HashMap) ssss.data).get("guanG");
                Float chiG = (Float) ((HashMap) ssss.data).get("chiG");
                binding.ecgCgc.setText(String.format("寸: %.2f 关: %.2f  尺: %.2f",cunG,guanG,chiG));

                return;
            }
            String _s = ssss.dataToJson();
//            String text = String.format("%s\n start============\n  指令类型:%s \n  指令可视化数据:%s\nend================\n", binding.sendData.getText(),ssss.step.name(), _s==null ? "" : _s);
            Log.e("==>>","======="+String.format("start============\n  指令类型:%s \n  指令可视化数据:%s\nend================\n", ssss.step.name(), _s==null ? "" : _s));
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    if(binding== null) return;
//                    binding.sendData.setText(text);
//                }
//            });
        }
    };
    NotifyInterface _link = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceLink;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleLink _data = (NotifyResp.BleLink) data.data;
            binding.closeBle.setText(((BleDevice) _data.device).state == 0 ? "未连接" : "已连接");
        }
    };

    NotifyInterface _history = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.testHistory;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleHistoryData ssss = (NotifyResp.BleHistoryData) data.data;

            String text = String.format("%s\n history============\n       历史数据接收数量:%d\n       内容:%s  \nend================\n", binding.sendData.getText(),ssss.data.size(),ssss.data.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.sendData.setText(text);
                }
            });
        }
    };

}