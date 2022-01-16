package clientSide.GUI.login.panels;

import clientSide.GUI.utilities.ButtonTextCallback;
import clientSide.GUI.utilities.InputPanel;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;

import javax.swing.*;
import java.util.function.Predicate;

/**
 * clientSide.GUI.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:51 PM
 * Description: The panel for singing up accounts
 */
public class SignUpAccountPanel extends AccountPanel {

    /**
     * The field for inputting displayed name
     */
    private final InputPanel displayedName = new InputPanel("Displayed name (showed to the others)");

    /**
     * Construct a new panel with the callback when clicking the confirm button
     *
     * @param title          the text displayed on the top of the panel
     * @param buttonText     the text displayed on the confirm button
     * @param onConfirmClick the callback when clicking the confirm button
     * @param back           the back button and its text information
     */
    public SignUpAccountPanel(String title, String buttonText, Predicate<AccountFullInformation> onConfirmClick, ButtonTextCallback back) {
        super(title, buttonText, back);

        confirmButton.addActionListener(e->{
            try
            {
                if (checkEmpty())
                {
                    JOptionPane.showMessageDialog(this, "Can not leave the forms empty. Pls! Input information for them!");
                    return;
                }

                if (checkBlank())
                {
                    JOptionPane.showMessageDialog(this, "Account and password can not contain space character. Pls! Check your inputs again!");
                    return;
                }
                //Can not create an account with system username
                if (accountPanel.getInputField().getText().equals("system") && accountPanel.getInputField().getText().equals("System"))
                {
                    JOptionPane.showMessageDialog(this, "The username can not be system. Please, try another one!");
                    return;
                }

                AccountFullInformation account = new AccountFullInformation(new Account(accountPanel.getInputField().getText(), passwordPanel.getPassword()),
                        displayedName.getInputField().getText());
                //Call the callback to create a new account
                if (onConfirmClick.test(account))
                    JOptionPane.showMessageDialog(this, "Create account successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(this, "Failed to create account!", "FAILED", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(this, "The program has met an error!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.add(Box.createVerticalStrut(10));
        this.add(displayedName);
        this.add(Box.createVerticalStrut(10));
        this.add(confirmPanel);
    }


    /**
     * Check the fields are empty
     * @return the check result (true: no empty field, false: there's at least empty field)
     */
    @Override
    protected boolean checkEmpty()
    {
        String displayed = displayedName.getInputField().getText();
        return displayed == null || displayed.isBlank() || super.checkEmpty();
    }
}
