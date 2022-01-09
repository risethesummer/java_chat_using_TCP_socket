package clientSide.GUI.login.panels;

import clientSide.GUI.utilities.ButtonTextCallback;
import clientSide.GUI.utilities.InputPanel;
import clientSide.GUI.utilities.PasswordInputPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Client.GUI.Server.login
 * Created by NhatLinh - 19127652
 * Date 12/20/2021 - 10:59 PM
 * Description: The panel for getting account, password from users
 */
public class AccountPanel extends JPanel {

    protected final InputPanel accountPanel;
    protected final PasswordInputPanel passwordPanel;
    protected final JButton confirmButton;
    protected final JPanel confirmPanel;
    /**
     * Construct a new panel with the callback when clicking the confirm button
     * @param title the text displayed on the top of the panel
     * @param buttonText the text displayed on the confirm button
     * @param back the back button and its text information
     */
    public AccountPanel(String title, String buttonText, ButtonTextCallback back)
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Create account section
        accountPanel = new InputPanel("Account");
        //Create password section
        passwordPanel = new PasswordInputPanel("Password");
        //Create buttons section
        confirmPanel = new JPanel(new FlowLayout());
        confirmButton = new JButton(buttonText);

        JButton backButton = new JButton(back.text());
        backButton.addActionListener(back.callback());
        //Add buttons to the confirmation panel
        confirmPanel.add(confirmButton);
        confirmPanel.add(backButton);

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Default", Font.BOLD, 15));
        this.add(Box.createVerticalStrut(20));
        this.add(label);
        //Add sections to main panel
        this.add(Box.createVerticalStrut(10));
        this.add(accountPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(passwordPanel);
    }

    protected boolean checkEmpty()
    {
        String account = accountPanel.getInputField().getText();
        String pass = passwordPanel.getPassword();
        return account == null || account.isBlank() || pass == null || pass.isBlank();
    }

    protected boolean checkBlank()
    {
        String account = accountPanel.getInputField().getText();
        String pass = passwordPanel.getPassword();
        return account.indexOf(' ') >= 0 || pass.indexOf(' ') >= 0;
    }
}
