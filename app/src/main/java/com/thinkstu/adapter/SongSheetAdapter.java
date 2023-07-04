package com.thinkstu.adapter;


import android.content.*;
import android.view.*;
import android.widget.*;

import com.thinkstu.entity.*;
import com.thinkstu.musics.*;

import java.util.List;

/**
 * 作用：歌单适配器
 */
public class SongSheetAdapter extends BaseAdapter {
    private List<SongSheetBean> songSheetList;
    private Context             mContext;

    public SongSheetAdapter(Context context, List<SongSheetBean> songSheetList) {
        this.mContext      = context;
        this.songSheetList = songSheetList;
    }

    @Override
    public int getCount() {
        return songSheetList.size();
    }

    @Override
    public Object getItem(int i) {
        return songSheetList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // 作用：获取歌单列表
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SongSheetBean    songSheet = (SongSheetBean) getItem(i);
        View             contentView;
        final ViewHolder viewHolder;
        if (view == null) {
            contentView          = LayoutInflater.from(mContext).inflate(R.layout.item_songsheet, null);
            viewHolder           = new ViewHolder();
            viewHolder.name      = contentView.findViewById(R.id.item_songSheet_name);
            viewHolder.imageView = contentView.findViewById(R.id.item_songSheet_img);
            contentView.setTag(viewHolder);
        } else {
            contentView = view;
            viewHolder  = (ViewHolder) contentView.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.ic_disk);
        viewHolder.name.setText(songSheet.getName());
        return contentView;
    }

    private class ViewHolder {
        TextView  name;
        ImageView imageView;
    }
}
