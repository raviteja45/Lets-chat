package in.chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static in.chat.ChatUtil.connection;

/**
 * Created by Ravi on 21-01-2018.
 */

public class Installation extends AppCompatActivity implements ConnectionListener {

    EditText name, phonenumber,email;
    Button done;
    ObjectMapper objMapper = new ObjectMapper();
     ProgressDialog create = null;
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, 101);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean getInstallationDetails() {
        boolean result = true;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if (!checkPermission()) {
                requestForSpecificPermission();
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/letschat");
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
                file.mkdir();
                try {
                    FileWriter fileWriter = new FileWriter(new File(file, "letschat"));
                    fileWriter.append(name.getText());
                    fileWriter.append("-" + phonenumber.getText() + "-" + telephonyManager.getDeviceId());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                    create.dismiss();
                }
            }
        }
        else{
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/letschat");
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            file.mkdir();
            try {
                FileWriter fileWriter = new FileWriter(new File(file, "letschat"));
                fileWriter.append(name.getText());
                fileWriter.append("-" + phonenumber.getText() + "-" + telephonyManager.getDeviceId());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                create.dismiss();
            }
        }

        return result;
    }

    public void insertDetails(final View view) {

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.184.0.54:2015/letschat/letschat/rest/insertregistrationdetails",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is "+response);
                        if("inserted".equalsIgnoreCase(response)){
                            Thread t1 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    processConnection(view);
                                }
                            });
                            t1.start();
                            try {
                                t1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            create.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public byte[] getBody() {
                RegistrationBean res = new RegistrationBean();
                res.setUserName(name.getText().toString());
                res.setPhoneNumber(phonenumber.getText().toString());
                res.setEmailAddress(email.getText().toString());
                res.setRelations(phonenumber.getText().toString());
                String JSon = null;
                try {
                    JSon = objMapper.writeValueAsString(res);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return JSon.getBytes();
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(Installation.this);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(request);

    }


    private void processConnection(View view) {
                try {

                    XMPPTCPConnectionConfiguration.Builder builder1 = XMPPTCPConnectionConfiguration.builder();
                    builder1.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    builder1.setServiceName(ChatUtil.HOST_NAME);
                    builder1.setHost(ChatUtil.HOST_NAME);
                    builder1.setResource("Test");
                    builder1.setDebuggerEnabled(true);
                    connection = new XMPPTCPConnection(builder1.build());
                    connection.connect();
                    try {
                        AccountManager accountManager = AccountManager.getInstance(connection);
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(phonenumber.getText().toString(), "admin");
                        builder1.setUsernameAndPassword(phonenumber.getText().toString(), "admin");
                        connection.login();
                        if(connection.getUser().equalsIgnoreCase(phonenumber.getText().toString())){
                            System.out.println("Authorized");
                        }
                        else{
                            System.out.println("Un Authorized");
                        }

                    } catch (SmackException | XMPPException e) {
                        System.out.println("Exception is " + e);
                    }
                } catch (SmackException | XMPPException | IOException e) {
                    e.printStackTrace();
                }

        create.dismiss();
        Intent intent = new Intent(view.getContext(), Friendfinder.class);
        Installation.this.finish();
        view.getContext().startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       final ConnectivityManager con = (ConnectivityManager) Installation.this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //if (con.getActiveNetworkInfo() != null) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/letschat");
            if (!file.exists()) {
                setContentView(R.layout.installation);
                name = (EditText) findViewById(R.id.name);
                phonenumber = (EditText) findViewById(R.id.phonenumber);
                email = (EditText)findViewById(R.id.email);
                done = (Button) findViewById(R.id.finish);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        create = ProgressDialog.show(Installation.this, "Relax... We are Processing", "", false, false);
                        if(con.getActiveNetworkInfo() == null){
                            create.dismiss();
                            Toast.makeText(Installation.this, "Hmmm...Check your Internet Connection...", Toast.LENGTH_LONG).show();
                        }

                        else{
                            if (!name.getText().toString().isEmpty() && !phonenumber.getText().toString().isEmpty()&&!email.getText().toString().isEmpty()) {
                                boolean result = getInstallationDetails();
                                if(result){
                                    Thread t2 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            insertDetails(view);
                                        }
                                    });
                                    t2.start();
                                    try {
                                        t2.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    Toast.makeText(Installation.this, "Error while creating file", Toast.LENGTH_LONG).show();
                                }


                            } else {
                                Toast.makeText(Installation.this, "Please Enter all the fields", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

            } else {
                Intent intent = new Intent(this, Friendfinder.class);
                Installation.this.finish();
                this.startActivity(intent);
            }

        //}


    }
}
