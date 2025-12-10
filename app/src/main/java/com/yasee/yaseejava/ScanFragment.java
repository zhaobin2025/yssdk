package com.yasee.yaseejava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yasee.yasee.Notify;
import com.yasee.yasee.core.enums.DeviceProcess;
import com.yasee.yasee.protocols.ble.BleDevice;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yaseejava.adapters.BleItemsAda;
import com.yasee.yaseejava.databinding.ScanFragmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFragment extends Fragment {

    private ScanFragmentBinding binding;
    private RecyclerView recyclerView;
    private BleItemsAda myAdapter = new BleItemsAda(new ArrayList<>(), new BleItemsAda.ItemClickListener() {
        @Override
        public void onItemClick(BleDevice item) {
            if(item.state != 0) {
                NavHostFragment.findNavController(ScanFragment.this)
                        .navigate(R.id.action_FirstFragment_to_BleInfo);
                return;
            }
            item.connect();
        }
    });

    private List<BleDevice> rawList;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ScanFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.bleRecyler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Initialize the adapter and set it to the RecyclerView
        recyclerView.setAdapter(myAdapter);


        Notify.getSingle().listen(_devices);
        Notify.getSingle().listen(_binds);
        Notify.getSingle().listen(_state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Notify.getSingle().remove(_devices);
        Notify.getSingle().remove(_state);
        binding = null;
    }


    public void filter(CharSequence filterStr) {
        switch (filterStr.toString()) {
            case "tmd":
                List<BleDevice> tmds = rawList.stream().filter(i -> i.getPlatform().sortName().equals("Tmd")).collect(Collectors.toList());
                myAdapter.setItems(tmds);
                break;
            case "hlw":
                List<BleDevice> hlws = rawList.stream().filter(i -> i.getPlatform().sortName().equals("Hlw")).collect(Collectors.toList());
                myAdapter.setItems(hlws);
                break;
            case "wl":
                List<BleDevice> wls = rawList.stream().filter(i -> i.getPlatform().sortName().equals("wl")).collect(Collectors.toList());
                myAdapter.setItems(wls);
                break;
            case "yc":
                List<BleDevice> ycs = rawList.stream().filter(i -> i.getPlatform().sortName().equals("yc")).collect(Collectors.toList());
                myAdapter.setItems(ycs);
                break;
        }
    }

    NotifyInterface _devices = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.searchDevices;
        }
        @Override
        public void message(NotifyResp data) {
            rawList = (List<BleDevice>) data.data;
            myAdapter.setItems(rawList);
        }
    };

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
            _bd.putString("mac",((BleDevice)((NotifyResp.BleLink) data.data).device).getMac());
            NavHostFragment.findNavController(ScanFragment.this)
                    .navigate(R.id.action_FirstFragment_to_BleInfo,_bd);
        }
    };


    NotifyInterface _binds = new NotifyInterface() {
        @Override
        public NotifyType getType() {
            return NotifyType.bindDevices;
        }
        @Override
        public void message(NotifyResp data) {
            Log.i("绑定", "message: 绑定变化");
            MainActivity.binds.addAll((List<BleDevice>) data.data);
//            myAdapter.setItems((List<BleDevice>) data.data);
        }
    };
}