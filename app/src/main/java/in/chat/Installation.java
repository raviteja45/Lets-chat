package in.chat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
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

    EditText name, phonenumber;
    Button done;
    ObjectMapper objMapper = new ObjectMapper();

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void getInstallationDetails() {
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
        }
    }

    public void insertDetails() {

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.19:2015/Services/test/rest/insertregistrationdetails",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Intent intent = new Intent(MainActivity.this, RetrieveMainDetails.class);
                        MainActivity.this.finish();
                        MainActivity.this.startActivity(intent);*/
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


    private void processConnection() {
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
                done = (Button) findViewById(R.id.finish);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(con.getActiveNetworkInfo() == null){
                            Toast.makeText(Installation.this, "Hmmm...Check your Internet Connection...", Toast.LENGTH_LONG).show();
                        }
                        else{
                            if (!name.getText().toString().isEmpty() && !phonenumber.getText().toString().isEmpty()) {
                                getInstallationDetails();
                                Thread t1 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processConnection();
                                    }
                                });
                                t1.start();
                                try {
                                    t1.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                insertDetails();
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                Installation.this.finish();
                                view.getContext().startActivity(intent);
                            } else {
                                Toast.makeText(Installation.this, "Please Enter all the fields", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

            } else {
                Intent intent = new Intent(this, MainActivity.class);
                Installation.this.finish();
                this.startActivity(intent);
            }

        //}


    }
}
