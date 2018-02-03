package in.chat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static in.chat.ChatUtil.connection;

/**
 * Created by Ravi on 28-01-2018.
 */

public class Friendfinder extends AppCompatActivity implements ConnectionListener {

    ObjectMapper objMapper = new ObjectMapper();
    ListView lv1;
    String userInfo;
    public static String idGlobal = "abc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager con = (ConnectivityManager) Friendfinder.this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con.getActiveNetworkInfo() != null) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            File file = new File(Environment.getExternalStorageDirectory().getPath() + ChatUtil.FOLDER_PATH);
            if (file.exists()) {
                userInfo = ChatUtil.getFileDetails();

                if (telephonyManager.getDeviceId().equals(userInfo.split("-")[2])) {
                    setContentView(R.layout.friendsfinder);
                    lv1 = (ListView) findViewById(R.id.list123);
                    processConnection();
                    suggestFriends();
                }
            }
        }
    }

    private void suggestFriends() {

        RegistrationBean res = null;
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.19:2015/letschat/letschat/rest/retrievemaincontent",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response is "+response);
                            try {
                                RegistrationBean[] res = objMapper.readValue(response,RegistrationBean[].class);
                                System.out.println(res[0].getPhoneNumber());
                                displayContent(res);

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
                    res.setUserName(userInfo.split("-")[0]);
                    res.setPhoneNumber(userInfo.split("-")[1]);
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



    private void displayContent(RegistrationBean[] res) {


        final List<RegistrationBean> listArray = new LinkedList<>(Arrays.asList(res));
        final Friendsfinderhelper fHelper = new Friendsfinderhelper(Friendfinder.this,listArray,userInfo.split("-")[1]);
        lv1.setAdapter(fHelper);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



            }
        });
    }
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    protected void getMessage(Stanza stanza) {

        DocumentBuilder builder = null;
        Document document = null;
        InputSource inputSource = new InputSource();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
        }
        inputSource.setCharacterStream(new StringReader(stanza.toString()));
        try {
            document = builder.parse(inputSource);
        } catch (SAXException | IOException e) {
        }
        NodeList nodeList = document.getElementsByTagName("message");
        Element element = (Element) nodeList.item(0);
        String[] fromPerson = element.getAttribute("from").split("@");
        String id = element.getAttribute("id");
        NodeList body = element.getElementsByTagName("body");
        Element element1 = (Element) body.item(0);
        String res = ChatUtil.getData(element1);
        if (res != null && !res.isEmpty() && !idGlobal.equalsIgnoreCase(id)) {
            idGlobal = id;
            final DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
            MessageHolder bean = new MessageHolder();
            bean.setOwner(fromPerson[0]);
            bean.setWithWhom(fromPerson[0]);
            bean.setMessage(res);
            boolean result = dbHelper.insertRecords(bean,id);
            if(SendMessage.userType.equalsIgnoreCase(fromPerson[0])){
                SendMessage.arrayList.add(bean);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SendMessage.adapter.notifyDataSetChanged();
                    }
                });
            }

            if (result) {
                System.out.println("Successfully Inserted into DB from MainActivity");
            } else {
                System.out.println("Error while inserting into DB from MainActivity");
            }
        }

    }
    protected void receiveMessage() {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("Available");
        try {
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        StanzaFilter filter = new AndFilter(new StanzaTypeFilter(Message.class));
        PacketListener myListener = new PacketListener() {
            public void processPacket(Stanza stanza) {
                getMessage(stanza);

            }
        };
        connection.addPacketListener(myListener, filter);
    }

    private void processConnection() {

        new Thread() {

            public void run() {
                try {
                    XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                    builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    System.out.println("USer NAME is "+userInfo.split("-")[1]);
                    builder.setUsernameAndPassword(userInfo.split("-")[1], "admin");
                    builder.setServiceName(ChatUtil.HOST_NAME);
                    builder.setHost(ChatUtil.HOST_NAME);
                    builder.setResource("Test");
                    builder.setDebuggerEnabled(true);
                    connection = new XMPPTCPConnection(builder.build());
                    connection.connect();
                    connection.login();
                    receiveMessage();
                } catch (SmackException | XMPPException | IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();


    }

}
