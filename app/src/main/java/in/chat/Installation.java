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

public class Installation extends AppCompatActivity {

    EditText name, phonenumber, email;
    Button done;
    ObjectMapper objMapper = new ObjectMapper();
    ProgressDialog create = null;

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 101);
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
        File file = new File(Environment.getExternalStorageDirectory().getPath() + ChatUtil.FOLDER_PATH);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        file.mkdir();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkPermission()) {
                requestForSpecificPermission();
                result = fileCreation(file, telephonyManager, result);
            }
        } else {
            result = fileCreation(file, telephonyManager, result);
        }

        return result;
    }

    public boolean fileCreation(File file, TelephonyManager telephonyManager, boolean result) {
        try {
            FileWriter fileWriter = new FileWriter(new File(file, ChatUtil.FOLDER_NAME));
            fileWriter.append(name.getText());
            fileWriter.append("-" + phonenumber.getText() + "-" + telephonyManager.getDeviceId());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
            create.dismiss();
        }
        return result;
    }

    public void insertDetails(final View view) {

        StringRequest request = new StringRequest(Request.Method.POST, Connectionfactory.INSERT_INSTALLATION_RECORDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (ChatUtil.INSERTED_RETURN_VALUE.equalsIgnoreCase(response)) {
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
            builder1.setServiceName(Connectionfactory.HOST_NAME);
            builder1.setHost(Connectionfactory.HOST_NAME);
            builder1.setResource(ChatUtil.RESOURCE);
            builder1.setDebuggerEnabled(true);
            connection = new XMPPTCPConnection(builder1.build());
            connection.connect();
            try {
                AccountManager accountManager = AccountManager.getInstance(connection);
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.createAccount(phonenumber.getText().toString(), ChatUtil.USER_PASSWORD);
                builder1.setUsernameAndPassword(phonenumber.getText().toString(), ChatUtil.USER_PASSWORD);
                connection.login();
            } catch (SmackException | XMPPException e) {
            }
        } catch (SmackException | XMPPException | IOException e) {
            e.printStackTrace();
        }

        if (view != null) {
            create.dismiss();
            Intent intent = new Intent(view.getContext(), Friendfinder.class);
            Installation.this.finish();
            view.getContext().startActivity(intent);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ConnectivityManager con = (ConnectivityManager) Installation.this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + ChatUtil.FOLDER_PATH);
        if (!file.exists()) {
            setContentView(R.layout.installation);
            name = (EditText) findViewById(R.id.name);
            phonenumber = (EditText) findViewById(R.id.phonenumber);
            email = (EditText) findViewById(R.id.email);
            done = (Button) findViewById(R.id.finish);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    create = ProgressDialog.show(Installation.this, ChatUtil.PROCESSING_NOTIFICATION, "", false, false);
                    if (con.getActiveNetworkInfo() == null) {
                        create.dismiss();
                        Toast.makeText(Installation.this, ChatUtil.CHECK_CONNECTION_NOTIFICATION, Toast.LENGTH_LONG).show();
                    } else {
                        if (!name.getText().toString().isEmpty() && !phonenumber.getText().toString().isEmpty() && !email.getText().toString().isEmpty()) {
                            boolean result = getInstallationDetails();
                            if (result) {
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
                            } else {
                                Toast.makeText(Installation.this, ChatUtil.ERROR_FILE_CREATION_NOTIFICATION, Toast.LENGTH_LONG).show();
                            }


                        } else {
                            Toast.makeText(Installation.this, ChatUtil.FILL_ALL_FIELDS_NOTIFICATION, Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

        } else {

            if (con.getActiveNetworkInfo() == null) {
                Intent intent = new Intent(this, Nointernet.class);
                Installation.this.finish();
                this.startActivity(intent);
            } else {
                Intent intent = new Intent(this, Friendfinder.class);
                Installation.this.finish();
                this.startActivity(intent);
            }

        }
    }
}
