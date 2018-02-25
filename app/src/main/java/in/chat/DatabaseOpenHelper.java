package in.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Ravi on 17-12-2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    static String result = "abc";

    DatabaseOpenHelper(Context context) {
        super(context, "test", null, 1);
    }

    public ArrayList<MessageHolder> getHistory(String userType) {
        String CREATE_CONTACTS_TABLE = "create table if not exists UserRecords(owner text, message text, dateTime text, imageUrl text,withWhom text)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_CONTACTS_TABLE);
        ArrayList<MessageHolder> languageList = new ArrayList<MessageHolder>();
        String selectQuery = "SELECT  * FROM UserRecords WHERE withWhom='" + userType + "'";
        System.out.println("User type is " + userType);
        SQLiteDatabase db1 = this.getWritableDatabase();
        System.out.println(db1.getPath());
        Cursor cursor = db1.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MessageHolder bean = new MessageHolder();
                bean.setOwner(cursor.getString(0));
                bean.setMessage(cursor.getString(1));
                bean.setDateTime(cursor.getString(2));
                bean.setImageUrl(cursor.getString(3));
                bean.setWithWhom(cursor.getString(4));
                languageList.add(bean);
            } while (cursor.moveToNext());
        }
        return languageList;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean userChatTracker(String phoneNumber, String name) {
        String CREATE_CONTACTS_TABLE = "create table if not exists Chattracker(phonenumber text,name text)";
        SQLiteDatabase dbInsert = this.getWritableDatabase();
        dbInsert.execSQL(CREATE_CONTACTS_TABLE);
        ContentValues values = new ContentValues();
        values.put("phonenumber", phoneNumber );
        values.put("name", name);
        long i = dbInsert.insert("Chattracker", null, values);
        dbInsert.close();
        if (i != -1) {
            return true;
        } else {
            return false;
        }
    }

    /*public boolean updateLocalDbWithFriendName(String phoneNumber) {
        String CREATE_CONTACTS_TABLE = "create table if not exists Friendslist(phonenumber text)";
        SQLiteDatabase dbInsert = this.getWritableDatabase();
        dbInsert.execSQL(CREATE_CONTACTS_TABLE);
        ContentValues values = new ContentValues();
        values.put("phonenumber", phoneNumber );
        long i = dbInsert.insert("Chattracker", null, values);
        dbInsert.close();
        if (i != -1) {
            return true;
        } else {
            return false;
        }
    }*/

    public List<String> retrieveFriendsFromDb() {

        String CREATE_CONTACTS_TABLE = "SELECT  * FROM Chattracker";
        List<String> firendsList = new ArrayList<String>();
        SQLiteDatabase dbl = this.getWritableDatabase();
        String tableName = "Chattracker";
        Cursor cursor_table_check = dbl.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name ='"+tableName+"'", null);
        if(cursor_table_check!=null&&cursor_table_check.getCount()>0){
            Cursor cursor = dbl.rawQuery(CREATE_CONTACTS_TABLE, null);
            if (cursor!=null&&cursor.moveToFirst()) {
                do {
                    firendsList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }

        return firendsList;
    }

    public boolean insertRecords(MessageHolder messageHolder, String img) {
        String CREATE_CONTACTS_TABLE = "create table if not exists UserRecords(owner text, message text, dateTime text, imageUrl text,withWhom text)";
        SQLiteDatabase dbInsert = this.getWritableDatabase();
        dbInsert.execSQL(CREATE_CONTACTS_TABLE);
        ContentValues values = new ContentValues();
        values.put("owner", messageHolder.getOwner());
        values.put("message", messageHolder.getMessage());
        values.put("dateTime", messageHolder.getDateTime());
        values.put("imageUrl", messageHolder.getImageUrl());
        values.put("withWhom", messageHolder.getWithWhom());
        long i = dbInsert.insert("UserRecords", null, values);
        if (!img.equalsIgnoreCase("adminInsert")) {
            result = img;
        }
        dbInsert.close();
        if (i != -1) {
            return true;
        } else {
            return false;
        }
    }

}
