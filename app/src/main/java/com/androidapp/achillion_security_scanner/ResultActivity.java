package com.androidapp.achillion_security_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    TextView objTextViewName;
    private Button goBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        goBackButton = findViewById(R.id.GoBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultActivity.this, MainActivity.class);
//Pass data to the SayHelloNewScreen Activity through the Intent

//Ask Android to start the new Activity
                startActivity(i);

            }
        });

        objTextViewName = findViewById(R.id.textViewNameNewScreen);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
//Retrieve data from the Bundle (other methods include getInt(), getBoolean() etc)
            CharSequence resultText = extras.getCharSequence("savedResultText");

//Restore the dynamic state of the UI

            objTextViewName.setText(resultText);
            sendEmail("qr code scanned");
        }else{
            objTextViewName.setText("Nothing found");
        }
    }


    @SuppressLint("LongLogTag")
    protected void sendEmail(String message) {
        Log.i("Send email", message);

        String[] TO = {"velisarios1234@gmail.com"};
        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ResultActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}