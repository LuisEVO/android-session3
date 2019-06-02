package com.levo.session3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_RECORD_AUDIO = 1;
    static final String LOG_TAG = "AudioRecorder";

    String fileName = null;
    boolean recording = false;
    boolean playing = false;
    boolean permissionGranted = false;

    Button btRecord, btPlay;
    MediaRecorder recorder = null;
    MediaPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btRecord = findViewById(R.id.btRecord);
        btPlay = findViewById(R.id.btPlay);
        btPlay.setEnabled(false);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName = fileName + "/audioRecorder.3gp";


        btRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionGranted){
                    if (!recording) {
                        startRecording();
                    } else {
                        stopRecording();
                    }
                } else {
                    checkPermission();
                }
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });
    }



    private void startPlaying() {
        try {
            player = new MediaPlayer();

            player.setDataSource(fileName);
            player.prepare();
            player.start();

            btPlay.setText("Stop");
            this.playing = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }



    }

    private void stopPlaying() {

        player.stop();
        player.release();
        player = null;

        btPlay.setText("Play");
        this.playing = false;

    }


    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        recorder.setOutputFile(fileName);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
            //e.printStackTrace();
        }

        recorder.start();

        btRecord.setText("Detener");
        this.recording = true;

    }


    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        btPlay.setEnabled(true);
        btRecord.setText("Grabar");
        this.recording = false;

    }


    private void checkPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO) {
            permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionGranted){
                startRecording();
            } else {
                finish();
            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
