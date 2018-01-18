package in.chat;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by Ravi on 17-12-2017.
 */

public class ChatUtil {

    public static AbstractXMPPConnection connection;

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
}
