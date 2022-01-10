package clientSide.GUI.connect;

import clientSide.GUI.TwoWaysDisposeFrame;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.connect
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 9:12 PM
 * Description: ...
 */
public class ConnectFrame extends TwoWaysDisposeFrame {

    private final JLabel label = new JLabel("You lost connection to the server! Please click the reconnect button!");
    private final JButton reconnectButton = new JButton("RECONNECT TO THE SERVER");

    public ConnectFrame(Runnable reconnect, Runnable onClose)
    {
        super("Reconnect", onClose);

        reconnectButton.addActionListener(e -> {
            label.setText("Trying to connect to the server! Please, wait for a moment!");
            reconnectButton.setEnabled(false);
            reconnect.run();
        });

        Container contentPane = this.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.PAGE_START);
        contentPane.add(reconnectButton, BorderLayout.PAGE_END);

        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible)
    {
        label.setText("You lost connection to the server! Please click the reconnect button!");
        reconnectButton.setEnabled(true);
        super.setVisible(visible);
    }
}
