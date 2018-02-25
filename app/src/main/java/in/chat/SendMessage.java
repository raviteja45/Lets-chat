package in.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static in.chat.ChatUtil.connection;

/**
 * Created by Ravi on 17-12-2017.
 */

public class SendMessage extends Activity implements ConnectionListener {

    static String userType;
    static ArrayList<MessageHolder> arrayList;
    static AdapterHelper adapter;
    EditText typeMessage;
    Button send, click;
    ListView lv;
    public static String idGlobal = "abc";
    FileTransferManager manager;
    private Uri uri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage);
        userType = getIntent().getExtras().get("userType").toString();
        typeMessage = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        click = (Button) findViewById(R.id.click);
        lv = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new AdapterHelper(SendMessage.this, arrayList);
        lv.setAdapter(adapter);
        retrieveHistory();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(typeMessage, userType, null);
            }
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage1(typeMessage, userType);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        DetectNetWorkChange.getInstance().setConnectivityListener(this);
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

        if (res != null && !res.isEmpty()) {
            MessageHolder messageHolderAdapter = new MessageHolder();
            messageHolderAdapter.setOwner(fromPerson[0]);
            messageHolderAdapter.setWithWhom(fromPerson[0]);
            messageHolderAdapter.setImageUrl(null);
            messageHolderAdapter.setMessage(res);
            messageHolderAdapter.setDateTime(null);
            if (fromPerson[0].equalsIgnoreCase(xyz)) {
                arrayList.add(messageHolderAdapter);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }


        if (res != null && !res.isEmpty() && !idGlobal.equalsIgnoreCase(id)) {
            idGlobal = id;
            final DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            MessageHolder messageHolderDB = new MessageHolder();
            messageHolderDB.setOwner(fromPerson[0]);
            messageHolderDB.setWithWhom(fromPerson[0]);
            messageHolderDB.setMessage(res);
            boolean result = databaseOpenHelper.insertRecords(messageHolderDB, id);
            if (result) {
                System.out.println("Successfully Inserted into DB from SendMessage");
            } else {
                System.out.println("Error while inserting into DB from SendMessage");
            }
        }

    }


    private void sendMessage(EditText editText, final String userType, Bitmap bitmap) {
        String text = "";
        if (editText != null) {
            text = editText.getText().toString();
        }
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
        if (bitmap != null) {
            bean1.setBitMap(bitmap);
            bean1.setImageAttached(true);
        }


        arrayList.add(bean1);

        adapter.notifyDataSetChanged();

        final DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        MessageHolder bean = new MessageHolder();
        bean.setOwner("phone");
        bean.setWithWhom(userType);
        bean.setMessage(text);
        boolean result = dbHelper.insertRecords(bean, "adminInsert");
        if (result) {
            Toast.makeText(this, "record inserted", Toast.LENGTH_SHORT).show();
        }
        if (editText != null) {
            editText.setText("");
        }
        Chat chat = ChatManager.getInstanceFor(connection).createChat(userType + "@"+Connectionfactory.HOST_NAME, new ChatMessageListener() {
            public void processMessage(Chat chat, Message message) {

            }
        });
        try {
            chat.sendMessage(text);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String imageName = userType + "_" + UUID.randomUUID().toString();
        if (requestCode == 2) {
            if (data != null) {
                final Intent value = data;
                uri = data.getData();
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Connectionfactory.UPLOAD_IMAGE,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    sendMessage(null, userType, bitmap);
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            System.out.println("Error is " + error.getMessage());
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("filename", imageName);
                            params.put("image", imageString);
                            return params;
                        }


                    };

                    RequestQueue rQueue = Volley.newRequestQueue(this);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    rQueue.add(stringRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void sendMessage1(EditText editText, final String userType) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);

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
                        builder.setUsernameAndPassword(ChatUtil.getFileDetails().split("-")[1], "admin");
                        builder.setSendPresence(true);
                        builder.setServiceName(Connectionfactory.HOST_NAME);
                        builder.setHost(Connectionfactory.HOST_NAME);
                        builder.setResource(ChatUtil.RESOURCE);
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
                        PacketListener myListener = new PacketListener() {
                            public void processPacket(Stanza stanza) {
                                getMessage(stanza, userType);
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
