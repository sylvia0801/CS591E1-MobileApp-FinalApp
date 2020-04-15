package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class RecomPriceActivity extends AppCompatActivity implements buttom_fragment.OnFragmentInteractionListener {
    private TextView price;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recom_price);

        Intent intent = getIntent();
        price = (TextView) findViewById(R.id.Price);
        mQueue = Volley.newRequestQueue(this);

        JsonParse();
    }

    public void back(View view){
        this.finish();

    }

    private void JsonParse(){
        // set the json request use url
        String url = "https://api.rainforestapi.com/request?api_key=375240EA648F42139D28C486D94155D9&type=bestsellers&url=https%3A%2F%2Fwww.amazon.com%2FBest-Sellers-Computers-Accessories-Memory-Cards%2Fzgbs%2Fpc%2F516866&output=json&language=en_US&customer_location=us";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //get the json array
                            JSONArray jsonArray = response.getJSONArray("bestsellers");

                            NumberFormat nf=new DecimalFormat( "0.0 ");
                            double average = 0.0;

                            //iterate the json array, find the price object
                            for(int i = 0; i < jsonArray.length(); ++i){
                                JSONObject seller = jsonArray.getJSONObject(i);

                                JSONObject price = seller.getJSONObject("price");
                                double value = price.getDouble("value");
                                System.out.println(value);

                                average = average + value;
                            }

                            average = Double.parseDouble(nf.format(average / 100));
                            price.setText(String.valueOf(average ));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
