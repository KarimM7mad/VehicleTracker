package com.example.karimm7mad.vehicletracker;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class audioService extends Service {
    public MediaPlayer mpAudio;
    public static boolean notified;

    @Override
    public void onCreate() {
        super.onCreate();
        mpAudio = MediaPlayer.create(this, R.raw.alarm);
        mpAudio.setLooping(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mpAudio.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!audioService.notified) {
            mpAudio.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
