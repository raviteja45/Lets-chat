package in.chat;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Ravi on 05-02-2018.
 */

public class Mychats extends Activity implements ConnectionListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
