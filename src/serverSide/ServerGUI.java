package serverSide;

import javax.swing.*;
import java.awt.*;

/**
 * serverSide
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:28 PM
 * Description: ...
 */
public class ServerGUI extends JFrame {

    private final JButton openButton = new JButton("OPEN SERVER");
    private final JButton closeButton = new JButton("CLOSE SERVER");
    private final DefaultListModel logModel = new DefaultListModel();
    private final DefaultListModel userModel = new DefaultListModel();

    private Runnable onClose;

    public ServerGUI(Runnable openCallback, Runnable closeCallback, Runnable closeFrameCallback)
    {
        this.onClose = closeFrameCallback;

        openButton.addActionListener(e -> {
            openCallback.run();
            openButton.setEnabled(false);
            closeButton.setEnabled(true);
        });
        closeButton.addActionListener(e -> {
            closeCallback.run();
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

        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void addOnlineUser(String userName)
    {
        userModel.addElement(userName);
    }

    public void deleteUser(String userName)
    {
        userModel.removeElement(userName);
    }

    public void addLog(String log)
    {
        logModel.addElement(log);
    }

    @Override
    public void dispose()
    {
        onClose.run();
        super.dispose();
    }
}
