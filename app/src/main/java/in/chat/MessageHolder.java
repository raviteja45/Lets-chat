package in.chat;

import android.graphics.Bitmap;

/**
 * Created by Ravi on 17-12-2017.
 */

public class MessageHolder {

    String owner;

    public boolean isImageAttached() {
        return isImageAttached;
    }

    public void setImageAttached(boolean imageAttached) {
        isImageAttached = imageAttached;
    }

    String message;
    String dateTime;
    String imageUrl;
    String withWhom;
    Bitmap bitMap;
    boolean isImageAttached;



    public Bitmap getBitMap() {
        return bitMap;
    }

    public void setBitMap(Bitmap bitMap) {
        this.bitMap = bitMap;
    }
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWithWhom() {
        return withWhom;
    }

    public void setWithWhom(String withWhom) {
        this.withWhom = withWhom;
    }
}
