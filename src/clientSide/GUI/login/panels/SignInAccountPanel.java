package clientSide.GUI.login.panels;

import clientSide.GUI.utilities.ButtonTextCallback;
import serverSide.accounts.Account;
import sockets.protocols.Packet;

import javax.swing.*;
import java.util.function.Function;

/**
 * clientSide.GUI.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:56 PM
 * Description: ...
 */
public class SignInAccountPanel extends AccountPanel {
    /**
     * Construct a new panel with the callback when clicking the confirm button
     *
     * @param title          the text displayed on the top of the panel
     * @param buttonText     the text displayed on the confirm button
     * @param onConfirmClick the callback when clicking the confirm button
     * @param back           the back button and its text information
     */
    public SignInAccountPanel(String title, String buttonText, Function<Account, Packet> onConfirmClick, ButtonTextCallback back) {
        super(title, buttonText, back);

        confirmButton.addActionListener(e->{
            try
            {
                if (checkBlank())
                {
                    JOptionPane.showMessageDialog(this, "Account and password can not contain space character. Pls! Check your inputs again!");
                    return;
                }

                Account account = new Account(accountPanel.getInputField().getText(), passwordPanel.getPassword());
                Packet response = onConfirmClick.apply(account);
                if (!response.state())
                    JOptionPane.showMessageDialog(this, response.getPayloadAsObject());
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(this, "The program has met an error!");
            }
        });

        this.add(Box.createVerticalStrut(10));
        this.add(confirmPanel);
    }
}
