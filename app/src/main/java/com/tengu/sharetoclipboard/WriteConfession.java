package com.tengu.sharetoclipboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WriteConfession extends AppCompatActivity {

    EditText writeConfession;
    Button designConfession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_confession);
        writeConfession = findViewById(R.id.writeConfession);
        designConfession = findViewById(R.id.makeConfession);
//        ((EditText)findViewById(R.id.writeConfession)).setText("Sample Text");

        designConfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent makeConfession = new Intent(getApplicationContext(),ConfessionView.class);
                String conf = writeConfession.getText().toString();

                makeConfession.putExtra("mytext",conf);
                startActivity(makeConfession);
            }
        });
    }
}
