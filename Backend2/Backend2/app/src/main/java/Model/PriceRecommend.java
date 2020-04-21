package Model;

import android.content.Context;
import android.widget.TextView;
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

public class PriceRecommend {
    private String url = "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords";
    private String SERVICE_VERSION = "1.0.0";
    private String SECURITY_APPNAME = "";
    private String RESPONSE_DATA_FORMAT= "JSON";
    private String keyword;
    private RequestQueue rq;
    private double MinPrice = Integer.MAX_VALUE;
    private double MaxPrice = Integer.MIN_VALUE;
    private int count = 0;
    private double average = 0;

    public PriceRecommend(String keyword, Context context) {
        this.keyword = keyword.trim().replace(" ","");
        System.out.println(this.keyword);
        System.out.println("https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=TianDing-secondha-PRD-61973ae8a-e7eab068&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&keywords=switch32gb");
        rq = Volley.newRequestQueue(context);
    }

    public void jsonParse(TextView pricetext, TextView probability, double price){
        url = url+ "&SERVICE-VERSION=" + SERVICE_VERSION + "&SECURITY-APPNAME=" + SECURITY_APPNAME + "&RESPONSE-DATA-FORMAT=" + RESPONSE_DATA_FORMAT + "&REST-PAYLOAD" + "&keywords=" + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject keywordResponse = (JSONObject)(response.getJSONArray("findItemsByKeywordsResponse").get(0));
                    JSONObject searchResult = (JSONObject)(keywordResponse.getJSONArray("searchResult").get(0));
                    JSONArray items = searchResult.getJSONArray("item");

                    for (int i = 0;i < items.length();i++){
                        JSONObject item = (JSONObject)items.get(i);
                        System.out.println(item.getJSONArray("condition").length());

                        JSONObject condition = (JSONObject)item.getJSONArray("condition").get(0);
                        System.out.println(condition);
                        System.out.println(condition.getString("conditionDisplayName").toString());

                        if(condition.get("conditionDisplayName").toString().equals("[\"Used\"]")){

                            JSONObject sellingStatus = (JSONObject)(item.getJSONArray("sellingStatus").get(0));
                            JSONObject currentPrice = (JSONObject)(sellingStatus.getJSONArray("currentPrice").get(0));
                            double price = Double.valueOf(currentPrice.get("__value__").toString());
                            if(price > MaxPrice){
                                MaxPrice = price;
                            }
                            if(price < MinPrice){
                                MinPrice = price;
                            }
                            average = average + price;
                            count = count + 1;

                        }

                    }
                    //Compute Average
                    DecimalFormat df = new DecimalFormat("#.00");
                    pricetext.setText( df.format(average / count));

                    //Compute Probability
                    double MaxMinDif = MaxPrice - MinPrice;

                    if(price > MaxPrice){
                        probability.setText("Low");
                    }
                    if(price < MinPrice){
                        probability.setText("High");
                    }

                    double probablity = 1 - ((price - MinPrice) / MaxMinDif);

                    if(probablity > 0.8){
                        probability.setText("High");
                    }
                    else if(probablity > 0.5){
                        probability.setText("Median");
                    }
                    else{
                        probability.setText("Low");
                    }

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

        rq.add(request);

    }
}
