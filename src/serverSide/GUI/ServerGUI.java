package serverSide.GUI;

import utilities.ImageRepository;

import javax.swing.*;
import java.awt.*;

import static utilities.StringHandler.addDateTime;

/**
 * serverSide
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:28 PM
 * Description: The server GUI class
 */
public class ServerGUI extends JFrame {

    /**
     * Open server button (can only click when the server if off)
     */
    private final JButton openButton = new JButton("OPEN SERVER");

    /**
     * Close server button (can only click when the server is on)
     */
    private final JButton closeButton = new JButton("CLOSE SERVER");

    /**
     * Log user activities
     */
    private final DefaultListModel logModel = new DefaultListModel();

    /**
     * Show current online users
     */
    private final DefaultListModel userModel = new DefaultListModel();

    /**
     * Closing callback when trying to turn off the frame
     */
    private final Runnable onClose;

    /**
     * Create a new server GUI
     * @param openCallback the opening server callback
     * @param closeCallback the closing server callback
     * @param closeFrameCallback the closing program callback
     */
    public ServerGUI(Runnable openCallback, Runnable closeCallback, Runnable closeFrameCallback)
    {
        setTitle("Server");

        this.onClose = closeFrameCallback;

        setIconImage(ImageRepository.getInstance().getImage("server.png", ImageRepository.SMALL));

        //Add opening server action to the open button
        openButton.addActionListener(e -> {
            openCallback.run();
            //Enable close button, disable open button
            openButton.setEnabled(false);
            closeButton.setEnabled(true);
        });

        //Add closing server action to the close button
        closeButton.addActionListener(e -> {
            closeCallback.run();
            //Enable open button, disable close button
            openButton.setEnabled(true);
            closeButton.setEnabled(false);
        });
        closeButton.setEnabled(false);

        JList logList = new JList(logModel);
        JList userList = new JList(userModel);

        JTabbedPane listTab = new JTabbedPane();
        listTab.add("Online users", userList);
        listTab.add("Server log", logList);

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(openButton, BorderLayout.PAGE_START);
        contentPane.add(closeButton, BorderLayout.PAGE_END);
        contentPane.add(listTab);

        setMinimumSize(new Dimension(500, 500));
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Show a new online user
     * @param userName the username
     */
    public void addOnlineUser(String userName)
    {
        SwingUtilities.invokeLater(() ->  userModel.addElement(userName));
    }

    /**
     * Delete an online user from log
     * @param userName the user
     */
    public void deleteUser(String userName)
    {
        SwingUtilities.invokeLater(() -> userModel.removeElement(userName));
    }

    /**
     * Add a new log (client activities)
     * @param log the added log
     */
    public void addLog(String log)
    {
        //Add datetime to the log
        SwingUtilities.invokeLater(() -> logModel.addElement(addDateTime(log)));
    }

    @Override
    public void dispose()
    {
        onClose.run();
        super.dispose();
    }
}
