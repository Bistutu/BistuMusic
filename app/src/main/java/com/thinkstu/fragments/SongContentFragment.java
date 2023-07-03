package com.thinkstu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thinkstu.Service.MusicService;
import com.thinkstu.Service.ServiceImpl.*;
import com.thinkstu.adater.SongAdapter;
import com.thinkstu.dto.SongDto;
import com.thinkstu.entity.SongBean;
import com.thinkstu.music.*;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/*歌单主界面*/

public class SongContentFragment extends Fragment {
    private static final String TAG = "SongContentFragment";
    private static SongContentFragment songContentFragment;

    private View view;
    private MusicService musicService;
    private SongDto songDto;

    public static SongContentFragment getInstance() {
        if (songContentFragment == null) {
            synchronized (SongContentFragment.class) {
                if (songContentFragment == null) {
                    songContentFragment = new SongContentFragment();
                }
            }
        }
        return songContentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = inflater.inflate(R.layout.fragment_main_song, container, false);
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initView();
        }
    }

    private void initView() {
        musicService = MusicServiceImpl.getInstance(getContext());
        ListView listView = view.findViewById(R.id.song_list);
        TextView textView_title = view.findViewById(R.id.song_title);
        textView_title.setText(songDto.getSongSheetBean().getName());
        if (songDto.isLocal()) {
            listView.setAdapter(new SongAdapter(getContext()));
        } else {
            listView.setAdapter(new SongAdapter(getContext(), songDto));
        }
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            SongBean songBean = (SongBean) adapterView.getItemAtPosition(i);
            musicService.play(songBean.getName());
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    public void onGetMessage(SongDto songDto) {
        if (songDto != null) {
            this.songDto = songDto;
        }
    }
}