package com.thinkstu.fragments;


import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;

import com.thinkstu.Service.*;
import com.thinkstu.Service.Impl.*;
import com.thinkstu.adapter.*;
import com.thinkstu.dto.*;
import com.thinkstu.entity.*;
import com.thinkstu.musics.*;

import org.greenrobot.eventbus.*;

/* 歌单主界面 */
public class SongContentFragment extends Fragment {
    private static SongContentFragment songContentFragment;

    private View         view;
    private MusicService musicService;
    private SongDto      songDto;

    // 构建单例模式
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

        ListView listView       = view.findViewById(R.id.song_list);
        TextView textView_title = view.findViewById(R.id.song_title);

        textView_title.setText(songDto.getSongSheetBean().getName());
        // 如果是本地歌单，则不显示歌单图片
        if (songDto.isLocal()) {
            listView.setAdapter(new SongAdapter(getContext()));
        } else {
            listView.setAdapter(new SongAdapter(getContext(), songDto));
        }
        // 点击事件：播放歌曲
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
