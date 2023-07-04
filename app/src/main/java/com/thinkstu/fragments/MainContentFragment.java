package com.thinkstu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thinkstu.MainActivity;
import com.thinkstu.Service.ServiceImpl.*;
import com.thinkstu.Service.SongSheetService;
import com.thinkstu.adater.SongSheetAdapter;
import com.thinkstu.dto.SongDto;
import com.thinkstu.entity.SongBean;
import com.thinkstu.entity.SongSheetBean;
import com.thinkstu.music.*;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/*歌单列表*/

public class MainContentFragment extends Fragment {

    private        View                view;
    private static MainContentFragment mainContentFragment;
    private        SongSheetService    songSheetService;

    // 构建单例模式
    public static MainContentFragment getInstance() {
        if (mainContentFragment == null) {
            synchronized (MainContentFragment.class) {
                if (mainContentFragment == null) {
                    mainContentFragment = new MainContentFragment();
                }
            }
        }
        return mainContentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view             = inflater.inflate(R.layout.fragment_main_content, container, false);
        songSheetService = new SongSheetServiceImpl();
        initView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initView();
        }
    }

    private void initView() {
        //查歌单
        ListView                  listView         = view.findViewById(R.id.main_listView_songSheet);
        final List<SongSheetBean> data             = songSheetService.findAll();
        final SongSheetAdapter    songSheetAdapter = new SongSheetAdapter(getContext(), data);
        listView.setAdapter(songSheetAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                SongSheetBean  songSheetBean = (SongSheetBean) adapterView.getItemAtPosition(i);
                List<SongBean> songBeanList  = songSheetService.findSongBeanBySongSheetId(songSheetBean.getId());
                if (i != 0) {
                    EventBus.getDefault().postSticky(new SongDto(songSheetBean, songBeanList));
                } else {
                    EventBus.getDefault().postSticky(new SongDto(songSheetBean, songBeanList, true));
                }
                mainActivity.enterSongContentFragment();
            }
        });
    }
}
