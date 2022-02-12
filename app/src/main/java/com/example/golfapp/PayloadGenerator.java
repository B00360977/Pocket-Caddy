package com.example.golfapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayloadGenerator {

    private String url = "http://86cd8507-d97a-4c5b-a1c0-baf1f2f6d789.westeurope.azurecontainer.io/score";

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
                System.out.println(responseText);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handles error in response
            }
        });
        return jsonObjectRequest;
    }

}
