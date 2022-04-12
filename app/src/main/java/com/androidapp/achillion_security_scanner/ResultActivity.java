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
                Config.first_email_timesSent = 0;
                Config.second_email_times_sent = 0;
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
            String[] resultTexts = resultText.toString().split(":");
            String region_address = null;
            String customer_email_address = null;
            String qr_code_access_code = null;
            if(resultTexts.length == 3){
               region_address = resultTexts[0];
               customer_email_address =resultTexts[1];
               qr_code_access_code = resultTexts[2];
               objTextViewName.setText(region_address + "\n" + "lat, lon: " + Config.lat +"," +Config.lon) ;
               if(Config.access_code.equals(qr_code_access_code)){

                 //  if(Config.first_email_timesSent < 1 && Config.second_email_times_sent < 1){

                      // sendEmail("O έλεγχος της περιοχής " + region_address + " ολοκληρώθηκε!  Περιοχή ελέγχου:" + Config.user_address + ", " + Config.lat + ", " + Config.lon, region_address, Config.RECEIVER_COMPANY_EMAIL_ADDRESS);
                  //     Config.first_email_timesSent ++;
                      // sendEmail("O έλεγχος της περιοχής " + region_address + " ολοκληρώθηκε!  Περιοχή ελέγχου:" + Config.user_address + ", " + Config.lat + ", " + Config.lon, region_address, customer_email_address);
                  //     Config.second_email_times_sent++;
                  // }
               }else{
                   objTextViewName.setText("Invalid access code.");
                   Toast.makeText(this, "Invalid access code." , Toast.LENGTH_SHORT).show();
               }
           } else{
               objTextViewName.setText("Not company's qr code.");
               Toast.makeText(this, "Not company's qr code." , Toast.LENGTH_SHORT).show();
           }



        }else{
            objTextViewName.setText("Nothing found");
        }
    }


    private void sendEmail(String msg, String subj, String email_Address) {
        //Getting content for email
        String email = email_Address;
        String subject = subj;
        String message = msg;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();

    }

}