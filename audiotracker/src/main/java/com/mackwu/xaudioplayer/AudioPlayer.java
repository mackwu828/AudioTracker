package com.mackwu.xaudioplayer;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

import com.mackwu.xaudioplayer.base.AudioPlayState;
import com.mackwu.xaudioplayer.base.IAudioPlayer;
import com.mackwu.xaudioplayer.util.IOUtil;
import com.mackwu.xaudioplayer.util.LogUtil;

import java.io.FileInputStream;

/**
 * ===================================================
 * Created by MackWu on 2020/6/21 3:40
 * <a href="mailto:wumengjiao828@163.com">Contact me</a>
 * <a href="https://github.com/mackwu828">Follow me</a>
 * ===================================================
 */
public class AudioPlayer implements IAudioPlayer {


    private int streamType = AudioManager.STREAM_MUSIC;
    private int sampleRateInHz = 16000;
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mode = AudioTrack.MODE_STREAM;
    private int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);


    private static final String HEAD = AudioPlayer.class.getSimpleName();
    private static AudioPlayer instance;
    private AudioTrack audioTrack;
    private AudioPlayState state;
    private String sourceDirPath;
    private String sourcePath;

    private AudioPlayer(Context context) {
        sourceDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/audio_play";
        sourcePath = sourceDirPath + "/recording.pcm";
    }

    public static AudioPlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (AudioPlayer.class) {
                if (instance == null) {
                    instance = new AudioPlayer(context);
                }
            }
        }
        return instance;
    }


    @Override
    public void start() {
        LogUtil.d(HEAD, "start...  state: " + state);
        if (null == audioTrack) {
            audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode);
            state = AudioPlayState.IDLE;
        }

        if (state == AudioPlayState.IDLE || state == AudioPlayState.STOPPED || state == AudioPlayState.COMPLETED) {
            startPlaying();
        }
    }

    private void startPlaying() {
        new Thread(() -> {
            FileInputStream fis = null;
            byte[] buffer = new byte[bufferSizeInBytes];
            try {
                fis = new FileInputStream(sourcePath);
                while (fis.available() > 0) {
                    int read = fis.read(buffer);
                    if (read == AudioTrack.ERROR_INVALID_OPERATION || read == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (read == AudioTrack.ERROR) {
                        state = AudioPlayState.ERROR;
                        break;
                    }
                    if (read == AudioTrack.SUCCESS) {
                        state = AudioPlayState.COMPLETED;
                        break;
                    }
                    audioTrack.play();
                    state = AudioPlayState.PLAYING;
                    audioTrack.write(buffer, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtil.close(fis);
            }
        }).start();
    }

    @Override
    public void stop() {
        LogUtil.d(HEAD, "stop...  state: " + state);
        if (state == AudioPlayState.PLAYING) {
            audioTrack.stop();
            state = AudioPlayState.STOPPED;
        }
    }

    @Override
    public void pause() {
        LogUtil.d(HEAD, "pause...");
    }

    @Override
    public void resume() {
        LogUtil.d(HEAD, "resume...");
    }

    @Override
    public void release() {
        LogUtil.d(HEAD, "release...");
        if (state == AudioPlayState.IDLE || state == AudioPlayState.STOPPED) {
            audioTrack.release();
            audioTrack = null;
            state = AudioPlayState.RELEASED;
        }
    }

    @Override
    public void seekTo(final int duration) {

    }
}
