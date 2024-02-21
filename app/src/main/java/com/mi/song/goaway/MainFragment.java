package com.mi.song.goaway;


import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mi.song.goaway.util.TimeUtil;

import java.io.File;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 2323;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int[] colors = TimeUtil.getColorArray(getActivity());

        View textView = view.findViewById(R.id.info);
        View payBtn = view.findViewById(R.id.pay_btn);
        View noteBtn = view.findViewById(R.id.note_btn);
        View usageBtn = view.findViewById(R.id.usage_btn);
        View useBtn = view.findViewById(R.id.use_btn);

        textView.setBackgroundColor(colors[0]);
        payBtn.setBackgroundColor(colors[1]);
        noteBtn.setBackgroundColor(colors[2]);
        usageBtn.setBackgroundColor(colors[3]);
        useBtn.setBackgroundColor(colors[4]);

        payBtn.setOnClickListener(this);
        noteBtn.setOnClickListener(this);
        usageBtn.setOnClickListener(this);
        useBtn.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (requestCode == REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                wechatPay();
            } else {
                Toast.makeText(activity, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.use_btn) {
            use();
        } else if (id == R.id.pay_btn) {
            pay();
        } else if (id == R.id.note_btn) {
            note();
        } else if (id == R.id.usage_btn) {
            usage();
        }
    }

    private void use() {
//        try {
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getActivity(), GoAwayWallpaperService.class));
            startActivityForResult(intent, 100);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void usage() {
        ((MainActivity)getActivity()).startFragment(new UsageStatisticsFragment(), true);
    }

    private void pay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void note() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/songhanghang/goaway/blob/master/README.md"));
        startActivity(intent);
    }

    private void wechatPay() {
        //检测微信是否安装
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (!WeiXinDonate.hasInstalledWeiXinClient(activity)) {
            Toast.makeText(activity, "未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //已经有权限
            new AlertDialog.Builder(activity)
                    .setTitle("微信捐赠操作步骤")
                    .setMessage("点击确定按钮后会跳转微信扫描二维码界面：\n\n" + "1. 点击右上角的菜单按钮\n\n" + "2. 点击'从相册选取二维码'\n\n" + "3. 选择第一张二维码图片即可\n\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InputStream weixinQrIs = getResources().openRawResource(R.raw.didikee_weixin);
                            String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AndroidDonateSample" + File.separator +
                                    "didikee_weixin.png";
                            WeiXinDonate.saveDonateQrImage2SDCard(qrPath, BitmapFactory.decodeStream(weixinQrIs));
                            WeiXinDonate.donateViaWeiXin(activity, qrPath);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    private void aliPay() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(activity);
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(activity, "FKX08327O1EEEDGRVIWIFB");
        } else {
            Toast.makeText(activity, "未安装支付宝客户端", Toast.LENGTH_SHORT).show();
        }
    }
}
