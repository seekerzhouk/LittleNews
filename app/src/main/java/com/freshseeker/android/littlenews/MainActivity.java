package com.freshseeker.android.littlenews;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jzvd.Jzvd;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AFragment aFragment;
    private BFragment bFragment;
    private CFragment cFragment;
    private Toolbar toolbar;

    public static int navigationItemNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化AFragment
        aFragment = new AFragment();
        //把Fragment添加到Activity中
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_fl, aFragment).commitAllowingStateLoss();
        initView();
        //注册eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除eventbus注册
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        Log.i("MainActivity", "Event: ");
        int secNumber = messageEvent.getsecNumber();
        int navNumber = messageEvent.getnavNumber();
        if (2 == navNumber && secNumber == 1) {
            startService(new Intent(this, MyIntentService.class));
        }
    }

    public void initView() {
        //菜单栏
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.some_news);
        setSupportActionBar(toolbar);

//        //悬浮按钮
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        //DraweLayout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    private long mBackPressedTime;

    @Override
    public void onBackPressed() {

        if (Jzvd.backPress()) {
            return;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
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
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            navigationItemNumber = 0;
            if (aFragment == null) {
                aFragment = new AFragment();
            }
            toolbar.setTitle(R.string.some_news);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_fl, aFragment).commitAllowingStateLoss();

        } else if (id == R.id.nav_gank) {
            navigationItemNumber = 1;
            if (bFragment == null) {
                bFragment = new BFragment();
            }
            toolbar.setTitle(R.string.some_ganks);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_fl, bFragment).commitAllowingStateLoss();

        } else if (id == R.id.nav_image_video) {
            navigationItemNumber = 2;
            if (cFragment == null) {
                cFragment = new CFragment();
            }
            toolbar.setTitle(R.string.take_a_rest);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_fl, cFragment).commitAllowingStateLoss();

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.github_link));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)));
        }else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
