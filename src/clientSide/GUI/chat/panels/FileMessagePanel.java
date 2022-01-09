package clientSide.GUI.chat.panels;

import clientSide.GUI.utilities.FileMessage;
import clientSide.GUI.utilities.Message;
import sockets.protocols.FileTransfer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:06 PM
 * Description: ...
 */
public class FileMessagePanel extends MessagePanel {

    private final FileTransfer fileTransfer;

    public FileMessagePanel(FileMessage msg) {
        super(new Message(msg.user(), msg.file().name()));
        fileTransfer = msg.file();
        contentField.setToolTipText("Click to download file!");
        contentField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                askToDownloadFile();
            }
        });
    }

    private void askToDownloadFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select directory to save");
        chooser.setSelectedFile(new File(fileTransfer.name()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            try
            {
                FileOutputStream outputStream = new FileOutputStream(path);
                outputStream.write(fileTransfer.content());
                outputStream.flush();
                outputStream.close();
                JOptionPane.showMessageDialog(this, "Downloaded the file successfully!");
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Failed to download the file!");
            }
        };
    }
}
