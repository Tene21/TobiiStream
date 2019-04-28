package uk.ac.dundee.computing.tobiistream;

import android.util.Log;
import org.videolan.libvlc.MediaPlayer;
import java.lang.ref.WeakReference;

class MyPlayerListener implements MediaPlayer.EventListener {

    private static String TAG = "PlayerListener";
    private WeakReference<StreamActivity> mOwner;


    public MyPlayerListener(StreamActivity owner) {
        mOwner = new WeakReference<>(owner);
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        StreamActivity player = mOwner.get();

        switch(event.type) {
            case MediaPlayer.Event.EndReached:
                Log.d(TAG, "MediaPlayerEndReached");
                player.releasePlayer();
                break;
            case MediaPlayer.Event.Playing:
            case MediaPlayer.Event.Paused:
            case MediaPlayer.Event.Stopped:
            default:
                break;
        }
    }
}