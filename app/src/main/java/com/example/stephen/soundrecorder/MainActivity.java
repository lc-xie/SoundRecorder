package com.example.stephen.soundrecorder;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.stephen.soundrecorder.adapter.FragmentAdapter;
import com.example.stephen.soundrecorder.fragment.RecordFragment;
import com.example.stephen.soundrecorder.fragment.SavedRecordingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;
    private String[] titles={"RECORD","SAVED RECORDINGS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(new RecordFragment());
        fragmentList.add(new SavedRecordingsFragment());
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentAdapter=new FragmentAdapter(fragmentManager,titles,fragmentList);
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.setting){
            Intent intent=new Intent(this,SettingActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
