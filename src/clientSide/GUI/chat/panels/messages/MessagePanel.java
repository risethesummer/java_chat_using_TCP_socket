package clientSide.GUI.chat.panels.messages;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 11:06 PM
 * Description: The panel for showing a message
 */
public class MessagePanel extends JPanel {

    /**
     * Create a new panel
     * @param sender the sender of the message
     */
    public MessagePanel(String sender)
    {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), sender));
    }
}
