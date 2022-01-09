package clientSide.GUI.chat.panels;

import clientSide.GUI.utilities.FileMessage;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 1:00 PM
 * Description: ...
 */
public class SendFileMessagePanel extends FileMessagePanel {

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    private final JProgressBar progressBar = new JProgressBar();

    public SendFileMessagePanel(FileMessage msg) {
        super(msg);
        add(progressBar, BorderLayout.PAGE_END);
    }

}
