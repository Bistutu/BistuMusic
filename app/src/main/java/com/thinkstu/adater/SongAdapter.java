package com.thinkstu.adater;

import android.content.*;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.thinkstu.Service.ServiceImpl.*;
import com.thinkstu.dto.SongDto;
import com.thinkstu.entity.SongBean;
import com.thinkstu.entity.SongSheetBean;
import com.thinkstu.musics.*;

import org.litepal.LitePal;

public class SongAdapter extends BaseAdapter {
    private SongDto songDto;
    private Context mContext;

    public SongAdapter(Context context, SongDto songDto) {
        this.songDto  = songDto;
        this.mContext = context;
    }

    public SongAdapter(Context context) {
        // 获取第一份歌单和其中的歌曲
        this.songDto  = new SongDto(LitePal.findFirst(SongSheetBean.class), LitePal.findAll(SongBean.class));
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return songDto.getSongBeanList().size();
    }

    @Override
    public Object getItem(int i) {
        return songDto.getSongBeanList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SongBean   songBean = (SongBean) getItem(i);
        View       contentView;
        ViewHolder viewHolder;
        if (view == null) {
            contentView         = LayoutInflater.from(mContext).inflate(R.layout.item_song, null);
            viewHolder          = new ViewHolder();
            viewHolder.textView = contentView.findViewById(R.id.item_song_info);
            viewHolder.menu     = contentView.findViewById(R.id.item_song_menu);
            contentView.setTag(viewHolder);
        } else {
            contentView = view;
            viewHolder  = (ViewHolder) contentView.getTag();
        }
        viewHolder.textView.setText(songBean.getName());
        // 设置点击事件：弹出菜单
        viewHolder.menu.setOnClickListener(v -> v.post(() -> showPopMenu(v)));
        viewHolder.menu.setTag(songBean);
        // 设置背景
        contentView.setBackgroundResource(R.drawable.list_item_background);

        return contentView;
    }

    private class ViewHolder {
        private TextView  textView;
        private ImageView menu;
    }

    private void showPopMenu(final View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_song_download: {
                    // TODO 菜单的下载功能
                    SongBean songBean = (SongBean) view.getTag();
                    Intent   intent   = new Intent(mContext, DownloadService.class);
                    Toast.makeText(mContext, "歌曲下载中...", Toast.LENGTH_SHORT).show();
                    intent.putExtra("fileName", songBean.getName());
                    mContext.startService(intent);
                }
            }
            return false;
        });
        popupMenu.show();
    }
}
