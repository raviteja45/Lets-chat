package in.chat;

import android.os.Environment;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Ravi on 17-12-2017.
 */

public class ChatUtil {

    public static AbstractXMPPConnection connection;

    public static final String FOLDER_PATH = "/letschat";

    public static final String FOLDER_NAME = "letschat";

    public static final String HOST_NAME = "192.168.0.19";

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
