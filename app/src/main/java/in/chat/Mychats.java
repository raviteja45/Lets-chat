package in.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Ravi on 05-02-2018.
 */

public class Mychats extends Activity implements ConnectionListener {

    ListView listView;
    ArrayAdapter<String> adapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mychats);
        listView = (ListView)findViewById(R.id.listView);
        DatabaseOpenHelper openHelper = new DatabaseOpenHelper(this);
        final List<String> friedsList = openHelper.retrieveFriendsFromDb();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,friedsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Clicked on "+friedsList.get(i));
                Intent intent = new Intent(getApplicationContext(), SendMessage.class);
                intent.putExtra("userType", friedsList.get(i));
                startActivity(intent);
            }
        });

    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
