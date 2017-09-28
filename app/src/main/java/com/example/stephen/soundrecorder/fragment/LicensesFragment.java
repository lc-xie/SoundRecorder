package com.example.stephen.soundrecorder.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.stephen.soundrecorder.R;

/**
 * Created by stephen on 17-9-27.
 * show the liscense of this app
 */

public class LicensesFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater dialogInflater=getActivity().getLayoutInflater();
        View licensesView=dialogInflater.inflate(R.layout.fragment_licenses,null);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setView(licensesView)
                .setTitle("Open source liscenses")
                .setNeutralButton("ok",null);

        return builder.create();
    }
}
