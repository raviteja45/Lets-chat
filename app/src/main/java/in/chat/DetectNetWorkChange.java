package in.chat;

/**
 * Created by Ravi on 17-12-2017.
 */

import android.app.Application;

public class DetectNetWorkChange extends Application {

    private static DetectNetWorkChange detectChange;

    @Override
    public void onCreate() {
        super.onCreate();

        detectChange = this;
    }

    public static synchronized DetectNetWorkChange getInstance() {
        return detectChange;
    }

    public void setConnectivityListener(ConnectionListener listener1) {

        ConnectionManager.connectionListener = listener1;

    }
}
