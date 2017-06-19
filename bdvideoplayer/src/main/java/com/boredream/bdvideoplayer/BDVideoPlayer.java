package com.boredream.bdvideoplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import com.boredream.bdvideoplayer.listener.PlayerCallback;

import java.io.IOException;

/**
 * 只包含最基础的播放器功能，MediaPlayer可以替换成其他框架的播放器
 */
public class BDVideoPlayer {

    private static final String TAG = "VideoPlayer";

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    private MediaPlayer player;
    private int curState = STATE_IDLE;

    private PlayerCallback callback;
    private int currentBufferPercentage;
    private String path;
    private SurfaceHolder surfaceHolder;

    public void setCallback(PlayerCallback PlayerCallback) {
        this.callback = PlayerCallback;
    }

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
            setCurrentState(STATE_ERROR);
            if (callback != null) {
                callback.onError(player, framework_err, impl_err);
            }
            return true;
        }
    };

    public BDVideoPlayer() {
        setCurrentState(STATE_IDLE);
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void setVideoPath(String path) {
        this.path = path;
        openVideo();
    }

    public String getVideoPath() {
        return path;
    }

    public void openVideo() {
        if (path == null || surfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        reset();

        try {
            player = new MediaPlayer();
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    currentBufferPercentage = percent;
                    if (callback != null) callback.onBufferingUpdate(mp, percent);
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setCurrentState(STATE_PLAYBACK_COMPLETED);
                    if (callback != null) {
                        callback.onCompletion(mp);
                    }
                }
            });
            player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (callback != null) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            callback.onLoadingChanged(true);
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            callback.onLoadingChanged(false);
                        }
                    }
                    return false;
                }
            });
            player.setOnErrorListener(mErrorListener);
            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    if (callback != null) callback.onVideoSizeChanged(mp, width, height);
                }
            });
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    setCurrentState(STATE_PREPARED);
                    if (callback != null) {
                        callback.onPrepared(mp);
                    }
                }
            });
            currentBufferPercentage = 0;
            player.setDataSource(path);
            player.setDisplay(surfaceHolder);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setScreenOnWhilePlaying(true);
            player.prepareAsync();

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            setCurrentState(STATE_PREPARING);
        } catch (IOException | IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + path, ex);
            setCurrentState(STATE_ERROR);
            mErrorListener.onError(player, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    public void start() {
        Log.i("DDD", "start");
        if (isInPlaybackState()) {
            player.start();
            setCurrentState(STATE_PLAYING);
        }
    }

    public void restart() {
        Log.i("DDD", "restart");
        openVideo();
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (player.isPlaying()) {
                player.pause();
                setCurrentState(STATE_PAUSED);
            }
        }
    }

    public void reset() {
        if (player != null) {
            player.reset();
            player.release();
            setCurrentState(STATE_IDLE);
        }
    }

    private void setCurrentState(int state) {
        curState = state;
        if (callback != null) {
            callback.onStateChanged(curState);
            switch (state) {
                case STATE_IDLE:
                case STATE_ERROR:
                case STATE_PREPARED:
                    callback.onLoadingChanged(false);
                    break;
                case STATE_PREPARING:
                    callback.onLoadingChanged(true);
                    break;
            }
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            // TODO: 2017/6/19 = null ?
            player = null;
            surfaceHolder = null;
            setCurrentState(STATE_IDLE);
        }
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return player.getDuration();
        }

        return -1;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int progress) {
        if (isInPlaybackState()) {
            player.seekTo(progress);
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && player.isPlaying();
    }

    public int getBufferPercentage() {
        if (player != null) {
            return currentBufferPercentage;
        }
        return 0;
    }

    public boolean isInPlaybackState() {
        return (player != null &&
                curState != STATE_ERROR &&
                curState != STATE_IDLE &&
                curState != STATE_PREPARING);
    }

}
