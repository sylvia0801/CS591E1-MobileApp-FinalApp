package com.example.backend.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backend.R;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.PaymentPostParams;

import Model.Item;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private Button payNowButton;
    private EditText cardNameEditText;
    private EditText cardNumberEditText;
    private EditText cardCvvEditText;
    private EditText cardExpiryMonthEditText;
    private EditText cardExpiryYearEditText;
    private Bundle bundle;

    private String cardName;
    private String cardNumber;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;

    private PayuHashes mPayuHashes;
    private PaymentParams mPaymentParams;
    private PostData postData;

    private TextView amountTextView;
    private TextView transactionIdTextView;
    private PayuConfig payuConfig;

    private PayuUtils payuUtils;

    Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        (payNowButton = (Button) findViewById(R.id.button_card_make_payment)).setOnClickListener(this);

        cardNameEditText = (EditText) findViewById(R.id.edit_text_name_on_card);
        cardNumberEditText = (EditText) findViewById(R.id.edit_text_card_number);
        cardCvvEditText = (EditText) findViewById(R.id.edit_text_card_cvv);
        cardExpiryMonthEditText = (EditText) findViewById(R.id.edit_text_expiry_month);
        cardExpiryYearEditText = (EditText) findViewById(R.id.edit_text_expiry_year);


        //TODO item gets parsed to database
        bundle = getIntent().getExtras();
        item = bundle.getParcelable("payitem");

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("CardActivity");
                System.out.println(item);
                Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
            }
        });


        // lets get payment default params and hashes
        mPayuHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

        (amountTextView = (TextView) findViewById(R.id.text_view_amount)).setText(PayuConstants.AMOUNT + ": " + mPaymentParams.getAmount());
        (transactionIdTextView = (TextView) findViewById(R.id.text_view_transaction_id)).setText(PayuConstants.TXNID + ": " + mPaymentParams.getTxnId());


        payuUtils = new PayuUtils();


        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            String issuer;
            Drawable issuerDrawable;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 5){ // to confirm rupay card we need min 6 digit.
                    if(null == issuer) issuer = payuUtils.getIssuer(charSequence.toString());
                    if (issuer != null && issuer.length() > 1 && issuerDrawable == null){
                        issuerDrawable = getIssuerDrawable(issuer);
                        if(issuer.contentEquals(PayuConstants.SMAE)){ // hide cvv and expiry
                            cardExpiryMonthEditText.setVisibility(View.GONE);
                            cardExpiryYearEditText.setVisibility(View.GONE);
                            cardCvvEditText.setVisibility(View.GONE);
                        }else{ //show cvv and expiry
                            cardExpiryMonthEditText.setVisibility(View.VISIBLE);
                            cardExpiryYearEditText.setVisibility(View.VISIBLE);
                            cardCvvEditText.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    issuer = null;
                    issuerDrawable = null;
                }
                cardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        // Oh crap! Resource IDs cannot be used in a switch statement in Android library modules less... (Ctrl+F1)
        // Validates using resource IDs in a switch statement in Android library module
        // we cant not use switch and gotta use simple if else
        if (v.getId() == R.id.button_card_make_payment) {
            // setup the hash
            mPaymentParams.setHash(mPayuHashes.getPaymentHash());
            System.out.println(mPayuHashes.getPaymentHash());

            // lets try to get the post params

            postData = null;
            // lets get the current card number;
            cardNumber = String.valueOf(cardNumberEditText.getText());
            cardName = cardNameEditText.getText().toString();
            expiryMonth = cardExpiryMonthEditText.getText().toString();
            expiryYear = cardExpiryYearEditText.getText().toString();
            cvv = cardCvvEditText.getText().toString();

            // lets not worry about ui validations.
            mPaymentParams.setCardNumber(cardNumber);
            mPaymentParams.setCardName(cardName);
            mPaymentParams.setNameOnCard(cardName);
            mPaymentParams.setExpiryMonth(expiryMonth);
            mPaymentParams.setExpiryYear(expiryYear);
            mPaymentParams.setCvv(cvv);
            try {
                postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // okay good to go.. lets make a transaction
                // launch webview
                payuConfig.setData(postData.getResult());
                Intent intent = new Intent(this, PaymentsActivity.class);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
            } else {
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private Drawable getIssuerDrawable(String issuer){

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            switch (issuer) {
                case PayuConstants.VISA:
                    return getResources().getDrawable(R.drawable.visa);
                case PayuConstants.LASER:
                    return getResources().getDrawable(R.drawable.laser);
                case PayuConstants.DISCOVER:
                    return getResources().getDrawable(R.drawable.discover);
                case PayuConstants.MAES:
                    return getResources().getDrawable(R.drawable.maestro);
                case PayuConstants.MAST:
                    return getResources().getDrawable(R.drawable.master);
                case PayuConstants.AMEX:
                    return getResources().getDrawable(R.drawable.amex);
                case PayuConstants.DINR:
                    return getResources().getDrawable(R.drawable.diner);
                case PayuConstants.JCB:
                    return getResources().getDrawable(R.drawable.jcb);
                case PayuConstants.SMAE:
                    return getResources().getDrawable(R.drawable.maestro);
                case PayuConstants.RUPAY:
                    return getResources().getDrawable(R.drawable.rupay);
            }
            return null;
        }else {

            switch (issuer) {
                case PayuConstants.VISA:
                    return getResources().getDrawable(R.drawable.visa, null);
                case PayuConstants.LASER:
                    return getResources().getDrawable(R.drawable.laser, null);
                case PayuConstants.DISCOVER:
                    return getResources().getDrawable(R.drawable.discover, null);
                case PayuConstants.MAES:
                    return getResources().getDrawable(R.drawable.maestro, null);
                case PayuConstants.MAST:
                    return getResources().getDrawable(R.drawable.master, null);
                case PayuConstants.AMEX:
                    return getResources().getDrawable(R.drawable.amex, null);
                case PayuConstants.DINR:
                    return getResources().getDrawable(R.drawable.diner, null);
                case PayuConstants.JCB:
                    return getResources().getDrawable(R.drawable.jcb, null);
                case PayuConstants.SMAE:
                    return getResources().getDrawable(R.drawable.maestro, null);
                case PayuConstants.RUPAY:
                    return getResources().getDrawable(R.drawable.rupay, null);
            }
            return null;
        }
    }
}
