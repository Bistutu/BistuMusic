package com.thinkstu.adater;


import android.content.*;
import android.view.*;
import android.widget.*;

import com.thinkstu.Service.*;
import com.thinkstu.Service.ServiceImpl.*;
import com.thinkstu.entity.*;
import com.thinkstu.musics.*;

import java.util.List;

/**
 * 作用：歌单适配器
 */
public class SongSheetAdapter extends BaseAdapter {
    private List<SongSheetBean> songSheetList;
    private Context             mContext;
    private SongSheetService    songSheetService;

    public SongSheetAdapter(Context context, List<SongSheetBean> songSheetList) {
        this.mContext         = context;
        this.songSheetList    = songSheetList;
        this.songSheetService = new SongSheetServiceImpl();
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
            viewHolder.menu      = contentView.findViewById(R.id.item_songSheet_menu);
            contentView.setTag(viewHolder);
        } else {
            contentView = view;
            viewHolder  = (ViewHolder) contentView.getTag();
            if (i == 0) {
                viewHolder.menu.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.menu.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.imageView.setImageResource(R.drawable.ic_disk);
        viewHolder.name.setText(songSheet.getName());
        viewHolder.menu.setOnClickListener(view1 -> view1.post(() -> showPopMenu(view1)));
        viewHolder.menu.setTag(getItem(i));
        return contentView;
    }

    private class ViewHolder {
        TextView  name;
        ImageView imageView;
        ImageView menu;
    }

    private void showPopMenu(final View view) {
        final SongSheetAdapter songSheetAdapter = this;
        final SongSheetBean    songSheet        = (SongSheetBean) view.getTag();
        PopupMenu              popupMenu        = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.song_sheet_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            songSheetService.delete(songSheet);
            songSheetList.remove(songSheet);
            songSheetAdapter.notifyDataSetChanged();
            return false;
        });
        popupMenu.show();
    }
}
