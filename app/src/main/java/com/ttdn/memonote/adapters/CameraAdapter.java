package com.ttdn.memonote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttdn.memonote.R;
import com.ttdn.memonote.data.Camera;

import java.util.List;

/**
 * Created by ttdn1 on 10/1/2017.
 */

public class CameraAdapter extends BaseAdapter {
    private List<Camera> mListCamera;
    private int mLayoutInflater;
    private Context mContext;

    public CameraAdapter(Context aContext, int layout, List<Camera> listData){
        this.mContext = aContext;
        this.mListCamera = listData;
        this.mLayoutInflater = layout;
    }

    @Override
    public int getCount() {
        return mListCamera.size();
    }

    @Override
    public Object getItem(int position) {
        return mListCamera.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView =inflater.inflate(mLayoutInflater,null);
        TextView txtName = (TextView)convertView.findViewById(R.id.txt_camera);
        txtName.setText(mListCamera.get(position).mName);
        ImageView img =(ImageView)convertView.findViewById(R.id.img_camera);
        img.setImageResource(mListCamera.get(position).mImage);
        return convertView;
    }
}
