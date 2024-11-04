package com.techrahman.smsbot20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class SMSReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d("checkMessage","checkMessage");

        if (bundle != null) {
            final Object[] object = (Object[]) bundle.get("pdus");
            String number = "";
            StringBuilder messageBuilder = new StringBuilder();

            for (int i = 0; i < object.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object[i]);
                if (number.isEmpty()) {
                    number = smsMessage.getDisplayOriginatingAddress();
                }
                messageBuilder.append(smsMessage.getDisplayMessageBody());
            }
            String message = messageBuilder.toString();
            MainActivity.number = number;
            MainActivity.body = message;
            Log.d("checkMessage", number + message);

            sendServer(context,number,message);
        }
    }

    private void sendServer(Context context,String mobile,String message) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://demo.techrahman.xyz/getmessage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("message", message);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }
}
