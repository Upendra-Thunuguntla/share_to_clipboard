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

public class MainActivity extends AppCompatActivity {

    private static final String TENGU_URL = "https://stosb.com/tengu/android-apps/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConfessionView.verifyStoragePermissions(this);
        setUpToolbar();

        Button buttonOne = findViewById(R.id.ssButton);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button Clicked");
                Intent confessionViewIntent = new Intent(getApplicationContext(), ConfessionView.class);
                confessionViewIntent.putExtra("mytext","31 F\n" +
                        "\n" +
                        "I worked for Infosys till I turned 26. And then I stopped working as my marriage was settled. It was not stopped forcefully by my husband or any one. I was of the kind who didn't like this technical job. And my parents found me a US match. Just to test him, I asked him if I can work after marriage. He said it's my choice and he's not looking for anything else except caring and understanding partner. We got married, I left my job and everything was great. A guy every girl would look after. Understanding and caring. Never imposed any restriction on my freedom. Whatever I wished to do, he doesn't ever give a no. Everything was great including our sex life. After two years of my marriage, my school frnds in US planned for a get together. When I asked him the same, as expected he said you are free to go, but be careful and give a call if anything is needed. He called once every night n spoke for 5 min to check about my well being. In my school gang there's a guy named Y and I knew he had crush on me. He is kind of pervert. After 3 days of trip, we drunk. My husband allowed me to drink, but only to a certain limit and he would not allow beyond that. At that time I had it full with my husband not being there to stop me. Everyone got down. I and Y were the only ones awake. He told me that he had a crush so n so and asked about my sex life. I said it's great and while talking we got into each other and we had sex. It continued for the next 2 days and I came back to my home. Y used to call me on my num asking me to come over to his room. I denied it. It went on continuously and I blocked him. So one day he called my husband and told everything. Listening to it, my husband on tears came to me and asked if it is true. I said no and when he asked me about each n every detail, I had to accept it. He didn't talk to me for weeks. He stopped calling me. He used to eat outside. I tried and begged him a lot for his forgiveness falling on his feet, for which he says give me sometime and he was not able to take it. One fine day, he could not take it anymore broke out his silence and asked me for divorce. I begged him a lot. He said he tried his best  to continue and he is not able to live with me and promised that he won't say the reason to anyone. With no choice, we got divorced and everyone who thought us a happy perfect couple were shocked. Till date I feel like I should not have got weaken at that moment with Y.\n");
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
