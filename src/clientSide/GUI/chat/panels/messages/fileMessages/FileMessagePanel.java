package clientSide.GUI.chat.panels.messages.fileMessages;
import clientSide.GUI.chat.panels.files.FileDownloadPanel;
import clientSide.GUI.chat.panels.messages.MessagePanel;
import clientSide.messages.FileMessage;
import sockets.protocols.packet.FileTransfer;
import java.util.List;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:06 PM
 * Description: The panel for showing a file message
 */
public class FileMessagePanel extends MessagePanel {

    /**
     * Determine how many files should be on a line
     */
    private static final int MAX_FILE_ON_LINE = 4;

    /**
     * Create a new panel
     * @param msg the file message
     */
    public FileMessagePanel(FileMessage msg) {
        super(msg.toString());
        List<FileTransfer> fileTransfer = msg.files();
        //JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //Each line should have MAX_FILE_ON_LINE files
        JPanel filePanel = new JPanel (new GridLayout((fileTransfer.size() / MAX_FILE_ON_LINE) + 1, MAX_FILE_ON_LINE));
        for (FileTransfer file : fileTransfer)
            filePanel.add(new FileDownloadPanel(file));
        add(filePanel, BorderLayout.CENTER);
    }
}
