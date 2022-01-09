package clientSide.GUI.chat.panels;

import clientSide.GUI.utilities.FileMessage;
import clientSide.GUI.utilities.Message;
import serverSide.accounts.AccountShowInformation;
import sockets.protocols.FileTransfer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:40 PM
 * Description: ...
 */
public class ChatContentPanel extends JPanel {

    private final JTextArea inputField = new JTextArea();
    private final JTextField filePathField = new JTextField();
    private final JPanel logPanel = new JPanel();
    private final AccountShowInformation user;
    private boolean isOnline = true;

    public ChatContentPanel(AccountShowInformation user, Consumer<Message> onSend, BiConsumer<FileMessage, DoubleConsumer> onSendFile)
    {
        JPanel panel = new JPanel(new BorderLayout());

        this.user = user;

        //Add message content section
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.PAGE_AXIS));
        JScrollPane logScroll = new JScrollPane(logPanel);
        panel.add(logScroll, BorderLayout.CENTER);

        //See file section
        JPanel filePanel = new JPanel(new BorderLayout());
        JButton getFileButton = new JButton("Choose file");
        getFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.isFile())
                    filePathField.setText(file.getAbsolutePath());
            }
        });
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(getFileButton, BorderLayout.LINE_END);

        //Add sending message section
        inputField.setLineWrap(true);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        JButton sendMsgButton = new JButton("Send");
        sendMsgButton.addActionListener(e ->
        {
            if (!isOnline)
            {
                JOptionPane.showMessageDialog(this, "The user is currently offline");
                return;
            }

            String msg = inputField.getText();
            if (msg != null && !msg.isBlank())
            {
                onSend.accept(new Message(this.user.account(), msg));
                inputField.setText(null);
                addMsg(new Message("You", msg));
            }

            String currentPath = filePathField.getText();
            if (currentPath != null && new File(currentPath).isFile())
            {
                try
                {
                    String fileName = new File(currentPath).getName();
                    FileInputStream stream = new FileInputStream(currentPath);
                    FileTransfer fileTransfer = new FileTransfer(fileName, stream.readAllBytes());
                    stream.close();

                    FileMessage show = new FileMessage("You", fileTransfer);
                    FileMessage sent = new FileMessage(this.user.account(), fileTransfer);

                    SendFileMessagePanel sendPanel = new SendFileMessagePanel(show);
                    addMsg("You", sendPanel);
                    onSendFile.accept(sent, p -> sendPanel.getProgressBar().setValue((int)p));
                    filePathField.setText(null);
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        });
        inputPanel.add(sendMsgButton, BorderLayout.LINE_END);

        //File and input panel
        JPanel outerInputPanel = new JPanel(new BorderLayout());
        outerInputPanel.add(filePanel, BorderLayout.PAGE_START);
        outerInputPanel.add(inputPanel, BorderLayout.CENTER);

        panel.add(outerInputPanel, BorderLayout.PAGE_END);
        add(panel);
    }

    public void addMsg(Message msg)
    {
        MessagePanel messagePanel = new MessagePanel(new Message(user.displayedName(), msg.content()));
        add(msg.user(), messagePanel);
    }

    public void addMsg(String user, MessagePanel panel)
    {
        if (this.user.account().equals(user))
            panel.setAlignmentX(LEFT_ALIGNMENT);
        else
            panel.setAlignmentX(RIGHT_ALIGNMENT);
        SwingUtilities.invokeLater(() -> logPanel.add(panel));
    }

    public void setOnline(boolean b) {
        isOnline = b;
    }
}
