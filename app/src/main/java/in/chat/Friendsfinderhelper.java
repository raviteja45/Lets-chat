package in.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ravi on 29-01-2018.
 */

public class Friendsfinderhelper extends BaseAdapter {
    private final Activity context;
    private final List<RegistrationBean> stringValue;
    private final String admin;
    ObjectMapper objMapper = new ObjectMapper();
    public Friendsfinderhelper(Activity context, List<RegistrationBean> stringValue,String admin) {
        this.context = context;
        this.stringValue = stringValue;
        this.admin = admin;

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
        if(stringValue!=null&&admin!=null&&stringValue.get(position).getPhoneNumber()!=null&&!stringValue.get(position).getPhoneNumber().equalsIgnoreCase(admin)){
            TextView txt1 = (TextView) viewItem.findViewById(R.id.username);
            //TextView txt2 = (TextView) viewItem.findViewById(R.id.txt2);
            final Button bt1 = (Button)viewItem.findViewById(R.id.friends);
            txt1.setText(stringValue.get(position).getUserName());
            //txt2.setText(stringValue.get(position).getUserName());
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(view.getContext());
                    dbHelper.userChatTracker(stringValue.get(position).getPhoneNumber(),stringValue.get(position).getUserName());
                    updateFriedRelation(view,stringValue.get(position).getPhoneNumber());
                    bt1.setEnabled(false);
                    /*Intent intent = new Intent(view.getContext(), SendMessage.class);
                    intent.putExtra("userType", stringValue.get(position).getPhoneNumber());
                    view.getContext().startActivity(intent);*/
                }
            });
        }

        return viewItem;
    }

    private void updateFriedRelation(View view, final String relation) {
        RegistrationBean res = null;
        final String data = ChatUtil.getFileDetails();
        StringRequest request = new StringRequest(Request.Method.POST, Connectionfactory.UPDATE_RELATION_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            public byte[] getBody() {
                RegistrationBean res = new RegistrationBean();
                res.setUserName(data.split("-")[0]);
                res.setPhoneNumber(data.split("-")[1]);
                res.setRelations(relation);
                String JSon = null;
                try {
                    JSon = objMapper.writeValueAsString(res);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return JSon.getBytes();
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(view.getContext());
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(request);


    }
}
