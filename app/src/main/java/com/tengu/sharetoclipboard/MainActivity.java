package com.tengu.sharetoclipboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tengu.sharetoclipboard.utils.PreferenceUtil;
import com.tengu.sharetoclipboard.utils.Tools;

public class MainActivity extends AppCompatActivity {

    private static final String TENGU_URL = "https://stosb.com/tengu/android-apps/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConfessionView.verifyStoragePermissions(this);
        setUpToolbar();

        findViewById(R.id.designConfession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),WriteConfession.class));
            }
        });

        Button buttonOne = findViewById(R.id.ssButton);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button Clicked");
                Intent confessionViewIntent = new Intent(getApplicationContext(), ConfessionView.class);
                confessionViewIntent.putExtra("mytext", Tools.sample1);
                startActivity(confessionViewIntent);

            }});

        findViewById(R.id.cardViewTengu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(TENGU_URL));
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonRemoveLauncher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.remove_launcher)
                        .setMessage(R.string.remove_launcher_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Removes the launcher icon
                                PackageManager p = getPackageManager();
                                p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
            }
        });

        initShowTitleCheckBox();

        initDisplayNotificationCheckBox();
    }

    private void initShowTitleCheckBox() {
        CheckBox cb = findViewById(R.id.showTitleOnShare);
        cb.setChecked(PreferenceUtil.shouldShowTitle(this));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtil.setShowTitle(MainActivity.this, isChecked);
            }
        });
    }

    private void initDisplayNotificationCheckBox() {
        CheckBox cb = findViewById(R.id.displayNotification);
        cb.setChecked(PreferenceUtil.shouldDisplayNotification(this));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtil.setDisplayNotification(MainActivity.this, isChecked);
            }
        });
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary600));
        }
    }

}
