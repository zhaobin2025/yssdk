package com.yasee.yaseejava.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yasee.yasee.Notify;
import com.yasee.yasee.protocols.ble.BleDevice;
import com.yasee.yasee.core.enums.DeviceProcess;
import com.yasee.yasee.core.enums.NotifyType;
import com.yasee.yasee.core.interfaces.NotifyInterface;
import com.yasee.yasee.core.models.NotifyResp;
import com.yasee.yaseejava.R;

import java.util.List;

import com.yasee.yaseejava.databinding.BleItemBinding;

public class BleItemsAda extends RecyclerView.Adapter<BleItemsAda.BleItemsAdaViewHolder> {
    private List<BleDevice> itemList;
    private BleItemBinding bib;
    private ItemClickListener itemClickListener;

    public BleItemsAda(List<BleDevice> itemList,ItemClickListener itemClickListener) {
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
    }
    public void setItems(List<BleDevice> devices) {
        this.itemList = devices;
        notifyDataSetChanged();
    }
    public interface ItemClickListener {
        void onItemClick(BleDevice item);
    }

    @NonNull
    @Override
    public BleItemsAdaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bib = BleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BleItemsAdaViewHolder(bib.listItem);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BleItemsAdaViewHolder holder, int position) {
        BleDevice item = itemList.get(position);
        holder.textView.setText(item.getModel());
        holder.linked.setText(item.state == 0 ? "绑定连接" : "已连接");
        holder.linked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            }
        });
        Notify.getSingle().listen(new NotifyInterface() {
            @Override
            public NotifyType getType() {
                return NotifyType.deviceLink;
            }

            @Override
            public void message(NotifyResp data) {
                NotifyResp.BleLink bl = (NotifyResp.BleLink) data.data;
                holder.linked.setText(bl.process != DeviceProcess.unlink ? "已连接" : "绑定连接");
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class BleItemsAdaViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Button linked;
        private BleItemBinding bib;

        public BleItemsAdaViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            linked = itemView.findViewById(R.id.ble_linked);
        }
    }

}
