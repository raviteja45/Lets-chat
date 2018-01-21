package in.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static in.chat.ChatUtil.connection;

public class MainActivity extends AppCompatActivity implements ConnectionListener {

    Button user1, user2, user3, done;
    public static String idGlobal = "abc";
    EditText name, phonenumber;
    String userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager con = (ConnectivityManager) MainActivity.this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con.getActiveNetworkInfo() != null) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            File file = new File(Environment.getExternalStorageDirectory().getPath() + ChatUtil.FOLDER_PATH);
            if (file.exists()) {
                 userInfo = ChatUtil.getFileDetails();

                if (telephonyManager.getDeviceId().equals(userInfo.split("-")[2])) {
                    setContentView(R.layout.homescreen);
                    processConnection();
                   /* if (getIntent().getExtras().get("userName") != null) {
                        user = getIntent().getExtras().get("userName").toString();
                    }*/
                    user1 = (Button) findViewById(R.id.user1);
                    user2 = (Button) findViewById(R.id.user2);
                    user3 = (Button) findViewById(R.id.user3);

                    user1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getApplicationContext(), SendMessage.class);
                            intent.putExtra("userType", "teja");
                            startActivity(intent);
                        }
                    });

                    user2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SendMessage.class);
                            intent.putExtra("userType", "ravi");
                            startActivity(intent);

                        }
                    });

                    user3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                        }
                    });
                }
            }


        } else {
            Toast.makeText(MainActivity.this, "There is no internet Connection", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        DetectNetWorkChange.getInstance().setConnectivityListener(this);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("Available");
        try {
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        if (isConnected) {
            Toast.makeText(this, "Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
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
            boolean result = dbHelper.insertRecords(bean);
            if (result) {
                System.out.println("Successfully Inserted into DB from MainActivity");
            } else {
                System.out.println("Error while inserting into DB from MainActivity");
            }
        }

    }


}
