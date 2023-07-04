package com.thinkstu.fragments;


import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;

import com.thinkstu.*;
import com.thinkstu.Service.*;
import com.thinkstu.Service.Impl.*;
import com.thinkstu.adapter.*;
import com.thinkstu.dto.*;
import com.thinkstu.entity.*;
import com.thinkstu.musics.*;

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

    // 作用：初始化视图
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
