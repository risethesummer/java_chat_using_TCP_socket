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

    private final JLabel label = new JLabel();

    public ConnectFrame(Runnable reconnect, Runnable onClose)
    {
        super("Reconnect", onClose);

        JButton reconnectButton = new JButton("RECONNECT TO THE SERVER");

        reconnectButton.addActionListener(e -> reconnect.run());

        Container contentPane = this.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.PAGE_START);
        contentPane.add(reconnectButton, BorderLayout.PAGE_END);

        //Hide title bar
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (visible)
            label.setText("You lost connection to the server! Please click the reconnect button!");
        super.setVisible(visible);
    }
}
