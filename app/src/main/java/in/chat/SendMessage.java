package in.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static in.chat.ChatUtil.connection;

/**
 * Created by Ravi on 17-12-2017.
 */

public class SendMessage extends Activity implements ConnectionListener {

    static String userType;
    ArrayList<MessageHolder> arrayList;
    AdapterHelper adapter;
    EditText typeMessage;
    Button send;
    ListView lv;
    public static String idGlobal = "abc";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage);
        userType = getIntent().getExtras().get("userType").toString();
        System.out.println("On create user is "+userType);
        typeMessage = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        lv = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<MessageHolder>();
        adapter = new AdapterHelper(SendMessage.this, arrayList);
        lv.setAdapter(adapter);
        retrieveHistory();
        receiveMessage();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(typeMessage,userType);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        DetectNetWorkChange.getInstance().setConnectivityListener(this);
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
                getMessage(stanza, userType);
                System.out.println("On receive user is "+userType);

            }
        };
        connection.addPacketListener(myListener, filter);
    }

    protected void getMessage(Stanza stanza, String xyz) {

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

            if(res != null && !res.isEmpty()){
                MessageHolder bean1 = new MessageHolder();
                bean1.setOwner(fromPerson[0]);
                bean1.setWithWhom(fromPerson[0]);
                bean1.setImageUrl(null);
                bean1.setMessage(res);
                bean1.setDateTime(null);
                if (fromPerson[0].equalsIgnoreCase(xyz)) {
                    arrayList.add(bean1);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }


            if(res != null && !res.isEmpty()&&!idGlobal.equalsIgnoreCase(id)){
                idGlobal = id;
                final DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
                MessageHolder bean = new MessageHolder();
                bean.setOwner(fromPerson[0]);
                bean.setWithWhom(fromPerson[0]);
                bean.setMessage(res);
                boolean result = dbHelper.insertRecords(bean);
            }



      //  }

    }



    private void sendMessage(EditText editText,final String userType) {

        String text = editText.getText().toString();
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("Available");
        try {
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        MessageHolder bean1 = new MessageHolder();
        bean1.setOwner("phone");
        bean1.setMessage(text);
        bean1.setWithWhom(userType);

        arrayList.add(bean1);

        adapter.notifyDataSetChanged();

        final DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        MessageHolder bean = new MessageHolder();
        bean.setOwner("phone");
        bean.setWithWhom(userType);
        bean.setMessage(text);
        boolean result = dbHelper.insertRecords(bean);
        if (result) {
            Toast.makeText(this, "record inserted", Toast.LENGTH_SHORT).show();
        }
        editText.setText("");
        Chat chat = ChatManager.getInstanceFor(connection).createChat(userType + "@192.168.0.19", new ChatMessageListener() {
            public void processMessage(Chat chat, Message message) {

            }
        });
        try {
            chat.sendMessage(text);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void retrieveHistory() {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(this);
        ArrayList<MessageHolder> result = helper.getHistory(userType);
        for (MessageHolder s : result) {
            MessageHolder bean = new MessageHolder();
            bean.setWithWhom(s.getWithWhom());
            bean.setMessage(s.getMessage());
            bean.setOwner(s.getOwner());
            arrayList.add(bean);
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            new Thread() {

                public void run() {
                    try {
                        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                        builder.setUsernameAndPassword("phone", "admin");
                        builder.setSendPresence(true);
                        builder.setServiceName("192.168.0.19");
                        builder.setHost("192.168.0.19");
                        builder.setResource("Test");
                        builder.setDebuggerEnabled(true);
                        Presence presence = new Presence(Presence.Type.available);
                        presence.setStatus("Available");
                        connection = new XMPPTCPConnection(builder.build());
                        connection.connect();
                        connection.login();
                        Presence presence123 = new Presence(Presence.Type.available);
                        presence123.setStatus("Available");
                        try {
                            connection.sendStanza(presence123);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        StanzaFilter filter = new AndFilter(new StanzaTypeFilter(Message.class));
                        PacketListener myListener = new PacketListener()
                        {
                            public void processPacket(Stanza stanza)
                            {
                                getMessage(stanza,userType);
                            }
                        };
                        connection.addPacketListener(myListener, filter);
                        try {
                            connection.sendStanza(presence);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                    } catch (SmackException | XMPPException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }.start();




        }

    }

}
