package clientSide.GUI.chat.panels.files;

import clientSide.GUI.utilities.ButtonImage;
import sockets.protocols.packet.FileTransfer;
import utilities.ImageRepository;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * clientSide.GUI.chat.panels.messages.fileMessages
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:48 PM
 * Description: A panel for showing a file with a delete button
 */
public class FileDeletePanel extends FilePanel {

    /**
     * Create a panel
     * @param receiveFile the file to be stored in this panel
     * @param onDelete the delete callback
     */
    public FileDeletePanel(FileTransfer receiveFile, Consumer<Component> onDelete) {
        super(receiveFile);
        //Delete section
        ButtonImage deleteButton = new ButtonImage("remove.png", ImageRepository.MEDIUM, "Delete files");
        deleteButton.addActionListener(e -> {
            //Call the callback and set this panel to be invisible
            onDelete.accept(this);
            setVisible(false);
        });
        JPanel outerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerButtonPanel.add(deleteButton);
        add(outerButtonPanel, BorderLayout.PAGE_END);
    }
}
