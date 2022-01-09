package clientSide.GUI.chat.panels;

import clientSide.GUI.utilities.Message;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 11:06 PM
 * Description: ...
 */
public class MessagePanel extends JPanel {

    protected JTextArea contentField;

    public MessagePanel(Message msg)
    {
        super(new BorderLayout());
        JLabel label = new JLabel(msg.toString());
        contentField = new JTextArea(msg.content());
        contentField.setLineWrap(true);
        add(label, BorderLayout.PAGE_START);
        add(contentField, BorderLayout.CENTER);
    }
}
