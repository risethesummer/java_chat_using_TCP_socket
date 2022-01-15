package clientSide.GUI.chat.panels.files;

import clientSide.GUI.utilities.LabelImage;
import sockets.protocols.packet.FileTransfer;
import utilities.ImageRepository;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.chat.panels.messages.fileMessages
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:46 PM
 * Description: A panel for showing a file
 */
public class FilePanel extends JPanel {

    /**
     * The stored file
     */
    protected final FileTransfer storedFile;

    /**
     * Get the stored file
     * @return the stored file
     */
    public FileTransfer getStoredFile()
    {
        return storedFile;
    }

    /**
     * Create a panel
     * @param receiveFile the file to be stored in this panel
     */
    public FilePanel(FileTransfer receiveFile)
    {
        super(new BorderLayout());
        this.storedFile = receiveFile;

        //File name section
        JLabel nameLabel = new JLabel(storedFile.name());
        add(nameLabel, BorderLayout.PAGE_START);

        //File icon section
        LabelImage fileIcon = new LabelImage("files.png", ImageRepository.BIG);
        add(fileIcon, BorderLayout.CENTER);
    }
}
