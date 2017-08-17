package com.example.stephen.soundrecorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stephen.soundrecorder.R;

/**
 * Created by stephen on 17-8-12.
 */

public class BottomRecordingFragment extends Fragment {
    public static final String STOP_RECORDING="com.example.stephen.soundrecorder.action.STOP_RECORDING";

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_layout_recoroding,container,false);
        FloatingActionButton fab_stop_recording;
        fab_stop_recording =(FloatingActionButton)view.findViewById(R.id.fab_stop_recording);
        fab_stop_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(STOP_RECORDING);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().sendBroadcast(intent);
            }
        });
        return view;
    }
}
