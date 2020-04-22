package com.example.backend.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.backend.R;
import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Interfaces.ValueAddedServiceApiListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.payu.india.Tasks.ValueAddedServiceTask;

import DAO.SdkUIConstants;

/**
 * This activity is where you get the payment options.
 */
public class PayBaseActivity extends AppCompatActivity {
    public Bundle bundle;
    private PayuConfig payuConfig;
    private PaymentParams mPaymentParams;
    private String merchantKey;
    private String userCredentials;
    private PayuHashes mPayUHashes;
    private PayuResponse mPayuResponse;
    private PayuUtils mPayuUtils;
    private PayuResponse valueAddedResponse;
    private Button payNowButton;
    private PostData postData;
    private ValueAddedServiceTask valueAddedServiceTask;

    private PostData mPostData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_base);

        payNowButton = (Button) findViewById(R.id.button_pay_now);
        payNowButton.setEnabled(false);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
            payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

            mPayuUtils = new PayuUtils();

            mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
            mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
            merchantKey = mPaymentParams.getKey();
            userCredentials = mPaymentParams.getUserCredentials();


            ((TextView) findViewById(R.id.textview_amount)).setText(SdkUIConstants.AMOUNT + ": " + mPaymentParams.getAmount());
            ((TextView) findViewById(R.id.textview_txnid)).setText(SdkUIConstants.TXN_ID + ": " + mPaymentParams.getTxnId());

            if (mPaymentParams != null && mPayUHashes != null && payuConfig != null) {
                /**
                 * Below merchant webservice is used to get all the payment options enabled on the merchant key.
                 */
                MerchantWebService merchantWebService = new MerchantWebService();
                merchantWebService.setKey(mPaymentParams.getKey());
                merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
                merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());

                merchantWebService.setHash(mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash());

                // fetching for the first time.
                if (null == savedInstanceState) { // dont fetch the data if its been called from payment activity.
                    PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
                    if (postData.getCode() == PayuErrors.NO_ERROR) {
                        // ok we got the post params, let make an api call to payu to fetch the payment related details
                        payuConfig.setData(postData.getResult());

                        GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask((PaymentRelatedDetailsListener) this);
                        paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
                    } else {
                        Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_SHORT).show();
        }

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePaymentByCreditCard();
            }
        });
    }

    private void makePaymentByCreditCard() {

        // lets try to get the post params
        mPaymentParams.setCardNumber(((EditText) findViewById(R.id.edit_text_card_number)).getText().toString().replace(" ", ""));
        mPaymentParams.setNameOnCard(((EditText) findViewById(R.id.edit_text_name_on_card)).getText().toString());
        mPaymentParams.setExpiryMonth(((EditText) findViewById(R.id.edit_text_expiry_month)).getText().toString());
        mPaymentParams.setExpiryYear(((EditText) findViewById(R.id.edit_text_expiry_year)).getText().toString());
        mPaymentParams.setCvv(((EditText) findViewById(R.id.edit_text_card_cvv)).getText().toString());

        if (mPaymentParams.getStoreCard() == 1 && !((EditText) findViewById(R.id.edit_text_card_label)).getText().toString().isEmpty())
            mPaymentParams.setCardName(((EditText) findViewById(R.id.edit_text_card_label)).getText().toString());
        else if (mPaymentParams.getStoreCard() == 1 && ((EditText) findViewById(R.id.edit_text_card_label)).getText().toString().isEmpty())
            mPaymentParams.setCardName(((EditText) findViewById(R.id.edit_text_name_on_card)).getText().toString());

        try {
            mPostData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mPostData != null && mPostData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(mPostData.getResult());

        }


        Intent intent = new Intent(this, MainPageActivity.class);
        Toast.makeText(getApplicationContext(), "Your payment was successful.", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }


}