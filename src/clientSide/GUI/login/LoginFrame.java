package clientSide.GUI.login;

import clientSide.GUI.TwoWaysDisposeFrame;
import clientSide.GUI.utilities.ButtonTextCallback;
import clientSide.GUI.login.panels.SignInAccountPanel;
import clientSide.GUI.login.panels.SignUpAccountPanel;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;
import sockets.protocols.packet.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Client.GUI
 * Created by NhatLinh - 19127652
 * Date 12/20/2021 - 10:58 PM
 * Description: The class representing the frame for Server.login, logout activities
 */
public class LoginFrame extends TwoWaysDisposeFrame {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final String SIGN_IN_TITLE = "SIGN IN";
    private final String SIGN_UP_TITLE = "SIGN UP";

    /**
     * Construct a new login frame with sign in and sign up callback
     * @param onSignIn the callback when user signing in
     * @param onSignUp the callback when user signing up
     */
    public LoginFrame(Function<Account, Packet> onSignIn, Predicate<AccountFullInformation> onSignUp, Runnable onClose)
    {
        super("Sign in/Sign up", onClose);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(SIGN_IN_TITLE, new SignInAccountPanel(SIGN_IN_TITLE,"Sign in", onSignIn, new ButtonTextCallback("Sign up a new account", (e) -> cardLayout.show(cardPanel, SIGN_UP_TITLE))));
        cardPanel.add(SIGN_UP_TITLE, new SignUpAccountPanel(SIGN_UP_TITLE,"Sign up", onSignUp, new ButtonTextCallback("Back to sign in", (e) -> cardLayout.show(cardPanel, SIGN_IN_TITLE))));
        cardLayout.show(cardPanel, SIGN_IN_TITLE);

        this.getContentPane().add(cardPanel);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
