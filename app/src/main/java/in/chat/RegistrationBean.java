package in.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Ravi on 22-01-2018.
 */

public class RegistrationBean {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String userName;
    public String phoneNumber;
    public String emailAddress;
    public String relations;
    @JsonIgnore
    public String _id;


    public String get_id() {
        return _id;
    }


    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmailAddress() {
        return emailAddress;

    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }
}
