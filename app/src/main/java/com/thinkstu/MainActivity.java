package com.thinkstu;

import androidx.appcompat.app.*;
import androidx.fragment.app.*;

import android.os.Bundle;
import android.view.*;

import com.thinkstu.fragments.*;
import com.thinkstu.musics.*;

import org.litepal.LitePal;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // 隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 初始化数据库，并且每次启动时清空数据库，防止干扰
        LitePal.initialize(this);
        LitePal.deleteAll("songbean");
        LitePal.deleteAll("songsheetbean");
        //将碎片和activity绑定
        initMainFragment(savedInstanceState);
    }

    // 初始化碎片：将碎片和 activity 绑定
    private void initMainFragment(Bundle bundle) {
        if (bundle == null) {
            FragmentManager     fragmentManager     = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                    .add(R.id.fragment_host, MainFragment.getInstance(), MainFragment.class.getName())
                    .add(R.id.fragment_host, MusicInfoFragment.getInstance(), MusicInfoFragment.class.getName())
                    .add(R.id.main_content, MainContentFragment.getInstance(), MainContentFragment.class.getName())
                    .hide(MusicInfoFragment.getInstance())
                    .commit();
        }
    }

    // 显示歌曲播放页
    public void enterMusicInfoFragment() {
        FragmentManager     fragmentManager     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .hide(MainFragment.getInstance())
                .show(MusicInfoFragment.getInstance())
                .addToBackStack(null)
                .commit();
    }

    //初始化歌单列表碎片
    public void enterSongContentFragment() {
        FragmentManager     fragmentManager     = getSupportFragmentManager();
        Fragment            fragment            = fragmentManager.findFragmentByTag(SongContentFragment.class.getName());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .hide(MainContentFragment.getInstance());
        if (fragment == null) {
            fragmentTransaction
                    .add(R.id.main_content, SongContentFragment.getInstance(), SongContentFragment.class.getName());
        } else {
            fragmentTransaction
                    .show(fragment);
        }
        fragmentTransaction
                .addToBackStack(null)
                .commit();
    }

    //设置返回键方法
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }
}
