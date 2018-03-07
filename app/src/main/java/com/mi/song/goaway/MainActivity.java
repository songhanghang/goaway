package com.mi.song.goaway;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;

public class MainActivity extends AppCompatActivity {

    private MenuItem mMenuItem;
    private android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // statistics
        StatService.start(this);
        startFragment(new MainFragment(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenuItem = menu.findItem(R.id.menu_setting);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Go back MainFragment
                onBackPressed();
                break;
            case R.id.menu_setting: // Go SettingFragment
                startFragment(new SettingFragment(), true);

                mMenuItem.setVisible(false);
                setTitle(R.string.menu_setting);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // for miui，miui dev version will leak activity，miui internal is processing fix，Emmmmmm...
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
    }

    /**
     * When back press in SettingFragment, back to MainFragment
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = fm.findFragmentByTag(SettingFragment.class.getName());
        if (fragment != null && fragment.isVisible()) {
            mMenuItem.setVisible(true);
            setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        super.onBackPressed();
    }

    public void startFragment(Fragment fragment, boolean addBackStack) {
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.main_container, fragment, fragment.getClass().getName());
        if (addBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }
}
