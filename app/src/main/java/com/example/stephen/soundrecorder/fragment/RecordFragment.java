package com.example.stephen.soundrecorder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stephen.soundrecorder.CalculateDurationToTime;
import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.adapter.RecordingsAdapter;
import com.example.stephen.soundrecorder.beans.RecordingFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 17-8-11.
 */

public class RecordFragment extends Fragment {

    private MediaRecorder mediaRecorder;
    private File fileDir;
    private boolean isRecording=false;
    private int timeRecording=0;
    private String fileNameAuto="record.mp3";

    private TextView textViewTime;
    private FloatingActionButton fab_normal;
    private RecordFragmentReceiver receiver;
    public static final String BEGIN_RECORDING="com.example.stephen.soundrecorder.action.BEGIN_RECORDING";
    public static final String STOP_RECORDING="com.example.stephen.soundrecorder.action.STOP_RECORDING";
    public static final String CHANGE_UI="com.example.stephen.soundrecorder.action.CHANGE_UI";
    public static final String NOTIFY_RECYCLER_VIEW="com.example.stephen.soundrecorder.action.NOTIFY_RECYCLER_VIEW";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_record,container,false);

        FragmentManager fragmentManager=getChildFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.bottom_frame_layout,new BottomNormalFragment());
        transaction.commit();
        textViewTime=(TextView)view.findViewById(R.id.time_recording);

        fileDir=new File(Environment.getExternalStorageDirectory(),"/RecordFile");
        if (!fileDir.exists())fileDir.mkdir();
        return view;
    }

    public void beginRecording(){
        File newFile=new File(fileDir,fileNameAuto);
        int i=1;
        while (newFile.exists()){
            fileNameAuto="record"+"_"+i;
            newFile=new File(fileDir,fileNameAuto+".mp3");
            i++;
        }

        try {
            newFile.createNewFile();
            mediaRecorder=new MediaRecorder();
            // 第1步：设置音频来源（MIC表示麦克风）
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //第2步：设置音频输出格式（默认的输出格式）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //第3步：设置音频编码方式（默认的编码方式）
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(newFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (IOException e){
            e.printStackTrace();
        }
        timeRecording=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording){
                    timeRecording++;
                    Intent changeUiIntent=new Intent(CHANGE_UI);
                    getActivity().sendBroadcast(changeUiIntent);
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                textViewTime.setText(calTime(timeRecording));
            }
        }
    };

    public void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        final EditText editText;
        editText=new EditText(getContext());
        editText.setText(fileNameAuto);
        editText.selectAll();

        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setTitle("输入文件名");
        dialog.setIcon(R.drawable.ic_saved_recordings_1);
        dialog.setView(editText);
        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName=editText.getText().toString();
                File nowFile=new File(fileDir,fileNameAuto+".mp3");
                if (nowFile.exists()){
                    File renameFile=new File(fileDir,fileName+".mp3");
                    nowFile.renameTo(renameFile);
                }
                Toast.makeText(getContext(),"录音文件已保存！",Toast.LENGTH_SHORT).show();
                //发送广播通知SavedRecordingsFragment更新recyclerView
                Intent notifyRecyclerViewIntent=new Intent(NOTIFY_RECYCLER_VIEW);
                getActivity().sendBroadcast(notifyRecyclerViewIntent);
            }
        });
        dialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File nowFile=new File(fileDir,fileNameAuto+".mp3");
                nowFile.delete();
                Toast.makeText(getContext(),"录音文件未保存！",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        dialog.show();
        textViewTime.setText(R.string.text_view_time_auto);
    }


    public class RecordFragmentReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentManager fragmentManager=getChildFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();

            switch (intent.getAction()){
                case BEGIN_RECORDING:
                    //开始录音
                    //先切换底部framelayout布局
                    transaction.replace(R.id.bottom_frame_layout,new BottomRecordingFragment());
                    transaction.commit();
                    isRecording=true;
                    beginRecording();
                    break;
                case STOP_RECORDING:
                    //停止录音
                    //先切换底部framelayout布局
                    transaction.replace(R.id.bottom_frame_layout,new BottomNormalFragment());
                    transaction.commit();
                    isRecording=false;
                    stopRecording();
                    break;
                case CHANGE_UI:
                    textViewTime.setText(calTime(timeRecording));
                    break;
                default:break;
            }
        }
    }

    public String calTime(int i){
        int min,sec;
        min=i/60;
        sec=i%60;
        String minStr,secStr;
        if (min==0){
            minStr="00";
        }else minStr=""+min;
        if (sec<10){
            secStr="0"+sec;
        }else secStr=""+sec;
        return minStr+":"+secStr;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BEGIN_RECORDING);
        intentFilter.addAction(STOP_RECORDING);
        intentFilter.addAction(CHANGE_UI);
        receiver=new RecordFragmentReceiver();
        getActivity().registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }
}
