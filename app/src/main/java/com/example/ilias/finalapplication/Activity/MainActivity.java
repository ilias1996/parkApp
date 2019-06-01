package com.example.ilias.finalapplication.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ilias.finalapplication.R;
import com.example.ilias.finalapplication.SectionsPagerAdapter;
import com.example.ilias.finalapplication.SessionManager;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;
    private SessionManager session;
    private Button btnLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("App Find a Spot");

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

       // btnLog = findViewById(R.id.main_logout_btn);

      //  btnLog.setOnClickListener(new View.OnClickListener() {
        //    @Override
          //  public void onClick(View v) {

            //    session = new SessionManager(getApplicationContext());

              //  session.setLogin(false);

                //Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                //startActivity(intent);

            //}
     //   });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
