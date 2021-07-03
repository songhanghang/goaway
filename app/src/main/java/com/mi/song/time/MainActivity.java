package com.mi.song.time;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity {

    private android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // statistics
        startFragment(new MainFragment(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // for miui，miui dev version will leak activity，miui internal is processing fix，Emmmmmm...
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
    }

    protected void startFragment(Fragment fragment, boolean addBackStack) {
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(android.R.id.content, fragment, fragment.getClass().getName());
        if (addBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }
}
