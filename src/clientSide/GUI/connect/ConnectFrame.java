package clientSide.GUI.connect;

import clientSide.GUI.utilities.TwoWaysDisposeFrame;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.connect
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 9:12 PM
 * Description: The frame for reconnecting
 */
public class ConnectFrame extends TwoWaysDisposeFrame {

    /**
     * The label showing current status
     */
    private final JLabel label = new JLabel("You lost connection to the server! Please click the reconnect button!");
    /**
     * Reconnect button
     */
    private final JButton reconnectButton = new JButton("RECONNECT TO THE SERVER");

    /**
     * Create a new frame
     * @param reconnect reconnecting to the server callback
     * @param onClose closing frame callback
     */
    public ConnectFrame(Runnable reconnect, Runnable onClose)
    {
        super("Reconnect (Close to exit the program)", "connect.png", onClose);

        reconnectButton.addActionListener(e -> {
            label.setText("Trying to connect to the server! Please, wait for a moment!");
            //Disable the connect button after clicking it (wait for the connection)
            reconnectButton.setEnabled(false);
            reconnect.run();
        });

        Container contentPane = this.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.PAGE_START);
        contentPane.add(reconnectButton, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Set the frame's visibility
     * @param visible the visible value
     */
    @Override
    public void setVisible(boolean visible)
    {
        label.setText("You lost connection to the server! Please click the reconnect button!");
        reconnectButton.setEnabled(true);
        super.setVisible(visible);
    }
}
