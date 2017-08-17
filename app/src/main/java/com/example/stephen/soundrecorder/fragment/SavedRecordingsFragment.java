package com.example.stephen.soundrecorder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stephen.soundrecorder.CalculateDurationToTime;
import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.adapter.FragmentAdapter;
import com.example.stephen.soundrecorder.adapter.RecordingsAdapter;
import com.example.stephen.soundrecorder.beans.RecordingFiles;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 17-8-11.
 */

public class SavedRecordingsFragment extends Fragment {

    private RecordingsAdapter recordingsAdapter;
    private RecyclerView recyclerView;
    private List<RecordingFiles> recordingFilesList;
    //文件存储地址
    private File fileDir= new File(Environment.getExternalStorageDirectory(),"/RecordFile");
    private File[] files;
    //广播
    public static final String NOTIFY_RECYCLER_VIEW="com.example.stephen.soundrecorder.action.NOTIFY_RECYCLER_VIEW";
    public static final String PLAY_DIALOG="com.example.stephen.soundrecorder.action.PLAY_DIALOG";
    private NotifyReciever notifyReciever;
    //播放界面
    PlayRecordingsFragment playRecordingsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_saved_recordings,container,false);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_recordings);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recordingFilesList=new ArrayList<>();
        files=fileDir.listFiles();
        //反向加载录音文件数据
        for (int i=(files.length-1);i>=0;i--){
            addItemToRecyclerList(files[i]);
        }
        recordingsAdapter=new RecordingsAdapter(recordingFilesList,this);
        recyclerView.setAdapter(recordingsAdapter);

        return view;
    }

    public class NotifyReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()==NOTIFY_RECYCLER_VIEW){
                files=null;
                files=fileDir.listFiles();
                recordingFilesList.clear();
                for (int i=(files.length-1);i>=0;i--){
                    addItemToRecyclerList(files[i]);
                }
            }else if (intent.getAction()==PLAY_DIALOG){
                playRecordingsFragment=PlayRecordingsFragment.newInstance(intent.getStringExtra("record_json"));
                playRecordingsFragment.show(getChildFragmentManager(),null);
            }
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                recordingsAdapter.notifyDataSetChanged();
            }
        }
    };

    public void addItemToRecyclerList(final File file){
        final String name;
        name=file.getName();
        final MediaPlayer mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepareAsync();
        } catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int length;
                long data;
                length= mediaPlayer.getDuration();
                data=file.lastModified();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                recordingFilesList.add(new RecordingFiles(name, length,simpleDateFormat.format(data)));
                Log.d("SavedRecordingsFragment","add item to list");
                mediaPlayer.release();
                //onPrepareed 会有延迟，故设置一个handler来更新adapter
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(NOTIFY_RECYCLER_VIEW);
        intentFilter.addAction(PLAY_DIALOG);
        notifyReciever=new NotifyReciever();
        getActivity().registerReceiver(notifyReciever,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(notifyReciever);
    }
}
