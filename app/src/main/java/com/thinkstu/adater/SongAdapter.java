package com.thinkstu.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkstu.dto.SongDto;
import com.thinkstu.entity.SongBean;
import com.thinkstu.entity.SongSheetBean;
import com.thinkstu.music.*;

import org.litepal.LitePal;

public class SongAdapter extends BaseAdapter {
    private static final String TAG = "SongAdapter";
    private SongDto songDto;
    private Context mContext;

    public SongAdapter(Context context, SongDto songDto){
        this.songDto = songDto;
        this.mContext = context;
    }

    public SongAdapter(Context context){
        this.songDto = new SongDto(LitePal.findFirst(SongSheetBean.class),LitePal.findAll(SongBean.class));
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
        SongBean songBean = (SongBean) getItem(i);
        View contentView;
        ViewHolder viewHolder;
        if (view == null) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.item_song, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = contentView.findViewById(R.id.item_song_info);
            viewHolder.menu = contentView.findViewById(R.id.item_song_menu);
            contentView.setTag(viewHolder);
        } else {
            contentView = view;
            viewHolder = (ViewHolder) contentView.getTag();
        }
        viewHolder.textView.setText(songBean.getName());
        viewHolder.menu.setOnClickListener(view1 -> view1.post(() -> showPopMenu(view1)));
        viewHolder.menu.setTag(songBean);
        return contentView;
    }

    private class ViewHolder {
        private TextView textView;
        private ImageView menu;
    }

    private void showPopMenu(final View view) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_addtosheet, null);
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        final SongBean songBean = (SongBean) view.getTag();
        final SongAdapter songAdapter = this;
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_song_download:
                    // TODO 下载功能
                    Toast.makeText(mContext, "暂未实现！", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        popupMenu.show();
    }

}
