package in.chat;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

import static in.chat.ChatUtil.connection;

public class MainActivity extends AppCompatActivity implements ConnectionListener {

    Button user1, user2, user3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("My notification")
                        .setContentText("Reached my page");


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, mBuilder.build());
            user1 = (Button) findViewById(R.id.user1);
            user2 = (Button) findViewById(R.id.user2);
            user3 = (Button) findViewById(R.id.user3);
            processConnection();

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
                    builder.setUsernameAndPassword("phone", "admin");
                    builder.setServiceName("192.168.0.19");
                    builder.setHost("192.168.0.19");
                    builder.setResource("Test");
                    builder.setDebuggerEnabled(true);
                    connection = new XMPPTCPConnection(builder.build());
                    connection.connect();
                    connection.login();
                    System.out.println("From main activity "+connection.getUser());
                } catch (SmackException | XMPPException | IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();

        user1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SendMessage.class);
                intent.putExtra("userType", "user1");
                startActivity(intent);
                //finish();
            }
        });

        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessage.class);
                intent.putExtra("userType", "user2");
                startActivity(intent);
                //finish();

            }
        });

        user3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    AccountManager accountManager = AccountManager.getInstance(connection);
                    accountManager.sensitiveOperationOverInsecureConnection(true);
                    accountManager.createAccount("user3", "admin");
                } catch (SmackException  | XMPPException e) {
                    System.out.println("Exception is "+e);
                }


            }
        });

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

        if(isConnected){
            Toast.makeText(this, "Internet", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }
    }
}
