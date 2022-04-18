package com.example.golfapp;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to create a JSON payload that can be sent to the machine learning
 * algorithm to get a recommendation
 */

public class PayloadGenerator {

    private String url = "http://c2b3da2d-3bca-4cd1-b562-58ab936f551a.westeurope.azurecontainer.io/score";

    // create the json object that can be sent
    public JsonObjectRequest createJSONPayload(String distance) throws JSONException {

        JSONObject firstObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        firstObject.put("distance", distance);
        jsonArray.put(firstObject);
        JSONObject secondObject = new JSONObject();
        secondObject.put("data", jsonArray);
        JSONObject finalObject = new JSONObject();
        finalObject.put("Inputs", secondObject);
        System.out.println(finalObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, finalObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String responseText = null;
                try {
                    responseText = response.getString("Results");
                    responseText = responseText.replace("[","");
                    responseText = responseText.replace("]","");
                    responseText = responseText.replace("\"","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GlobalVariables.getInstance().setRecommendation(responseText);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        });
        return jsonObjectRequest;
    }

}
