package com.thinkstu.fragments;

import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;

import com.thinkstu.Service.*;
import com.thinkstu.Service.Impl.*;
import com.thinkstu.musics.*;

import java.lang.ref.*;

// 歌曲播放界面
public class MusicInfoFragment extends Fragment {
    private static final int               REFRESH_SEEKBAR_PROGRESS = 1;    // 刷新进度条
    private static final int               REFRESH_SEEKBAR_MAX      = 2;    // 刷新进度条最大值
    private static final int               REFRESH_HEADER           = 3;    // 刷新头部
    private static final int               REFRESH_PLAY             = 4;    // 刷新播放按钮
    private static       MusicInfoFragment musicInfoFragment        = null;

    private MusicService   musicService;
    private View           view;
    private SeekBarHandler seekBarHandler;
    private TextView       textView_title;
    private SeekBar        seekBar;

    // 单例模式
    public static MusicInfoFragment getInstance() {
        if (musicInfoFragment == null) {
            synchronized (MusicInfoFragment.class) {
                if (musicInfoFragment == null) {
                    musicInfoFragment = new MusicInfoFragment();
                }
            }
        }
        return musicInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        seekBarHandler = new SeekBarHandler(this);
        view           = inflater.inflate(R.layout.fragment_music_info, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        musicService   = MusicServiceImpl.getInstance(getContext());
        textView_title = view.findViewById(R.id.info_head_title);
        seekBar        = view.findViewById(R.id.info_seekBar);
        // 图片旋转动画
        View      image_column = view.findViewById(R.id.image_column);
        Animation animation    = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        image_column.startAnimation(animation);

        ImageView       imageView_last = view.findViewById(R.id.info_last);
        final ImageView imageView_play = view.findViewById(R.id.info_play);
        ImageView       imageView_next = view.findViewById(R.id.info_next);

        musicService.setMusicChangedListener(() -> refreshHeader());
        musicService.setMusicPlayingChangedListener(() -> {
            refreshImgPlay();
            if (musicService.isPlaying()) {
                send();
            } else {
                seekBarHandler.removeCallbacksAndMessages(null);
            }
        });

        textView_title.setText(musicService.getCurrentMusicInfo());
        // 上一首、下一首
        imageView_last.setOnClickListener(view -> musicService.last());
        imageView_next.setOnClickListener(view -> musicService.next());

        refreshImgPlay();
        // 播放按钮
        if (musicService.isPlaying()) {
            send();
        }
        imageView_play.setOnClickListener(view -> {
            if (musicService.isPlaying()) {
                musicService.onPause();
            } else {
                musicService.play(null);
            }
            refreshImgPlay();
        });
        seekBar.setMax(musicService.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seekTo(seekBar.getProgress());
            }
        });

        ImageView imageView_back = view.findViewById(R.id.info_head_back);
        imageView_back.setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initView();
        }
    }

    private void refreshImgPlay() {
        new Thread(() -> seekBarHandler.sendEmptyMessage(REFRESH_PLAY)).start();
    }

    private void refreshHeader() {
        new Thread(() -> seekBarHandler.sendEmptyMessage(REFRESH_HEADER)).start();
    }

    /**
     * 新建线程，通过handler定时刷新SeekBar的ui
     */
    private void send() {
        new Thread(() -> seekBarHandler.sendEmptyMessage(REFRESH_SEEKBAR_MAX)).start();
    }

    /**
     * 内部类，避免Handler发生内存泄漏
     * 由handleMessage处理
     */
    private static class SeekBarHandler extends Handler {
        WeakReference<MusicInfoFragment> musicInfoFragment;

        private SeekBarHandler(MusicInfoFragment musicInfoFragment) {
            this.musicInfoFragment = new WeakReference<>(musicInfoFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MusicInfoFragment activity = musicInfoFragment.get();
            switch (msg.what) {
                case REFRESH_HEADER:
                    activity.textView_title.setText(activity.musicService.getCurrentMusicInfo());
                    break;
                case REFRESH_PLAY:
                    FragmentActivity fragmentActivity = activity.getActivity();
                    if (fragmentActivity != null) {
                        ImageView imageView = fragmentActivity.findViewById(R.id.info_play);
                        if (activity.musicService.isPlaying()) {
                            imageView.setImageResource(R.drawable.ic_pause_black);
                        } else {
                            imageView.setImageResource(R.drawable.ic_play_black);
                        }
                    }
                    break;
                case REFRESH_SEEKBAR_MAX:
                    activity.seekBar.setMax(activity.musicService.getDuration());
                case REFRESH_SEEKBAR_PROGRESS:
                    if (activity.seekBar.getMax() != activity.musicService.getDuration()) {
                        activity.seekBarHandler.removeCallbacksAndMessages(null);
                        activity.send();
                    } else {
                        activity.seekBar.setProgress(activity.musicService.getCurrentProgress());
                        sendEmptyMessageDelayed(REFRESH_SEEKBAR_PROGRESS, 1000);
                    }
                    break;
            }
        }
    }
}
