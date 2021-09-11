package com.tengu.sharetoclipboard;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

/**
 * TODO: document your custom view class.
 */
public class ConfessionView extends AppCompatActivity {

    LinearLayout backGround;
    TextView confText;
    private int maxWords = 200;
    private int currentPage = 0;
    private TreeMap<Integer,String> pages;
    private String confessionText;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confession);
        verifyStoragePermissions(this);

        backGround = (LinearLayout) findViewById(R.id.bgLayout);

        //Getting Text
        confessionText = getIntent().getStringExtra("mytext");

        //Setting text into View
        confText = (TextView)findViewById(R.id.conftextView);
//        confText.setText(confessionText);

        //Getting multiple pages in case of long confession
//        pages = processTextAutoSplit(confText,confessionText,maxWords);
        pages = processText(conf
                essionText,maxWords);
        //Long Press to take screenshot
        confText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return callScreenShot("");
            }
        });

        //If clicked on long text each part will be shown for each click
        //Screenshot is automatic in this case after second Tap
        confText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If number of lines are lesser than 22 [Square fit] just take screenshot and close
                if (confText.getLineCount() < 22 && currentPage == 0){
                    callScreenShot("");
                    killScreen();
                }

                if (currentPage>0 && currentPage <= pages.size())
                    callScreenShot(""+(currentPage));
                if (currentPage >= pages.size())
                    killScreen();

                confText.setText("");
                confText.setText(pages.get(currentPage));
                confText.clearComposingText();
                System.out.println(pages.get(currentPage));

                currentPage++;
            }
        });

        //Imitating text view for screenshot
        backGround.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return callScreenShot("");
            }
        });
        //Tap on empty space to close
        backGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killScreen();
            }
        });
    }

    /*
    To Close the Current Screen
     */
    protected void killScreen(){
        ConfessionView.super.finish();
    }

    /*
    Showing Toast with page number and
     */
    protected boolean callScreenShot(String i){
        Date date = new Date();
        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh_mm_ss", date);
        Toast.makeText(ConfessionView.this, "Captured " + i, Toast.LENGTH_SHORT).show();
        screenshot(getWindow().getDecorView().getRootView(), "Confession_"+format+"_"+i);
        return true;
    }

    /*
    Process and split text into 200 worded parts
     */
    protected TreeMap<Integer,String> processText(String confessionText, int maxWords){
        TreeMap<Integer,String> multiPageConfession = new TreeMap<Integer,String>();
        String words[] = confessionText.split(" ");
        int wordCount = words.length;
        int keyCount = (int) Math.ceil(wordCount/(double)maxWords);


        for (int i = 0; i < keyCount;i++){
            StringBuilder sb = new StringBuilder();
            for (int j=i*maxWords;j<((i+1)*maxWords < wordCount ? (i+1)*maxWords : wordCount);j++){
                sb.append(words[j]);
                sb.append(" ");
            }
            multiPageConfession.put(i, sb.toString().trim());
        }

//        System.out.println(keyCount);
        return multiPageConfession;
    }

    protected TreeMap<Integer,String> processTextAutoSplit(TextView txtView, String confessionText, int maxWords){
        TreeMap<Integer,String> multiPageConfession = new TreeMap<Integer,String>();
        String words[] = confessionText.split(" ");
        int wordCount = words.length;
//        TextView dummyView = new TextView(getApplicationContext());

        int lastWord=0,currentWord=0,mapKey=0;

        while(currentWord<=(words.length-1)){
            StringBuilder sb = new StringBuilder();

            while(txtView.getLineCount() <= 22){
                txtView.setText("");
                sb.append(words[currentWord]+" ");

                txtView.setText(sb.toString());
                System.out.println(txtView.getLineCount());
                currentWord++;
            }
            System.out.println(sb.toString());
            multiPageConfession.put(mapKey,sb.toString());
            mapKey++;
        }

        System.out.println(multiPageConfession);
        return multiPageConfession;
    }

    public static<T> T[] subArray(T[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end);
    }

    public static<T> T[] subArray(T[] array, double beg, double end) {
        return subArray(array, (int)beg, (int)end);
    }

    protected File screenshot(View view, String filename) {
        try {
            // Initialising the directory of storage
            String dirPath = Environment.getExternalStorageDirectory() + "/Confessions/";
            File confessionsDirectory = new File(dirPath);
            if (!confessionsDirectory.exists()) {
                System.out.println("Folder does not exist creating folder");
                boolean mkdir = confessionsDirectory.mkdirs();
                System.out.println(mkdir);
            }
            // File name
            String path = dirPath + "/" + filename + ".png";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageFile = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            bitmap.recycle();

            //Adding Metadata for Media scan visibility
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Photo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Edited");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, imageFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getName().toLowerCase(Locale.US));
            values.put("_data", imageFile.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return imageFile;

        } catch (FileNotFoundException io) {
            io.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // verifying if storage permission is given or not
    public static void verifyStoragePermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}