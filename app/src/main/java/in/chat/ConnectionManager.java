package in.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Ravi on 17-12-2017.
 */

public class ConnectionManager extends BroadcastReceiver {

    public static ConnectionListener connectionListener;
    @Override
    public void onReceive(Context context, Intent arg1) {
        boolean isConnected = false;
        ConnectivityManager connectionManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        if(activeNetwork!=null&&activeNetwork.isConnectedOrConnecting()){
            isConnected = true;
        }
        if (connectionListener != null) {
            connectionListener.onNetworkConnectionChanged(isConnected);
        }
    }



}
