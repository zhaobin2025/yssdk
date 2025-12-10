package com.yasee.yaseejava.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.yasee.yasee.core.models.Check;
import com.yasee.yaseejava.R;

import java.util.List;

public class ChecksAda extends BaseAdapter {
    private Context mContext;
    private List<Check> checks;
    public ChecksAda(Context c,List<Check> cs) {
        mContext = c;
        checks = cs;
    }

    @Override
    public int getCount() {
        return checks.size();
    }

    @Override
    public Object getItem(int position) {
        return checks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Check one = checks.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_item, null);
        }
        TextView tv = convertView.findViewById(R.id.check_name);
        tv.setText(one.name);
        return  convertView;
    }

}