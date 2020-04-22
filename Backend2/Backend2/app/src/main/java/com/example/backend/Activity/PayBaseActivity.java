package com.example.backend.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.custombrowser.CustomBrowser;
import com.payu.custombrowser.PayUCustomBrowserCallback;
import com.payu.custombrowser.bean.CustomBrowserResultData;
import com.payu.custombrowser.util.PaymentOption;
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
import com.payu.payuui.Adapter.PagerAdapter;
import com.payu.payuui.Adapter.SavedCardItemFragmentAdapter;
import com.payu.payuui.Fragment.CreditDebitFragment;
import com.payu.payuui.Fragment.SavedCardItemFragment;
import com.payu.payuui.R;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;
import com.payu.payuui.Widget.SwipeTab.SlidingTabLayout;
import com.payu.phonepe.PhonePe;
import com.payu.samsungpay.PayUSUPIPostData;
import com.payu.samsungpay.PayUSamsungPay;
import com.payu.samsungpay.PayUSamsungPayCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.description;
import static android.R.attr.key;

/**
 * This activity is where you get the payment options.
 */
public class PayBaseActivity extends FragmentActivity implements PaymentRelatedDetailsListener, ValueAddedServiceApiListener, View.OnClickListener {

    public Bundle bundle;
    private ArrayList<String> paymentOptionsList = new ArrayList<String>();
    private PayuConfig payuConfig;
    private PaymentParams mPaymentParams;
    private String merchantKey;
    private String userCredentials;
    private PayuHashes mPayUHashes;
    private PayuResponse mPayuResponse;
    private PayuUtils mPayuUtils;
    private PayuResponse valueAddedResponse;
    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private Button payNowButton;
    private PaymentOption paymentOption;
    private Spinner spinnerNetbanking;
    private String bankCode;
    private PostData postData;
    private String samPayPostData;
    private ValueAddedServiceTask valueAddedServiceTask;
    private ArrayList<StoredCard> savedCards;

    private PostData mPostData;
    private PayUSamsungPay payUSamsungPay;
    private ProgressBar mProgressBar;
    private boolean isSamsungPayAvailable = false;
    private boolean isStandAlonePhonePeAvailable = false;
    private  boolean isPaymentByPhonePe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payu_base);

        (payNowButton = (Button) findViewById(R.id.button_pay_now)).setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        bundle = getIntent().getExtras();





        if (bundle != null) {
            payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
            payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

            mPayuUtils = new PayuUtils();

            mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
            mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
            merchantKey = mPaymentParams.getKey();
            userCredentials = mPaymentParams.getUserCredentials();


            // Call back method of PayU custom browser to check availability of Samsung Pay

            PayUCustomBrowserCallback payUCustomBrowserCallback = new PayUCustomBrowserCallback() {

                @Override
                public void onCBErrorReceived(int code, String errormsg) {
                    super.onCBErrorReceived(code, errormsg);
                    Toast.makeText(PayUBaseActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void isPaymentOptionAvailable(CustomBrowserResultData resultData) {
                    isSamsungPayAvailable = true;
                    if(pagerAdapter!=null)
                        pagerAdapter.notifyDataSetChanged();
                }
            };

            //In this method we check the availability of Samsung Pay as Payment option on device being used

            new CustomBrowser().checkForPaymentAvailability(this, paymentOption.SAMSUNGPAY, payUCustomBrowserCallback, mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash(), merchantKey, userCredentials);
            new CustomBrowser().checkForPaymentAvailability(this, paymentOption.PHONEPE, payUCustomBrowserCallback, mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash(), merchantKey, userCredentials);

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

                        // lets set the visibility of progress bar
                        mProgressBar.setVisibility(View.VISIBLE);

                        GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
                        paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
                    } else {
                        Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
//                 close the progress bar
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueAddedServiceApiResponse(PayuResponse payuResponse) {
        valueAddedResponse = payuResponse;

        if (mPayuResponse != null) {
            if (mPayuResponse.isCreditCardAvailable() && mPayuResponse.isDebitCardAvailable()) {
                //Disable the pay button initially for CC/DC
                payNowButton.setEnabled(false);
            } else {
                //Enable the pay button for all other options
                payNowButton.setEnabled(true);
            }
            setupViewPagerAdapter(mPayuResponse, valueAddedResponse);
        }
    }

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;


        boolean lazypay = mPayuResponse.isLazyPayAvailable();

        if (valueAddedResponse != null)
            setupViewPagerAdapter(mPayuResponse, valueAddedResponse);

        MerchantWebService valueAddedWebService = new MerchantWebService();
        valueAddedWebService.setKey(mPaymentParams.getKey());
        valueAddedWebService.setCommand(PayuConstants.VAS_FOR_MOBILE_SDK);
        valueAddedWebService.setHash(mPayUHashes.getVasForMobileSdkHash());
        valueAddedWebService.setVar1(PayuConstants.DEFAULT);
        valueAddedWebService.setVar2(PayuConstants.DEFAULT);
        valueAddedWebService.setVar3(PayuConstants.DEFAULT);

        if ((postData = new MerchantWebServicePostParams(valueAddedWebService).getMerchantWebServicePostParams()) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            valueAddedServiceTask = new ValueAddedServiceTask(this);
            valueAddedServiceTask.execute(payuConfig);
        } else {
            if (postData != null)
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method sets us the view pager with payment options.
     *
     * @param payuResponse       contains the payment options available on the merchant key
     * @param valueAddedResponse contains the bank down status for various banks
     */
    private void setupViewPagerAdapter(final PayuResponse payuResponse, PayuResponse valueAddedResponse) {

        if (payuResponse.isResponseAvailable() && payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) { // ok we are good to go
            Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();

            if (payuResponse.isCreditCardAvailable() || payuResponse.isDebitCardAvailable()) {
                paymentOptionsList.add(SdkUIConstants.CREDIT_DEBIT_CARDS);
            }


        }


        else {
            Toast.makeText(this, "Something went wrong : " + payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), paymentOptionsList, payuResponse, valueAddedResponse);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        slidingTabLayout.setDistributeEvenly(false);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (paymentOptionsList.get(position)) {
                    case SdkUIConstants.SAVED_CARDS:
                        ViewPager myViewPager = (ViewPager) findViewById(R.id.pager_saved_card);
                        int currentPosition = ((ViewPager) findViewById(R.id.pager_saved_card)).getCurrentItem();
                        savedCards = payuResponse != null ? payuResponse.getStoredCards() : null;
                        if (savedCards != null) {
                            if (savedCards.size() == 0) {
                                payNowButton.setEnabled(false);
                                break;
                            }


                            if (savedCards.get(currentPosition).getCardType().equals("SMAE")) {
                                payNowButton.setEnabled(true);
                            } else {
                                if (mSaveFragment != null && mSaveFragment.cvvValidation()) {
                                    payNowButton.setEnabled(true);
                                } else {
                                    payNowButton.setEnabled(false);
                                }
                            }
                        }
                        break;
                    case SdkUIConstants.CREDIT_DEBIT_CARDS:
                        PagerAdapter mPagerAdapter = (PagerAdapter) viewPager.getAdapter();
                        CreditDebitFragment tempCreditDebitFragment = mPagerAdapter.getFragment(position) instanceof CreditDebitFragment ? (CreditDebitFragment) mPagerAdapter.getFragment(position) : null;
                        if (tempCreditDebitFragment != null)
                            tempCreditDebitFragment.checkData();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_pay_now) {

            mPostData = null;

            if (mPayUHashes != null)
                mPaymentParams.setHash(mPayUHashes.getPaymentHash());

            if (paymentOptionsList != null && paymentOptionsList.size() > 0) {
                switch (paymentOptionsList.get(viewPager.getCurrentItem())) {
                    case SdkUIConstants.CREDIT_DEBIT_CARDS:
                        makePaymentByCreditCard();
                        break;
                }
            }

            if (mPostData != null && mPostData.getCode() == PayuErrors.NO_ERROR) {
                payuConfig.setData(mPostData.getResult());

            }
//            else if (paymentOptionsList.get(viewPager.getCurrentItem()).equalsIgnoreCase("SAMPAY")) {
//                        payuConfig.setData(samPayPostData);
//
//            }


            Intent intent = new Intent(this, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            intent.putExtra("isStandAlonePhonePeAvailable",isStandAlonePhonePeAvailable);
            intent.putExtra("isPaymentByPhonePe",isPaymentByPhonePe);

            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            if (mPostData != null)
                Toast.makeText(this, mPostData.getResult(), Toast.LENGTH_LONG).show();

        }
    }







    private void makePaymentByCreditCard() {
        CheckBox saveCardCheckBox = (CheckBox) findViewById(R.id.check_box_save_card);
        CheckBox enableOneClickPaymentCheckBox = (CheckBox) findViewById(R.id.check_box_enable_oneclick_payment);

        if (saveCardCheckBox.isChecked()) {
            mPaymentParams.setStoreCard(1);
        } else {
            mPaymentParams.setStoreCard(0);
        }




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

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            //Lets pass the result back to previous activity
            setResult(resultCode, data);
            finish();
        }
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
