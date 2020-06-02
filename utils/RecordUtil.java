package com.liudukun.dkchat.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;

import com.liudukun.dkchat.config.DKConfig;

import java.util.Timer;
import java.util.TimerTask;


public class RecordUtil {

    private static final String TAG = RecordUtil.class.getSimpleName();
    private static RecordUtil sInstance = new RecordUtil();
    private static int MAGIC_NUMBER = 500;
    private static int MIN_RECORD_DURATION = 1000;
    private RecordCallback mRecordCallback;
    private PlayCallback mPlayCallback;
    private Timer timer;
    private TimerTask task;

    private String mAudioRecordPath;
    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;
    private Handler mHandler;


    private RecordUtil() {
        mHandler = new Handler();
    }

    public static RecordUtil getInstance() {
        return sInstance;
    }


    public void startRecord(RecordCallback cb) {
        mRecordCallback = cb;
        try {
            mAudioRecordPath = StringUtil.getMediaCacheDirectory() + StringUtil.getRandomName() + ".mp4";
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 使用mp4容器并且后缀改为.m4a，来兼容小程序的播放
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mAudioRecordPath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.prepare();
            mRecorder.start();
            // 最大录制时间之后需要停止录制
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopInternalRecord();
                    onRecordCompleted(true);
                    mRecordCallback = null;
                    ToastUtil.toastShortMessage("已达到最大语音长度");
                }
            }, 60 * 1000 - 100);

            updateMicStatus();

        } catch (Exception e) {
            DKLog.d( "startRecord failed "+ e.getMessage());
            stopInternalRecord();
            onRecordCompleted(false);
        }
    }

    public void stopRecord() {
        stopInternalRecord();

        if (getDuration() < 1){
            ToastUtil.toastShortMessage("录音时间太短");
            onRecordCompleted(false);
            return;
        }
        onRecordCompleted(true);
        mRecordCallback = null;
    }

    private void stopInternalRecord() {
        mHandler.removeCallbacksAndMessages(null);
        if (mRecorder == null) {
            return;
        }
        mRecorder.release();
        mRecorder = null;
    }

    public void startPlay(String filePath,PlayCallback cb) {
        mPlayCallback = cb;
        mAudioRecordPath = filePath;
        try {
            stopInternalPlay();
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(filePath);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopInternalPlay();
                    onPlayCompleted(true);
                }
            });
            mPlayer.prepare();
            mPlayer.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                    while(isPlaying()){
                        long cu = mPlayer.getCurrentPosition();
                        Thread.sleep(500,0);
                        double p = cu * 1.0/ getDuration();
                        if (mPlayCallback!=null) {
                            mPlayCallback.onPlayProgress(p);
                        }
                        continue;
                    }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            DKLog.d( "startPlay failed");
            ToastUtil.toastLongMessage("语音文件已损坏或不存在");
            stopInternalPlay();
            onPlayCompleted(false);
        }
    }

    public void pausePlay(){
        if (mPlayer==null) return;
        mPlayer.pause();
    }

    public void resumePlay(){
        if (mPlayer==null) return;
        mPlayer.start();
    }

    public void seekPlay(int progress){
        if (mPlayer==null) return;
        int seek = (int) (getDuration() * (progress/100.0) );
        mPlayer.seekTo(seek);
    }

    public void stopPlay() {
        stopInternalPlay();
        onPlayCompleted(false);
        mPlayCallback = null;
    }

    private void stopInternalPlay() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    private void onPlayCompleted(boolean success) {
        if (mPlayCallback != null) {
            mPlayCallback.onCompletion(success);
        }
        mPlayer = null;
    }

    private void onRecordCompleted(boolean success) {
        if (mRecordCallback != null) {
            mRecordCallback.onCompletion(success);
        }
        mRecorder = null;
    }

    public String getPath() {
        return mAudioRecordPath;
    }

    public int getDuration() {
        if (TextUtils.isEmpty(mAudioRecordPath)) {
            return 0;
        }
        int duration = 0;
        // 通过初始化播放器的方式来获取真实的音频长度
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(mAudioRecordPath);
            mp.prepare();
            duration = mp.getDuration();
            // 语音长度如果是59s多，因为外部会/1000取整，会一直显示59'，所以这里对长度进行处理，达到四舍五入的效果
            if (duration < MIN_RECORD_DURATION) {
                duration = 0;
            } else {
                duration = duration + MAGIC_NUMBER;
            }
        } catch (Exception e) {
            DKLog.d(TAG+"getDuration failed"+ e);
        }
        if (duration < 0) {
            duration = 0;
        }
        return duration;
    }

    public interface RecordCallback {
        void onCompletion(Boolean success);
        void onVolumeMeter(double db);
    }

    public interface PlayCallback{
        void onCompletion(Boolean success);
        void onPlayProgress(double progress);
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {
        if (mRecorder != null) {
            double ratio = (double)mRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            mRecordCallback.onVolumeMeter(db);
            DKLog.d(TAG,"分贝值："+db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }
}
