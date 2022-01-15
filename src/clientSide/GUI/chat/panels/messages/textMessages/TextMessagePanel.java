package clientSide.GUI.chat.panels.messages.textMessages;

import clientSide.GUI.chat.panels.messages.MessagePanel;
import clientSide.messages.Message;

import javax.swing.*;
import java.awt.*;

import static utilities.StringHandler.getLines;

/**
 * clientSide.GUI.chat.panels.messages
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:09 PM
 * Description: The panel for showing a text message
 */
public class TextMessagePanel extends MessagePanel {

    /**
     * Content field of the message
     */
    protected final JTextArea contentField;

    /**
     * Determine how many words should appear in each line
     */
    private static final int WORDS_EACH_LINE = 120;

    /**
     * Create a new panel
     * @param msg the displayed message
     */
    public TextMessagePanel(Message msg)
    {
        super(msg.toString());
        //Break the message to multi-lines string (to avoid the message spreads out of the layout)
        contentField = new JTextArea(getLines(msg.content(), WORDS_EACH_LINE));
        contentField.setEditable(false);
        add(contentField, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), msg.toString()));
    }
}
