package clientSide.GUI.chat.panels.files;

import clientSide.GUI.utilities.ButtonImage;
import sockets.protocols.packet.FileTransfer;
import utilities.ImageRepository;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * clientSide.GUI.chat.panels.messages
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:07 PM
 * Description: A panel for showing a file with a download button
 */
public class FileDownloadPanel extends FilePanel {

    /**
     * Create a panel
     * @param receiveFile the file to be stored in this panel
     */
    public FileDownloadPanel(FileTransfer receiveFile)
    {
        super(receiveFile);
        //Download section
        ButtonImage downloadButton = new ButtonImage("download.png", ImageRepository.SMALL, "Download files");
        //Add listener to the download button
        downloadButton.addActionListener(e -> askToDownloadFile());
        JPanel outerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outerButtonPanel.add(downloadButton);
        add(outerButtonPanel, BorderLayout.PAGE_END);
    }

    private void askToDownloadFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select directory to save");
        chooser.setSelectedFile(new File(storedFile.name()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            try
            {
                FileOutputStream outputStream = new FileOutputStream(path);
                outputStream.write(this.storedFile.content());
                outputStream.flush();
                outputStream.close();
                JOptionPane.showMessageDialog(this, "Downloaded the files successfully!");
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Failed to download the files!");
            }
        }
    }
}
