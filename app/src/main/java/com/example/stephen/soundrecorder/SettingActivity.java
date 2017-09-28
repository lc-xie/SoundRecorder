package com.example.stephen.soundrecorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.stephen.soundrecorder.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        LinearLayout backLinear=(LinearLayout)findViewById(R.id.linear_top_back);
        backLinear.setOnClickListener(new BackLinearOnClick());

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.set_container, new SettingFragment())
                .commit();
    }

    public class BackLinearOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
