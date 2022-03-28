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
                Config.timesSent = 0;
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
            if(Config.timesSent < 1){

                sendEmail("O έλεγχος της περιοχής " + resultText + " ολοκληρώθηκε!", resultText.toString());
                Config.timesSent ++;
            }
        }else{
            objTextViewName.setText("Nothing found");
        }
    }


    private void sendEmail(String msg, String subj) {
        //Getting content for email
        String email = "info@achillionsecurity.gr";
        String subject = subj;
        String message = msg;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();

    }

}