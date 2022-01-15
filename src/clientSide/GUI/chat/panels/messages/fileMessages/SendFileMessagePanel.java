package clientSide.GUI.chat.panels.messages.fileMessages;

import clientSide.messages.FileMessage;
import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat.panels.messages.fileMessages
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:39 PM
 * Description: The panel for showing a file message sent by the client
 */
public class SendFileMessagePanel extends FileMessagePanel {

    /**
     * Show current progress of sending files
     */
    private final JProgressBar progressBar = new JProgressBar();

    /**
     * Get current progress bar
     * @return the progress bar
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Create a new panel
     * @param msg the file message
     */
    public SendFileMessagePanel(FileMessage msg) {
        super(msg);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.yellow);
        add(progressBar, BorderLayout.PAGE_END);
    }
}
