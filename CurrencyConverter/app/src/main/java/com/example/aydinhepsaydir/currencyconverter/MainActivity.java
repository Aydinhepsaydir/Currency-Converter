package com.example.aydinhepsaydir.currencyconverter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.net.sip.SipSession;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {


    private static final String WEBSITE_URL =
            "http://apilayer.net/api/historical?access_key=d9dd615bd87b7094eefdc414e5a22c26&date=";

    // for use of calendar dialog
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH) + 1;
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        createDate(year_x, month_x, day_x);

    }

    //called when user clicks on "CHOOSE DATE" button
    // creates dialog for choosing date
    public void chooseDateClick(View view) {
        showDialog(DIALOG_ID);
    }

    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_ID){
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        } else {
            return null;
        }
    }

    // event listener for when user has chosen a date
    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;

            createDate(year_x, month_x, day_x);
        }
    };

    // put into correct format for url
    public void createDate(int year, int month, int day){
        if (day_x < 10 && month_x < 10) {
            String date = "" + year_x + "-0" + month_x + "-0" + day_x;
            TextView tv = findViewById(R.id.date);
            tv.setText(date);
        } else if (day_x < 10) {
            String date = "" + year_x + "-" + month_x + "-0" + day_x;
            TextView tv = findViewById(R.id.date);
            tv.setText(date);
        } else if (month_x < 10) {
            String date = "" + year_x + "-0" + month_x + "-" + day_x;
            TextView tv = findViewById(R.id.date);
            tv.setText(date);
        } else {
            String date = "" + year_x + "-" + month_x + "-" + day_x;
            TextView tv = findViewById(R.id.date);
            tv.setText(date);
        }
    }

    // called when the first currency is chosen
    public void chooseCurrencyClick1(View view) {
        final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                TextView currency1 = findViewById(R.id.currency_code_1);
                currency1.setText(code); // sets the currency code when chosen

                ImageView iv = findViewById(R.id.flag);
                iv.setImageDrawable(getResources().getDrawable(flagDrawableResID)); // flag img when currency chosen

                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    }

    // called when the second currency is chosen
    public void chooseCurrencyClick2(View view) {
        final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                TextView currency2 = findViewById(R.id.currency_code_2);
                currency2.setText(code); // sets the currency code when chosen

                ImageView iv = findViewById(R.id.flag_2);
                iv.setImageDrawable(getResources().getDrawable(flagDrawableResID)); //flag img when currency chosen

                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    }

    // called when the convert button is pressed
    public void convertClick(View view) {

        TextView textView = findViewById(R.id.currency_code_1);
        String countryCode1 = textView.getText().toString();

        TextView textView2 = findViewById(R.id.currency_code_2);
        String countryCode2 = textView2.getText().toString();

        downloadRates(countryCode1, countryCode2);
    }

    //gets the data from 'Currency Layer' url using ion android library
    public void downloadRates(final String countryCode1, final String countryCode2) {

        TextView tv = findViewById(R.id.date);
        String date = tv.getText().toString();

        Ion.with(this)
                .load(WEBSITE_URL + date + "&currencies=USD," + countryCode1 + "," + countryCode2 + "&format=1")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // try/catch because if amount to convert is empty then does run
                        try {
                            processResults(result, countryCode1, countryCode2);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    // this method manipulates the numbers found on the webpage to get exchange rate
    public void processResults(String result, String code1, String code2) throws Exception {

        double usd_to_fst_curr = 0.0;
        double usd_to_snd_curr = 0.0;

        // "usd_to_fst_curr" is equivalent to 1 usd
        // if code1 is USD then exchange rate is 1
        if(code1 == "USD"){
            usd_to_fst_curr = 1.0;
        } else if (code1 == "") {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "You must input a currency to convert!", LENGTH_SHORT);
            toast.show();
            throw new Exception();
        } else {
            int index1 = result.indexOf("USD" + code1); // get index of where exchange rate is on web page
            int newIndex1 = index1 + 8;
            usd_to_fst_curr = Double.parseDouble(result.substring(newIndex1, newIndex1 + 7));
        }

        // "usd_to_snd_curr" is equivalent to 1 usd
        // if code2 is USD then exchange rate will be 1
        if (code2 == "USD"){
            usd_to_snd_curr = 1.0;
        } else if (code2 == "") {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "You must input a currency to convert to!", LENGTH_SHORT);
            toast.show();
            throw new Exception();
        } else {
            int index2 = result.indexOf("USD" + code2); //get index of where exchange rate is on web page
            int newIndex2 = index2 + 8;
            usd_to_snd_curr = Double.parseDouble(result.substring(newIndex2, newIndex2 + 7));
        }

        // this value is the exchange rate between the first currency and the second
        // (1 / usd_to_fst_curr) because simultaneous equations
        double exchangeRate = (1.0 / usd_to_fst_curr) * usd_to_snd_curr;

        // amount that user wants to convert
        // must multiply this by "fst_curr_to_snd_curr"
        double amountToExchange;

        EditText et = findViewById(R.id.amount_1);
        if (et.getText().toString() == ""){
            throw new Exception();
        } else {
            amountToExchange = Double.parseDouble(et.getText().toString());
        }

        double answer = amountToExchange * exchangeRate;

        // round to two decimal places and then convert to string
        answer = Math.round(answer * 100);
        String roundedAnswer = "" + answer/100;

        EditText ed = findViewById(R.id.amount_2);
        ed.setText(roundedAnswer);
    }

    // if user wants to swap the currencies
    public void swapClick(View view) {
        TextView tv1 = findViewById(R.id.currency_code_1);
        TextView tv2 = findViewById(R.id.currency_code_2);

        String code1 = tv1.getText().toString();
        String code2 = tv2.getText().toString();

        tv1.setText(code2);
        tv2.setText(code1);

    }
}