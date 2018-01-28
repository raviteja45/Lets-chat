package in.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Ravi on 28-01-2018.
 */

public class Friendfinder extends AppCompatActivity implements ConnectionListener {

    ObjectMapper objMapper = new ObjectMapper();
    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendsfinder);
        tv1 = (TextView)findViewById(R.id.initial);
        suggestFriends();
    }

    private void suggestFriends() {

        RegistrationBean res = null;
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.184.0.54:2015/letschat/letschat/rest/retrievemaincontent",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response is "+response);
                            try {
                                RegistrationBean[] res = objMapper.readValue(response,RegistrationBean[].class);
                                System.out.println(res[0].getPhoneNumber());
                                displayContent(res[0].getPhoneNumber());

                            } catch (IOException e) {
                                e.printStackTrace();
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
                    /*res.setUserName(name.getText().toString());
                    res.setPhoneNumber(phonenumber.getText().toString());
                    res.setEmailAddress(email.getText().toString());
                    res.setRelations(phonenumber.getText().toString());*/
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

            RequestQueue rQueue = Volley.newRequestQueue(Friendfinder.this);
            request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue.add(request);


    }



    private void displayContent(String phoneNumber) {


        tv1.setText(phoneNumber);
    }
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
