package com.example.musicplayerfinalproject;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.example.musicplayerfinalproject.MainActivity;

import java.util.Random;

public class TheService extends Service {
    MediaPlayer mediaPlayer;
    MainActivity main = new MainActivity();
    int songPos = 0;
    int totalTime;
    boolean isRepeat = false;
    boolean isShuffle = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void playSong(){
        mediaPlayer.start();
        main.play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp){
                if(main.isRepeat){
                    getSong();
                    playSong();
                    playSettings();
                } else{
                    if(main.isShuffle){
                        int max = main.arrayList.size() - 1;
                        Random r = new Random();
                        songPos = r.nextInt(((max - 0) + 1) + 0);
                    } else {
                        if(songPos == main.arrayList.size() - 1){
                            songPos = 0;
                        } else songPos++;
                    }
                    getSong();
                    playSong();
                    playSettings();
                }
            }
        });
    }

    /*public void nextSong(){
        mediaPlayer.stop();
        if(main.isShuffle){
            int max = main.arrayList.size() - 1;
            Random r = new Random();
            songPos = r.nextInt(((max - 0) + 1) + 0);
        } else {
            if(songPos == main.arrayList.size() - 1){
                songPos = 0;
            } else songPos++;
        }
        getSong();
        playSettings();
        playSong();
    }
*/

    public void getSong(){
        Resources res = getApplicationContext().getResources();
        String a = main.arrayList.get(songPos);
        int songId = res.getIdentifier(a,"raw", getApplicationContext().getPackageName());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);
    }

    public void playSettings(){
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f,0.5f);
        totalTime = mediaPlayer.getDuration();

        main.seekBar = (SeekBar) main.findViewById(R.id.seekBar);
        main.seekBar.setMax(totalTime);
        main.seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            mediaPlayer.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mediaPlayer != null){
                    try{
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {}
                }
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //update seekbar
            int currentPosition = msg.what;
            main.seekBar.setProgress(currentPosition);
            //update labels
            String eTime = createTimeLabel(currentPosition);
            main.eTimeLabel.setText(eTime);

            String rTime = createTimeLabel(totalTime - currentPosition);
            main.rTimeLabel.setText("- " + rTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if(sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}
