package com.example.musicplayerfinalproject;


import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button play, next, prev, shuffle, miniShuffle, miniRepeat;
    MediaPlayer mediaPlayer;
    TextView eTimeLabel, rTimeLabel, song_artist, song_title, songName;
    SeekBar seekBar;
    int totalTime, currentPosition;
    int songPos = 0;
    ImageView coverArt;
    boolean isShuffle = false;
    boolean isRepeat = false;
    final ArrayList<String> arrayList = new ArrayList<>();
    private ListView songView;
    String songCheck;
    private int songId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mediaPlayer.getCurrentPosition());
        outState.putInt("songPos", songPos);
        outState.putString("artist", song_artist.getText().toString());
        outState.putString("title", song_title.getText().toString());
        outState.putBoolean("isShuffle", isShuffle);
        outState.putBoolean("isRepeat", isRepeat);
        outState.putString("songCheck", songCheck);
        outState.putInt("totalTime", totalTime);
        outState.putInt("currentPos", currentPosition);
        outState.putInt("songId", songId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mediaPlayer.seekTo(savedInstanceState.getInt("position", 0));
        songPos = savedInstanceState.getInt("songPos", 0);
        totalTime = savedInstanceState.getInt("totalTime", 0);
        song_artist.setText(savedInstanceState.getString("artist", ""));
        song_title.setText(savedInstanceState.getString("title", ""));
        songCheck = savedInstanceState.getString("songCheck", "");
        isShuffle = savedInstanceState.getBoolean("isShuffle", false);
        isRepeat = savedInstanceState.getBoolean("isRepeat", false);
        songId = savedInstanceState.getInt("songId", 0);
        if (mediaPlayer.isPlaying()) {
            play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        } else {
            play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
        currentPosition = savedInstanceState.getInt("currentPos", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TheService theService = new TheService();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        song_artist = (TextView) findViewById(R.id.song_artist);
        song_title = (TextView) findViewById(R.id.song_title);
        coverArt = (ImageView) findViewById(R.id.coverArt);

        songView = (ListView) findViewById(R.id.song_list);
        songName = (TextView) findViewById(R.id.textView);

        //loop for arrayList
        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            arrayList.add(fields[i].getName());
        }

        SonglistAdapter songlistAdapter = new SonglistAdapter(this, R.layout.custom_layout, arrayList);
        songView.setAdapter(songlistAdapter);

        Uri.Builder builder = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI.buildUpon();


        miniShuffle = (Button) findViewById(R.id.miniShuffle);
        miniShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShuffle) {
                    isShuffle = true;
                    miniShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                } else {
                    isShuffle = false;
                    miniShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });

        miniRepeat = (Button) findViewById(R.id.miniRepeat);
        miniRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRepeat) {
                    isRepeat = true;
                    miniRepeat.setBackgroundResource(R.drawable.ic_repeat_on);
                } else {
                    isRepeat = false;
                    miniRepeat.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                }
            }
        });

        shuffle = (Button) findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShuffle = true;
                miniShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                int max = arrayList.size() - 1;
                Random r = new Random();
                songPos = r.nextInt(((max - 0) + 1) + 0);
                getSong();
                playSettings();
                playSong();
            }
        });

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                Resources res = getApplicationContext().getResources();
                if (isShuffle) {
                    int max = arrayList.size() - 1;
                    Random r = new Random();
                    songPos = r.nextInt(((max - 0) + 1) + 0);
                } else {
                    if (songPos == arrayList.size() - 1) {
                        songPos = 0;
                    } else songPos++;
                }
                getSong();
                playSettings();
                playSong();
            }
        });

        prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getApplicationContext().getResources();
                if (songPos == 0) {
                    songPos = arrayList.size() - 1;
                } else {
                    songPos--;
                }
                mediaPlayer.stop();
                getSong();
                playSettings();
                playSong();
            }
        });

        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Resources res = getApplicationContext().getResources();
                songCheck = arrayList.get(position);
                songPos = position;
                updateSongInfo();
                int songId = res.getIdentifier(songCheck, "raw", getApplicationContext().getPackageName());
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);
                    playSettings();
                    playSong();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);
                    playSettings();
                    playSong();
                }

            }
        });

        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    playSong();
                } else {
                    mediaPlayer.pause();
                    play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
            }
        });

        eTimeLabel = (TextView) findViewById(R.id.eTime);
        rTimeLabel = (TextView) findViewById(R.id.rTime);

        getSong();
        playSettings();
    }


    //METHODS
    public void getSong() {
        Resources res = getApplicationContext().getResources();
        songCheck = arrayList.get(songPos);
        songId = res.getIdentifier(songCheck, "raw", getApplicationContext().getPackageName());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songId);
        updateSongInfo();
    }

    public void updateSongInfo() {

        if (String.valueOf(songCheck).equals("all_good")) {
            song_title.setText("All Good");
            song_artist.setText("Capital Kings");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.allgood);
        } else if (String.valueOf(songCheck).equals("all_falls_down")) {
            song_title.setText("All Falls Down");
            song_artist.setText("Alan Walker");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.afd);
        } else if (String.valueOf(songCheck).equals("alive")) {
            song_title.setText("Alive");
            song_artist.setText("Stargazer");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.strgzr);
        } else if (String.valueOf(songCheck).equals("get_low")) {
            song_title.setText("Get Low");
            song_artist.setText("Zedd");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.getlow);
        } else if (String.valueOf(songCheck).equals("havana")) {
            song_title.setText("Havana");
            song_title.setTextSize(40);
            song_artist.setText("Camila Cabello");
            coverArt.setImageResource(R.mipmap.havana);
        } else if (String.valueOf(songCheck).equals("how_long")) {
            song_title.setText("How Long");
            song_title.setTextSize(40);
            song_artist.setText("Charlie Puth");
            coverArt.setImageResource(R.mipmap.howlong);
        } else if (String.valueOf(songCheck).equals("light_wrld_remix")) {
            song_title.setText("Light (WRLD REMIX)");
            song_title.setTextSize(30);
            song_artist.setText("San Holo");
            coverArt.setImageResource(R.mipmap.light);
        }else if (String.valueOf(songCheck).equals("castle_on_the_hill")) {
            song_title.setText("Castle on the Hill");
            song_artist.setText("Ed Sheeran");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.castle);
        }else if (String.valueOf(songCheck).equals("let_me_go")) {
            song_title.setText("Let Me Go");
            song_artist.setText("Hailee Steinfield");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.lemmego);
        }else if (String.valueOf(songCheck).equals("cold")) {
            song_title.setText("Cold");
            song_artist.setText("Rich Brian");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.cold);
        }else if (String.valueOf(songCheck).equals("sober")) {
            song_title.setText("Sober");
            song_artist.setText("Cheat Codes");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.sober);
        }else if (String.valueOf(songCheck).equals("smth_just_like_this")) {
            song_title.setText("Something Just Like This\n(Don Diablo Remix)");
            song_title.setTextSize(25);
            song_artist.setText("The Chainsmokers");
            coverArt.setImageResource(R.mipmap.smthjustlikethis);
        }else if (String.valueOf(songCheck).equals("you_n_i")) {
            song_title.setText("You & I");
            song_artist.setText("Mike Williams");
            song_title.setTextSize(40);
            coverArt.setImageResource(R.mipmap.youni);
        }else if (String.valueOf(songCheck).equals("found_you")) {
            song_title.setText("Found You");
            song_title.setTextSize(40);
            song_artist.setText("Kasbo");
            coverArt.setImageResource(R.mipmap.foundu);
        }
    }

    public void playSong() {
        mediaPlayer.start();
        play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isRepeat) {
                    getSong();
                    playSong();
                    playSettings();
                } else {
                    if (isShuffle) {
                        int max = arrayList.size() - 1;
                        Random r = new Random();
                        songPos = r.nextInt(((max - 0) + 1) + 0);
                    } else {
                        if (songPos == arrayList.size() - 1) {
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

    public void playSettings() {
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f);
        totalTime = mediaPlayer.getDuration();

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(totalTime);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
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
                while (mediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //update seekbar
            currentPosition = msg.what;
            seekBar.setProgress(currentPosition);
            //update labels
            String eTime = createTimeLabel(currentPosition);
            eTimeLabel.setText(eTime);

            String rTime = createTimeLabel(totalTime - currentPosition);
            rTimeLabel.setText("- " + rTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
