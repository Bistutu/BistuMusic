package com.thinkstu.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thinkstu.MainActivity;
import com.thinkstu.Service.MusicService;
import com.thinkstu.Service.Impl.*;
import com.thinkstu.musics.*;

import java.lang.ref.WeakReference;


/*
 * fragment_main.xml：footer界面
 */
public class MainFragment extends Fragment {
    private static final int          REFRESH_FOOTER = 3;
    private static final int          REFRESH_PLAY   = 4;
    private static       MainFragment mainFragment   = null;
    View image_column;

    private View          view;
    private MusicService  musicService;
    private TextView      textView_song;
    private FooterHandler footerHandler;

    // 单例模式
    public static MainFragment getInstance() {
        if (mainFragment == null) {
            synchronized (MainFragment.class) {
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
            }
        }
        return mainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initMainView();
        footerHandler = new FooterHandler(this);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //  初始化footer布局
    private void initMainView() {
        musicService = MusicServiceImpl.getInstance(getContext());      // 获取音乐服务（ Service 单例对象）
        //footer中的播放按钮
        final ImageView ImageView_play = view.findViewById(R.id.main_footer_play);
        image_column = view.findViewById(R.id.small_column);
        // 播放按钮的点击事件
        ImageView_play.setOnClickListener(view -> {
            musicService.play(null);
            if (musicService.isPlaying()) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
                image_column.startAnimation(animation);
            } else {
                ImageView_play.setImageResource(R.drawable.ic_pause_red);
                image_column.clearAnimation();
            }
        });
        // footer中的上一首按钮
        final ImageView ImageView_last = view.findViewById(R.id.main_footer_last);
        ImageView_last.setOnClickListener(view -> {
            musicService.last();
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            image_column.startAnimation(animation);
        });
        // footer中的下一首按钮
        final ImageView ImageView_next = view.findViewById(R.id.main_footer_next);
        ImageView_next.setOnClickListener(view -> {
            musicService.next();
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            image_column.startAnimation(animation);
        });

        // footer中的歌曲名
        textView_song = view.findViewById(R.id.main_footer_song);
        textView_song.setText(musicService.getCurrentMusicInfo());
        musicService.setMusicChangedListener(() -> refreshFooter());        // 设置音乐改变监听器
        musicService.setMusicPlayingChangedListener(() -> refreshPlay());   // 设置音乐播放状态改变监听器
        // 获取 fragment 的整体布局，并设置点击事件：点击当前正在播放的歌曲，进入歌曲详情界面
        LinearLayout footer = view.findViewById(R.id.main_footer);
        footer.setOnClickListener(view -> enterMusicInfoFragment());
    }

    private void enterMusicInfoFragment() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.enterMusicInfoFragment();  // 进入歌曲详情界面
        }
    }

    //刷新 footer
    private void refreshFooter() {
        new Thread(() -> footerHandler.sendEmptyMessage(REFRESH_FOOTER)).start();
    }

    private void refreshPlay() {
        new Thread(() -> footerHandler.sendEmptyMessage(REFRESH_PLAY)).start();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refreshPlay();
            initMainView();
        }
        super.onHiddenChanged(hidden);
    }


    /**
     * 内部类，避免Handler发生内存泄漏
     * 由handleMessage处理
     */
    private static class FooterHandler extends Handler {

        WeakReference<MainFragment> mainFragment;

        private FooterHandler(MainFragment mainFragment) {
            this.mainFragment = new WeakReference<>(mainFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainFragment activity = mainFragment.get();
            switch (msg.what) {
                case REFRESH_FOOTER:
                    activity.textView_song.setText(activity.musicService.getCurrentMusicInfo());
                    break;
                case REFRESH_PLAY:
                    if (activity.getActivity() != null) {
                        ImageView imageView = activity.getActivity().findViewById(R.id.main_footer_play);
                        if (activity.musicService.isPlaying()) {
                            imageView.setImageResource(R.drawable.ic_pause_red);
                        } else {
                            imageView.setImageResource(R.drawable.ic_play_red);
                        }
                    }
                    break;
            }
        }
    }
}
