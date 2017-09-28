package com.example.stephen.soundrecorder.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.stephen.soundrecorder.CalculateDurationToTime;
import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.beans.RecordingFiles;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;


public class PlayRecordingsFragment extends DialogFragment {

    private String recordingJson;
    private ImageView button;//播放按钮
    private TextView beginTime;
    private SeekBar seekBar;
    private RecordingFiles recordingFiles;//录音类实例
    private MediaPlayer mediaPlayer;
    private int curPosition =0;

    public static PlayRecordingsFragment newInstance(String recorderJson) {
        PlayRecordingsFragment fragment = new PlayRecordingsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("recorder_json",recorderJson);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        Bundle bundle=getArguments();
        if(bundle!=null) {
            recordingJson = bundle.getString("recorder_json");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(STYLE_NO_TITLE);//去掉标题栏
        View view= inflater.inflate(R.layout.fragment_dialog_play_recordings, container, false);
        TextView name,endTime;
        name=(TextView)view.findViewById(R.id.name_recording_play);
        beginTime=(TextView)view.findViewById(R.id.begin_time_play);
        endTime=(TextView)view.findViewById(R.id.end_time_play);
        button=(ImageView)view.findViewById(R.id.button_play);
        seekBar=(SeekBar)view.findViewById(R.id.seekbar_play);

        Gson gson=new Gson();
        recordingFiles=gson.fromJson(recordingJson,RecordingFiles.class);
        name.setText(recordingFiles.getFileName());
        endTime.setText(CalculateDurationToTime.calTime(recordingFiles.getFileLength()));
        File fileDir= new File(Environment.getExternalStorageDirectory(),"/RecordFile");
        File file=new File(fileDir,recordingFiles.getFileName());
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("PlayRecordingsFragment","MediaPlayer Error!!!");
                return false;
            }
        });
        try {
            mediaPlayer.setDataSource(file.getPath());
        }catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                curPosition=0;
                mediaPlayer.seekTo(0);
                button.setImageResource(R.drawable.ic_recordings_play);
                beginTime.setText(R.string.text_time_play);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                button.setOnClickListener(new ButtonOnClicked());
            }
        });
        seekBar.setMax(recordingFiles.getFileLength());
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
        return view;
    }

    public class ButtonOnClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                button.setImageResource(R.drawable.ic_recordings_play);
                curPosition =mediaPlayer.getCurrentPosition();
            }else {
                if (curPosition!=0){
                    mediaPlayer.seekTo(curPosition);
                }
                mediaPlayer.start();
                button.setImageResource(R.drawable.ic_recordings_pause);
            }
        }
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                beginTime.setText(CalculateDurationToTime.calTime(mediaPlayer.getCurrentPosition()));
            }
            handler.sendEmptyMessageDelayed(1,300);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}
