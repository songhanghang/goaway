package com.mi.song.goaway;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.didikee.donate.AlipayDonate;
import android.didikee.donate.WeiXinDonate;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public static final int REQUEST_CODE = 2323;
    public static final int MAIN = 0;
    public static final int SETTING = 1;

    private MainFragment mainFragment;
    private SettingFragment settingFragment;
    private int nowFragment = MAIN;

    private MenuItem settingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // statistics
        StatService.start(this);
        // get screen real height and width
        ScreenUtil.calcScreenParams(this);
        switchFragment(MAIN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // for miui，miui dev version will leak activity，miui internal is processing fix，Emmmmmm...
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                wechatPay();
            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        settingItem = menu.findItem(R.id.menu_setting);
        settingItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                switchFragment(SETTING);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        if (nowFragment == SETTING) {
            Log.d(TAG, "onBackPressed: true");
            settingItem.setVisible(true);
            nowFragment = MAIN;
        }
    }

    private void switchFragment(int key) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        if (key == MAIN) {
            if (mainFragment == null) {
                mainFragment = new MainFragment();
            }

            if (settingItem != null) {
                settingItem.setVisible(true);
            }
            transaction.replace(R.id.main_container, mainFragment);
            nowFragment = MAIN;
        } else if (key == SETTING) {
            if (settingFragment == null) {
                settingFragment = new SettingFragment();
            }
            settingItem.setVisible(false);
            transaction.replace(R.id.main_container, settingFragment);
            nowFragment = SETTING;
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void use(View view) {
        try {
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
                    , new ComponentName(getApplicationContext().getPackageName()
                            , GoAwayWallpaperService.class.getCanonicalName()));

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"支付宝", "微信"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    aliPay();
                } else {
                    wechatPay();
                }
            }
        });
        builder.create().show();
    }

    public void look(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/songhanghang/goaway/blob/master/README.md"));
        startActivity(intent);
    }

    private void wechatPay() {
        //检测微信是否安装
        if (!WeiXinDonate.hasInstalledWeiXinClient(this)) {
            Toast.makeText(this, "未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //已经有权限
            new AlertDialog.Builder(this)
                    .setTitle("微信捐赠操作步骤")
                    .setMessage("点击确定按钮后会跳转微信扫描二维码界面：\n\n" + "1. 点击右上角的菜单按钮\n\n" + "2. 点击'从相册选取二维码'\n\n" + "3. 选择第一张二维码图片即可\n\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InputStream weixinQrIs = getResources().openRawResource(R.raw.didikee_weixin);
                            String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AndroidDonateSample" + File.separator +
                                    "didikee_weixin.png";
                            WeiXinDonate.saveDonateQrImage2SDCard(qrPath, BitmapFactory.decodeStream(weixinQrIs));
                            WeiXinDonate.donateViaWeiXin(MainActivity.this, qrPath);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    private void aliPay() {
        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this);
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this, "FKX08327O1EEEDGRVIWIFB");
        } else {
            Toast.makeText(this, "未安装支付宝客户端", Toast.LENGTH_SHORT).show();
        }
    }

}
