package in.chat;

import android.os.Environment;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Ravi on 17-12-2017.
 */

public class ChatUtil {

    public static AbstractXMPPConnection connection;

    public static final String FOLDER_PATH = "/letschat";

    public static final String FOLDER_NAME = "letschat";

    public static final String RESOURCE = "Test";

    public static final String PROCESSING_NOTIFICATION = "Relax... We are Processing";

    public static final String CHECK_CONNECTION_NOTIFICATION = "Hmmm...Check your Internet Connection...";

    public static final String ERROR_FILE_CREATION_NOTIFICATION = "Error while creating file";

    public static final String FILL_ALL_FIELDS_NOTIFICATION = "Please Enter all the fields";

    public static final String INSERTED_RETURN_VALUE = "inserted";

    public static final String USER_PASSWORD = "admin";

    public static String getData(Element e) {
        if (e != null) {

            Node node = e.getFirstChild();

            if (node instanceof CharacterData) {
                CharacterData characterData = (CharacterData) node;
                return characterData.getData();
            }
        }


        return "";
    }

    public static String getFileDetails() {
        String userData = null;
        File file = new File(Environment.getExternalStorageDirectory().getPath() + FOLDER_PATH, FOLDER_NAME);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String l;
            try {
                while ((l = br.readLine()) != null) {
                    userData = l;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return userData;
    }


}
