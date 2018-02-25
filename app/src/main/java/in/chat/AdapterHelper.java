package in.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ravi on 17-12-2017.
 */

public class AdapterHelper extends BaseAdapter {

    private static LayoutInflater layoutInflater = null;
    ArrayList<MessageHolder> messageHolderList;

    public AdapterHelper(Activity activity, ArrayList<MessageHolder> list) {
        messageHolderList = list;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messageHolderList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MessageHolder message1 = messageHolderList.get(i);
        String message = message1.getMessage();
        if (view == null) {
            view = layoutInflater.inflate(R.layout.adapterhelper, null);
        }
        TextView messageText = (TextView) view.findViewById(R.id.messageText);
        ImageView imgView = (ImageView)view.findViewById(R.id.image123);
        messageText.setText(message);
        LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.lineLayout);
        LinearLayout main = (LinearLayout) view
                .findViewById(R.id.lineLayoutParent);
        if(message1.getBitMap()!=null){
            imgView.getLayoutParams().height = 200;
            imgView.getLayoutParams().width = 200;
            System.out.println("Value is "+message1.getBitMap().getWidth());
            imgView.setImageBitmap(message1.getBitMap());
            imgView.setVisibility(View.VISIBLE);
        }
        else{
            imgView.setVisibility(View.GONE);
        }
        if (message1.getOwner() != null && message1.getOwner().equalsIgnoreCase("phone")) {
           /* if(message1.getBitMap()!=null){
                imgView.getLayoutParams().height = 200;
                imgView.getLayoutParams().width = 200;
                System.out.println("Value is "+message1.getBitMap().getWidth());
                imgView.setImageBitmap(message1.getBitMap());
                imgView.setVisibility(View.VISIBLE);
            }
            else{
                imgView.setVisibility(View.GONE);
            }*/
            layout.setBackgroundResource(R.drawable.right);
            main.setGravity(Gravity.RIGHT);
        } else {
            layout.setBackgroundResource(R.drawable.left);
            main.setGravity(Gravity.LEFT);
        }
        messageText.setTextColor(Color.BLACK);
        return view;
    }
}
