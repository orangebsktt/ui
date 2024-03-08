package com.hackathon.fastshop.apicaller;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ApiCaller {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://jsonplaceholder.typicode.com/todos/1";

    public ApiCaller(){
        this.url = "https://jsonplaceholder.typicode.com/todos/1";
    }

    public ApiCaller(String url){
        this.url = url;
    }

    public void getData(android.content.Context ctx, int methodType) {

        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(ctx);
        methodType = Request.Method.GET;

        // String Request initialized
        mStringRequest = new StringRequest(methodType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Toast.makeText(ctx.getApplicationContext(), "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i(TAG, "Error :" + error.toString());
                System.out.println("error " + error);
            }
        });

        mRequestQueue.add(mStringRequest);
    }

}
