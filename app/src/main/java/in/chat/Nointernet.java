package in.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ravi on 05-02-2018.
 */

public class Nointernet extends AppCompatActivity implements ConnectionListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nointernet);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if(isConnected){
            Intent intent = new Intent(this, Friendfinder.class);
            Nointernet.this.finish();
            this.startActivity(intent);
        }


    }
}
