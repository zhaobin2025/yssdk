package com.yasee.yaseejava;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yasee.yasee.Notify;
import com.yasee.yasee.core.enums.CmdType;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yaseejava.databinding.ActivityMzBinding;

import java.util.HashMap;

public class MzActivity extends AppCompatActivity {

    private ActivityMzBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMzBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Notify.getSingle().listen(_ni);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notify.getSingle().remove(_ni);
    }

    NotifyInterface _ni = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.deviceData;
        }

        @Override
        public void message(NotifyResp data) {
            NotifyResp.BleNotifyData ssss = (NotifyResp.BleNotifyData) data.data;
            if(ssss.step != CmdType.countDown) return;

            Float cun = (Float) ((HashMap) ssss.data).get("cun");
            Float guan = (Float) ((HashMap) ssss.data).get("guan");
            Float chi = (Float) ((HashMap) ssss.data).get("chi");
            binding.svCun.addEcgData(cun);
            binding.svGuan.addEcgData(guan);
            binding.svChi.addEcgData(chi);

            Float cunG = (Float) ((HashMap) ssss.data).get("cunG");
            Float guanG = (Float) ((HashMap) ssss.data).get("guanG");
            Float chiG = (Float) ((HashMap) ssss.data).get("chiG");
            binding.svText.setText(String.format("寸: %.2f 关: %.2f  尺: %.2f",cunG,guanG,chiG));

            String _sss = "本次"; //binding.sendData.getText().toString();

            String _s = ssss.dataToJson();
            String text = String.format("start============\n  指令类型:%s \n  指令可视化数据:%s\nend================\n", ssss.step.name(), _s==null ? "" : _s);

        }
    };

}