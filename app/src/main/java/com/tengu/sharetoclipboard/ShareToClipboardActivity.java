package com.tengu.sharetoclipboard;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.tengu.sharetoclipboard.utils.NotificationUtil;
import com.tengu.sharetoclipboard.utils.PreferenceUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.io.text.VCardReader;
import ezvcard.parameter.VCardParameter;
import ezvcard.property.Email;
import ezvcard.property.Telephone;

public class ShareToClipboardActivity extends Activity {

    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        try{
        if((!Intent.ACTION_SEND.equals(action) && !Intent.ACTION_SEND_MULTIPLE.equals(action)) ||
                type == null) {
            finish();
            return;
        }

        if (type.startsWith("image/")) {
            if (Intent.ACTION_SEND_MULTIPLE.equals(action)){
                handleSendMultipleImages(intent);
            }
            handleSendImage(intent);
            finish();
            return;
        }

        if (!type.startsWith("text/")) {
            handleSendText(intent, R.string.error_type_not_supported_by_platform);
            finish();
            return;
        }
        switch (type) {
            case "text/plain":
                handleSendText(intent, R.string.error_no_data);
                break;
            case "text/x-vcard":
                handleSendVCard(intent);
                break;
            default:
                handleSendText(intent, R.string.error_type_not_supported);
                break;
        }}catch(Exception e){
            showToast(e.getMessage());
        }
        finish();
    }

    private void handleSendVCard(Intent intent) {
        Uri uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);

        VCard vcard;
        try {
            ContentResolver cr = getContentResolver();
            InputStream stream = cr.openInputStream(uri);
            VCardReader reader = new VCardReader(stream);
            vcard = reader.readNext();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showToast(getString(R.string.error_no_data));
            return;
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.error_no_data));
            return;
        }

        StringBuilder output = new StringBuilder();

        String fullName = vcard.getFormattedName().getValue();
        output.append(fullName);
        output.append("\n");

        for (Telephone telephone : vcard.getTelephoneNumbers()) {
            appendVCardTypes(output, telephone.getTypes());
            output.append(": ");
            output.append(telephone.getText());
            output.append("\n");
        }

        for (Email email : vcard.getEmails()) {
            appendVCardTypes(output, email.getTypes());
            output.append(": ");
            output.append(email.getValue());
            output.append("\n");
        }

        copyToClipboard(output.toString());
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void appendVCardTypes(StringBuilder output, List <? extends VCardParameter> types) {
        if (types.isEmpty()) {
            output.append(getString(R.string.other));
        } else {
            for (int i = 0; i < types.size(); i++) {
                if (i > 0) output.append(",");
                output.append(capitalize(types.get(i).toString()));
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void handleSendText(Intent intent, int err_msg) {
        String text = getSendTextString(intent);
        if (text != null) {
            copyToClipboard(text);
        } else {
            showToast(getString(err_msg));
        }
    }

    private String getSendTextString(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String sharedTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (sharedText == null && sharedTitle == null) return null;
        if (sharedText != null) {
            if (sharedTitle != null &&
                    !sharedText.contains(sharedTitle) &&
                    PreferenceUtil.shouldShowTitle(this)) {
                sharedText = String.format("%s - %s", sharedTitle, sharedText);
            }
            return sharedText;
        } else {
            return sharedTitle;
        }
    }

    public void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//        Bundle bundle = new Bundle();
        if (imageUris != null) {
            getContentResolver();
            for (int i = 0; i < Math.min(5, imageUris.size()); i++) {
                Uri uri = imageUris.get(i);
//                bundle.putString("image_source_" + i, uri.getHost());
                clipboardManager.setPrimaryClip(
                        ClipData.newUri(getContentResolver(),"URI",imageUris.get(i)));
                grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            if (imageUris.size()<5){
                showToast("Images copied to clipboard");
            }else{
                showToast("First 5 Images copied to clipboard");
            }
        }
    }

    public void handleSendImage(Intent intent) {

        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
        if (uri != null) {
            ContentResolver contentResolver = getContentResolver();
            if (contentResolver.getType(uri).startsWith("image/")) {
                clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, "URI", uri));
                showToast("Image copied to clipboard");
            }
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private void copyToClipboard(String clipboardText) {
        ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        /*        Inf Hyd Confessions 2021
        responses:-

                1. Mention your Age and Gender. ex: (26/M)
        Answer: 27 M

        2. Start your confession 😊:
        Answer: Dear all, Chala mandi melo developers or app support members ai untaru, coding ante ento teledhu Naku so koncham basics nerchukovali ani undi evaraina help chestara ( need some best tutorials/ blogs/ website ) nalanti valu coding nerchukovali anukuntaru but ardham kaka tension padtu untaru alanti vala kosam help avvachu me replies.

        Shared from "surveyheart.com"*/

        if (clipboardText.contains("confession") && clipboardText.contains("Gender")){
            clipboardText = clipboardText.replace("Answer: ","")
                    .replace("responses:-","")
                    .replace("\n\n","\n")
                    .replace("Responses:-","")
                    .replace("Inf Hyd Confessions 2021","")
                    .replace("Inf Hyd Confessions","")
                    .replace("1. Mention your Age and Gender. ex: (26/M)","")
                    .replace("2. Start your confession \uD83D\uDE0A:","")
                    .replace("Shared from \"surveyheart.com\"","")
                    .trim()
            ;
            ClipData clip = ClipData.newPlainText("text", clipboardText);
            clipboard.setPrimaryClip(clip);
            showToast("Confession Copied");
            Intent confessionViewIntent = new Intent(getApplicationContext(), ConfessionView.class);
            confessionViewIntent.putExtra("mytext",clipboardText);
            startActivity(confessionViewIntent);

        }else {

            ClipData clip = ClipData.newPlainText("text", clipboardText);
            clipboard.setPrimaryClip(clip);

            if (PreferenceUtil.shouldDisplayNotification(this)) {
                NotificationUtil.createNotification(this);
            } else {
                showToast(getString(R.string.notification_title));
            }
        }
    }
}
