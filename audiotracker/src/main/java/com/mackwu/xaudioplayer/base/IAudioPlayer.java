package com.mackwu.xaudioplayer.base;

/**
 * ===================================================
 * Created by MackWu on 2020/6/21 3:38
 * <a href="mailto:wumengjiao828@163.com">Contact me</a>
 * <a href="https://github.com/mackwu828">Follow me</a>
 * ===================================================
 */
public interface IAudioPlayer {

    /**
     * 开始播放
     */
    void start();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 恢复播放
     */
    void resume();

    /**
     * 释放资源
     */
    void release();

    /**
     * 指定播放位置
     */
    void seekTo(int duration);
}
