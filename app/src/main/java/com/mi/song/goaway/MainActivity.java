package com.mi.song.goaway;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.baidu.mobstat.StatService;

public class MainActivity extends AppCompatActivity {

    private MenuItem mMenuItem;
    private boolean inSetting = false;

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
                inSetting = false;
                break;
            case R.id.menu_setting: // Go SettingFragment
                startFragment(new SettingFragment(), true);
                inSetting = true;

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
        super.onBackPressed();
        if (inSetting) {
            mMenuItem.setVisible(true);
            setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            inSetting = !inSetting;
        }
    }

    private void startFragment(Fragment fragment, boolean addBackStack) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        if (addBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }
}
