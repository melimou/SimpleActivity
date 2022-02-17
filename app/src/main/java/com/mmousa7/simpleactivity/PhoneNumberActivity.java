package com.mmousa7.simpleactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberActivity extends Activity {
    protected TextView inputPrompt;
    protected EditText inputNumber;
    protected String phoneNumber;

    protected static final String phoneNumberKey = "PHONE_NUMBER" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        inputPrompt = (TextView) findViewById(R.id.input_prompt);
        inputNumber = (EditText) findViewById(R.id.input_number);

        if (savedInstanceState == null) {
            inputNumber.setText("");
        }
        else {
            // set the text in EditText element from previously saved instance
            phoneNumber = savedInstanceState.getString(phoneNumberKey);
            inputNumber.setText(phoneNumber);
            Log.i("MainActivity", "Saved state retrieved with " + phoneNumber + "number");
        }
        // Open soft keyboard once the second activity starts
        inputNumber.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputNumber, InputMethodManager.SHOW_IMPLICIT);
        inputNumber.setOnEditorActionListener(inputNumberListener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(phoneNumberKey, phoneNumber) ;
    }

    public TextView.OnEditorActionListener inputNumberListener = new TextView.OnEditorActionListener(){
        // called when "done" or "enter" key is pressed
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            Log.i("listener:", "got done");
            if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId== EditorInfo.IME_ACTION_DONE){
                phoneNumber = inputNumber.getText().toString();
                // send result + phone number back to MainActivity
                sendResultsBack(phoneNumber);
            }
            return false;
        }
    };

    private boolean validatePhoneNumber(String number){
        // validating phone number using regex patterns
        // \\d{10}: 10 digits from 0 to 9
        // (?:\d{3})\d{7}: 3 digits in () + 7 digits from 0 to 9
        // \(\d{3}\) \d{3}-?\d{4}: 3 digits in () + 3 digits, - and another 4 digits
        String pattern = "\\d{10}|" +
                "\\(\\d{3}\\)\\d{7}|" +
                "\\(\\d{3}\\) \\d{3}-?\\d{4}|" +
                "\\(\\d{3}\\)\\-\\d{7}|";
        Pattern p = Pattern.compile(pattern);
        Matcher match = p.matcher(phoneNumber);
        return match.matches();
    }

    // sending results back to MainActivity and finish the current activity
    private void sendResultsBack(String phoneNumber){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("phone_number", phoneNumber);
        if (validatePhoneNumber(phoneNumber))
            setResult(Activity.RESULT_OK, resultIntent);
        else
            setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

}
