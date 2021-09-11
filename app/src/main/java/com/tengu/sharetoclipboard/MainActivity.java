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
                confessionViewIntent.putExtra("mytext","F\n" +
                        "\n" +
                        "#636. 6years of relationship we were a happy couple.. Na 1st love, nenu kuda thana 1st love annadu..he's good at studies but records,assignments rayadam nachadhu thana 4years vi nene rasa, condonation fee emaina padina nene kattedanni,health bagalekapothe naku nidrapattedi kaadhu.Lab external ki time aipothundi thana shoe ki burada antesindi aaroju na birthday ah dress tho tudichesa.. 3rd year of relationship lo fb nunchi oka ammai thana school frnd anta msg chesindi ah abbai nuv relation lo unnara ani? Nenu may be ma cousins evaraina fake acnt tho doubt vachi aduguthunnaremo intlo telisthe godava aipoddi ledhu just close frnds annanu em ala adigav  ani ah ammai ni adiga nenu thanu relation lo unnam naku doubt vachindi nee meeda anduke adiga annadi.Nenu ah abbai ki cheppa msg kosam thanu naku school lo try chesindi nenu oppukole nee gurinchi telsi untadi chedagottadaniki ala cheppuntadi pattinchukoku annadu. 3years of relationship lo ah vishayam gurinchi eppudu cheppale Sare le ani nenu kuda pattinchukole. One fine day thana phone lo WhatsApp msg pop ayyindi I love you too ani contact name na fb ki msg chesina ammaidi nenu thanaki adagakunda ah ammai ki fb lo msg chesi adiga thanemo nenu thanu relation lo unn ani aaroje cheppa kadha ante nenu kuda cheppa 4 years of relationship ani ah ammai last week ae mem kalisam mem kiss cheskunnam kuda annadi nenu ventane thanaki call chesi burst aipoya.. Ma school frnds antha kalisam thane nannu kiss chesindi nenu kadhu na maara nammakam ledha annadu thana gurinchi naku eppudu cheppaledem ante chepthe nuvvekkada dhooram aipothavo annadu.. Tarwatha nenem anale konni days silence malli normal aipoyam nenu thana lekunda undalekapoyedanni..Tarwatha alane chinna  godavalu avi enni aina thana phone Nen eppudu check cheyle.oka rojunenu ninnu inka mosam cheyyali anukotle roju na meeda naake ahasyamga untundi I'm sorry nenu thanatho undi ninnu cheat chesa inni days ippudu nenu thanatho kuda lenu a ani naatho nuv undu ani bathimalalenu youdeserve better anesi bye cheppesadu anthe aipe.. Ah day nunchi ee day varuku daily badhapadthune unna. Intlo matches chusthunnaru naku inka ituvanti relationships meeda interest ledhu asalu intlo ela cheppali kuda teliyatle.. 6byears naatho unna manishe easyga cheat chesaru malli mosapothanemo bhayamga undi\n" +
                        "now what I'm trying to i#636 meeru abbailu kabatti ammailni restrict chesthar antaru memu ammailam memem cheyyali abbailu ila chesthe? (I agree both cases are diff but u got my point ri)");
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
