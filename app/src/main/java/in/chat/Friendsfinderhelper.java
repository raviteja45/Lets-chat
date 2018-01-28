package in.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Ravi on 29-01-2018.
 */

public class Friendsfinderhelper extends BaseAdapter {
    private final Activity context;
    private final List<RegistrationBean> stringValue;
    private final String admin;

    public Friendsfinderhelper(Activity context, List<RegistrationBean> stringValue,String admin) {
        //super(context, R.layout.friendsfinderhelper,stringValue);
        this.context = context;
        this.stringValue = stringValue;
        this.admin = admin;
       // this.intId = intId;

    }

    @Override
    public int getCount() {
        return stringValue.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View viewItem= inflater.inflate(R.layout.friendsfinderhelper, null, true);
        if(!stringValue.get(position).getPhoneNumber().equalsIgnoreCase(admin)){
            TextView txt1 = (TextView) viewItem.findViewById(R.id.txt1);
            TextView txt2 = (TextView) viewItem.findViewById(R.id.txt2);
            Button bt1 = (Button)viewItem.findViewById(R.id.bt1);
            txt1.setText(stringValue.get(position).getPhoneNumber());
            txt2.setText(stringValue.get(position).getUserName());
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Clicked on"+stringValue.get(position).getPhoneNumber());
                    Intent intent = new Intent(view.getContext(), SendMessage.class);
                    intent.putExtra("userType", stringValue.get(position).getPhoneNumber());
                    view.getContext().startActivity(intent);

                    //Toast.makeText(Friendsfinderhelper.this, "On click"+stringValue.get(position).getPhoneNumber(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return viewItem;
    }
}
