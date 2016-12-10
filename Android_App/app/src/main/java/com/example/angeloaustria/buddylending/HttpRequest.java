package com.example.angeloaustria.buddylending;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRequest {
    private Context context;
    public HttpRequest(Context context){
        this.context = context;
    }

    public void getAccountBalance(String username, final ResultCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        RequestQueue queue = Volley.newRequestQueue(context);
        final JSONObject jsonBody;
        jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
        }
        catch(JSONException e){
            Log.d("DEBUG", e.getMessage());
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest("http://138.197.132.23/post",
                jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.setSuccess(true);
                try {
                    // set balance to the callback.
                    callback.setData(response.get("balance"));
                }
                catch(JSONException e){
                    callback.setErr(e.getMessage());
                }
                handler.post(callback);
                Log.d("DEBUG", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.setSuccess(false);
                callback.setErr(error.getMessage());
                handler.post(callback);
                Log.d("DEBUG", "That didn't work!");
            }
        });
        queue.add(jsonRequest);
    }

    public void getUpvotes(String username, final ResultCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        RequestQueue queue = Volley.newRequestQueue(context);
        final JSONObject jsonBody;
        jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
        }
        catch(JSONException e){
            Log.d("DEBUG", e.getMessage());
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest("http://138.197.132.23/post",
                jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.setSuccess(true);
                try {
                    // set balance to the callback.
                    callback.setData(response.get("upVote"));
                }
                catch(JSONException e){
                    callback.setErr(e.getMessage());
                }
                handler.post(callback);
                Log.d("DEBUG", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.setSuccess(false);
                callback.setErr(error.getMessage());
                handler.post(callback);
                Log.d("DEBUG", "That didn't work!");
            }
        });
        queue.add(jsonRequest);
    }

    public void getDownvotes(String username, final ResultCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        RequestQueue queue = Volley.newRequestQueue(context);
        final JSONObject jsonBody;
        jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
        }
        catch(JSONException e){
            Log.d("DEBUG", e.getMessage());
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest("http://138.197.132.23/post",
                jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.setSuccess(true);
                try {
                    // set balance to the callback.
                    callback.setData(response.get("downVote"));
                }
                catch(JSONException e){
                    callback.setErr(e.getMessage());
                }
                handler.post(callback);
                Log.d("DEBUG", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.setSuccess(false);
                callback.setErr(error.getMessage());
                handler.post(callback);
                Log.d("DEBUG", "That didn't work!");
            }
        });
        queue.add(jsonRequest);
    }
}
