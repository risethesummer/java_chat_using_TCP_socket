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
 * Description: The panel for getting username, password from users
 */
public class AccountPanel extends JPanel {

    /**
     * Username input field
     */
    protected final InputPanel accountPanel;
    /**
     * Password input field
     */
    protected final PasswordInputPanel passwordPanel;
    /**
     * Confirm button
     */
    protected final JButton confirmButton;
    /**
     * The panel for confirm section
     */
    protected final JPanel confirmPanel = new JPanel();
    /**
     * Construct a new panel with the callback when clicking the confirm button
     * @param title the text displayed on the top of the panel
     * @param buttonText the text displayed on the confirm button
     * @param back the back button and its text information
     */
    public AccountPanel(String title, String buttonText, ButtonTextCallback back)
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Create username section
        accountPanel = new InputPanel("Account");
        //Create password section
        passwordPanel = new PasswordInputPanel("Password");
        //Create buttons section
        confirmButton = new JButton(buttonText);

        JButton backButton = new JButton(back.text());
        backButton.addActionListener(back.callback());
        //Add buttons to the confirmation panel
        confirmPanel.add(confirmButton);
        confirmPanel.add(backButton);

        setBorder(BorderFactory.createTitledBorder(title));
        this.add(Box.createVerticalStrut(10));
        this.add(accountPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(passwordPanel);

    }

    /**
     * Check the fields are empty
     * @return the check result (true: no empty field, false: there's at least empty field)
     */
    protected boolean checkEmpty()
    {
        String account = accountPanel.getInputField().getText();
        String pass = passwordPanel.getPassword();
        return account == null || account.isBlank() || pass == null || pass.isBlank();
    }

    /**
     * Check the fields contains any spaces
     * @return the check result (true: no field contains spaces, false: there's at least one field containing spaces)
     */
    protected boolean checkBlank()
    {
        String account = accountPanel.getInputField().getText();
        String pass = passwordPanel.getPassword();
        return account.indexOf(' ') >= 0 || pass.indexOf(' ') >= 0;
    }
}
