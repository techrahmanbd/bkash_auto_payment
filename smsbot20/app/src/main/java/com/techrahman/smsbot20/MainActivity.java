package com.techrahman.smsbot20;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static String number = "";
    public static String body = "";
    private TextView trxid;
    private AppCompatButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        trxid = findViewById(R.id.trxid);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trx = trxid.getText().toString();
                if (trx.isEmpty()) {
                    trxid.setError("TrxID Empty");
                } else {
                String url = "https://demo.techrahman.xyz/verify.php?trxid="+trx;
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (status.equals("paid")) {
                                        Toast.makeText(MainActivity.this, "অভিন্দন আপনি"+jsonObject.getString("amount")+"টাকা পরিশোঢ করেছেন", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, MainActivity2.class));
                                    } else if (status.equals("unpaid")) {
                                        Toast.makeText(MainActivity.this, "আপনি টাকা পরিষোধ করেনন", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(MainActivity.this, ""+jsonObject.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                // Add request to Volley queue
                Volley.newRequestQueue(MainActivity.this).add(request);


            }
            }
        });







        askSMSPermission();
    }





    //=============================================================
    //=============================================================
    private void askSMSPermission() {

        // This is only necessary for API level >= 33 (TIRAMISU)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            String[] permissionArrays = new String[]{ android.Manifest.permission.SEND_SMS, android.Manifest.permission.RECEIVE_SMS };

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) ==
                    PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.SEND_SMS)) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Notification Permission")
                        .setMessage("App ta sundor vabe chalanor jonno permission ta dorkar")
                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(MainActivity.this, permissionArrays, 1);

                            }
                        })
                        .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();


            } else {
                // Directly ask for the permission
                ActivityCompat.requestPermissions(MainActivity.this, permissionArrays, 1);
            }
        }
    }


    //=============================================================
    //======================================================================







}