package clientSide.GUI.chat.panels;

import clientSide.GUI.chat.panels.messages.MessagePanel;
import clientSide.GUI.chat.panels.files.FileDeletePanel;
import clientSide.GUI.chat.panels.messages.fileMessages.SendFileMessagePanel;
import clientSide.GUI.chat.panels.messages.textMessages.TextMessagePanel;
import clientSide.GUI.utilities.ButtonImage;
import clientSide.messages.FileMessage;
import clientSide.messages.Message;
import sockets.protocols.accounts.AccountShowInformation;
import sockets.protocols.packet.FileTransfer;
import utilities.ImageRepository;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:40 PM
 * Description: ...
 */
public class ChatContentPanel extends JPanel {

    /**
     * Alignment flag for the client
     */
    public static final boolean SELF = false;
    /**
     * Alignment flag for the other user
     */
    public static final boolean OTHER = true;
    /**
     * The sender of messages from the client
     */
    public static final String YOUR_SELF = "Yourself";
    /**
     * Chat text input field
     */
    private final JTextArea inputField = new JTextArea();
    /**
     * Current files field
     */
    private final JPanel filesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    /**
     * The panel for storing the client's messages
     */
    private final JPanel rightLogPanel = new JPanel();
    /**
     * The panel for storing the other user's messages
     */
    private final JPanel leftLogPanel = new JPanel();
    /**
     * Store the other user information
     */
    private final AccountShowInformation user;
    /**
     * For placing the left log and right log panel
     */
    private final JPanel logPanel = new JPanel(new GridLayout(1, 2));

    /**
     * Determine the user is online or not
     */
    private boolean isOnline = true;

    /**
     * Create a new chat panel
     * @param user the receiver of this chat
     * @param onSend sending messages callback
     * @param onSendFile sending files callback
     */
    public ChatContentPanel(AccountShowInformation user, Consumer<Message> onSend, BiConsumer<FileMessage, IntConsumer> onSendFile)
    {
        super(new BorderLayout());

        this.user = user;

        //Add message content section
        logPanel.setBorder(BorderFactory.createTitledBorder(logPanel.getBorder(), user.displayedName() + " (online)", TitledBorder.TOP, TitledBorder.CENTER, new Font("DIALOGUE", Font.BOLD, 14), Color.GREEN));
        leftLogPanel.setLayout(new BoxLayout(leftLogPanel, BoxLayout.PAGE_AXIS));
        rightLogPanel.setLayout(new BoxLayout(rightLogPanel, BoxLayout.PAGE_AXIS));
        JScrollPane chatScroll = new JScrollPane(logPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        logPanel.add(leftLogPanel);
        logPanel.add(rightLogPanel);
        add(chatScroll, BorderLayout.CENTER);

        //Setting for dropping files on the chat
        //https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
        this.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    //For each chosen file -> create a new file panel for it
                    for (File file : droppedFiles)
                    {
                        try
                        {
                            FileTransfer fileTransfer = FileTransfer.getFile(file.getAbsolutePath());
                            //Create a file panel for the file (send delete callback that delete the panel from the files panel)
                            FileDeletePanel fileDeletePanel = new FileDeletePanel(fileTransfer, (p) -> {
                                filesPanel.remove(p);
                                updateUI();
                            });
                            filesPanel.add(fileDeletePanel);
                            updateUI();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //Browse files section
        JPanel filePanel = new JPanel(new BorderLayout());
        ButtonImage getFileButton = new ButtonImage("browse.png", ImageRepository.MEDIUM, "Browse files (or drop files on the path field)");
        getFileButton.addActionListener(e -> {
            try
            {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.isFile())
                    {
                        FileTransfer fileTransfer = FileTransfer.getFile(file.getAbsolutePath());
                        //Create a file panel for the browsed file
                        FileDeletePanel fileDeletePanel = new FileDeletePanel(fileTransfer, (p) -> {
                            filesPanel.remove(p);
                            updateUI();
                        });
                        filesPanel.add(fileDeletePanel);
                        this.updateUI();
                    }
                }
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(this, "Could not select the files! Please, try it again!");
                exception.printStackTrace();
            }
        });
        JScrollPane fileScroll = new JScrollPane(filesPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        filePanel.add(fileScroll, BorderLayout.CENTER);
        filePanel.add(getFileButton, BorderLayout.LINE_END);

        //Add sending message section
        inputField.setLineWrap(true);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        ButtonImage sendMsgButton = new ButtonImage("send.png", ImageRepository.MEDIUM, "Send message");

        //Send message listener
        //Try to send the text message, then try to send the file message (if it exists at least 1 chosen file)
        sendMsgButton.addActionListener(e ->
        {
            if (!isOnline)
            {
                JOptionPane.showMessageDialog(this, "The user is currently offline!");
                return;
            }

            //Get text message from the input text field
            String msg = inputField.getText();
            if (msg != null && !msg.isBlank())
            {
                try
                {
                    //Send the text message
                    onSend.accept(new Message(this.user.username(), msg));
                    addMsg(new Message(YOUR_SELF, msg), SELF);
                }
                catch (Exception exception)
                {
                    JOptionPane.showMessageDialog(this, "Failed to send message! Please, try it later!", "COULD NOT SEND MESSAGE", JOptionPane.ERROR_MESSAGE);
                    exception.printStackTrace();
                }
                inputField.setText(null);
            }
            updateUI();

            //Get the chosen files from the files panel
            ArrayList<FileTransfer> files = new ArrayList<>(filesPanel.getComponentCount());
            for (Component c : filesPanel.getComponents())
            {
                try
                {
                    files.add(((FileDeletePanel) c).getStoredFile());
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }

            //At least transfer 1 file
            if (files.size() > 0)
            {
                FileMessage show = new FileMessage(YOUR_SELF, files);
                FileMessage sent = new FileMessage(this.user.username(), files);
                SendFileMessagePanel sendPanel = new SendFileMessagePanel(show);
                addMsg(sendPanel, SELF);
                //Send the callback setting the progress bar's value
                onSendFile.accept(sent, (i) -> SwingUtilities.invokeLater(() -> sendPanel.getProgressBar().setValue(i)));
                filesPanel.removeAll();
            }
            updateUI();
        });
        inputPanel.add(sendMsgButton, BorderLayout.LINE_END);

        //File and input panel
        JPanel outerInputPanel = new JPanel(new BorderLayout());
        outerInputPanel.add(filePanel, BorderLayout.PAGE_START);
        outerInputPanel.add(inputPanel, BorderLayout.CENTER);
        add(outerInputPanel, BorderLayout.PAGE_END);
    }

    /**
     * Add a new message to the chat panel
     * @param msg the message
     * @param left the message should stand in the left or right side
     */
    public void addMsg(Message msg, boolean left)
    {
        MessagePanel messagePanel = new TextMessagePanel(new Message(msg.user(), msg.content()));
        addMsg(messagePanel, left);
    }

    /**
     * Add a message panel to the chat panel
     * @param panel the message panel
     * @param left the message should stand in the left or right side
     */
    public void addMsg(MessagePanel panel, boolean left)
    {
        int height = panel.getPreferredSize().height;
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        if (left)
            panel.setAlignmentX(LEFT_ALIGNMENT);
        else
            panel.setAlignmentX(RIGHT_ALIGNMENT);
        Runnable addThread;
        //Add the panel to the main side then add an invisible struct to the other side
        //To make the chat in shape of zic zag
        if (left)
            addThread = () -> {
                leftLogPanel.add(panel);
                rightLogPanel.add(Box.createVerticalStrut(height));
            };
        else
            addThread = () -> {
                rightLogPanel.add(panel);
                leftLogPanel.add(Box.createVerticalStrut(height));
            };
        SwingUtilities.invokeLater(() -> {
            addThread.run();
            updateUI();
        });
    }

    /**
     * Mark the user of this chat as online
     * @param b the online value
     */
    public void setOnline(boolean b) {
        isOnline = b;
        if (b)
            logPanel.setBorder(BorderFactory.createTitledBorder(logPanel.getBorder(), user.displayedName() + " (online)", TitledBorder.TOP, TitledBorder.CENTER, new Font("Default", Font.BOLD, 14), Color.GREEN));
        else
            logPanel.setBorder(BorderFactory.createTitledBorder(logPanel.getBorder(), user.displayedName() + " (offline)", TitledBorder.TOP, TitledBorder.CENTER, new Font("Default", Font.BOLD, 14), Color.GREEN));
    }
}
