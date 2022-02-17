package com.mmousa7.simpleactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    protected TextView welcome;
    protected TextView press;
    protected Button input;
    protected Button call;
    protected String phoneNumber;
    protected int result;

    protected static final String phoneNumberKey = "PHONE_NUMBER" ;
    protected static final String resultKey = "RESULT" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check orientation of the device and choose the right layout accordingly
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.activity_main_landscape);
        else
            setContentView(R.layout.activity_main);

        welcome = (TextView) findViewById(R.id.welcome_text);
        press = (TextView) findViewById(R.id.press_text);
        input = (Button) findViewById(R.id.button1);
        call = (Button) findViewById(R.id.button2);

        if (savedInstanceState == null) {
            phoneNumber = "";
        }
        else {
            phoneNumber = savedInstanceState.getString(phoneNumberKey);
            result = savedInstanceState.getInt(resultKey);
            Log.i("MainActivity", "Saved state retrieved with " + phoneNumber + "number");
        }

        input.setOnClickListener(inputListener);
        call.setOnClickListener(callListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(phoneNumberKey, phoneNumber);
        outState.putInt(resultKey, result);
    }

    public View.OnClickListener inputListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switchToPhoneNumberActivity();
        }
    };

    public View.OnClickListener callListener = new View.OnClickListener()
    {
        // Called when call button is selected
        @Override
        public void onClick(View v) {
            // check result from the second activity
            if (result == 1)
                openDialer();
            else {
                String message;
                Log.i("MainActivity", "phone number is:" + phoneNumber);
                // check if phone number is not empty
                if (phoneNumber == "" || phoneNumber.isEmpty())
                    message = "Enter a phone number first";
                else
                    message = "Invalid Phone number: ";
                message += phoneNumber;
                Toast toastMessage = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toastMessage.show();
            }
        }
    };

    private void switchToPhoneNumberActivity(){
        // start the second activity for result
        Intent i = new Intent(MainActivity.this, PhoneNumberActivity.class);
        startActivityForResult(i,1);
    }

    protected void onActivityResult(int code, int result_code, Intent i) {
        // to handle null intent: when user presses back button on second activity without entering phone number
        if (i != null) {
            super.onActivityResult(code, result_code, i);
            Log.i("MainActivity", "Returned result is: " + result_code);
            if (result_code == RESULT_OK)
                result = 1;
            else if (result_code == RESULT_CANCELED)
                result = 0;
            // check if the key phone_number exists in extra bundle
            phoneNumber = i.hasExtra("phone_number") ? i.getStringExtra("phone_number") : " ";
        }
    }
    // opening phone dialer app using implicit intent
    private void openDialer(){
        Intent call = new Intent();
        call.setAction(Intent.ACTION_DIAL);
        String data = "tel:" + phoneNumber;
        call.setData(Uri.parse(data));
        startActivity(call);
    }
}