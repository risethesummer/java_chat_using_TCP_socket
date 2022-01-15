package clientSide.GUI.login;

import clientSide.GUI.utilities.TwoWaysDisposeFrame;
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
 * Description: The class representing the frame for login, logout activities
 */
public class LoginFrame extends TwoWaysDisposeFrame {

    /**
     * Card layout for putting the two function panels in the same place
     */
    private final CardLayout cardLayout;
    /**
     * Card panel storing the two panels
     */
    private final JPanel cardPanel;
    /**
     * The title for the signing in panel
     */
    private final String SIGN_IN_TITLE = "SIGN IN";
    /**
     * The title for the signing up panel
     */
    private final String SIGN_UP_TITLE = "SIGN UP";

    /**
     * Construct a new login frame with sign in and sign up callback
     * @param onSignIn the callback when the user signs in
     * @param onSignUp the callback when the user signs up
     * @param onClose the callback when closing the login panel
     */
    public LoginFrame(Function<Account, Packet> onSignIn, Predicate<AccountFullInformation> onSignUp, Runnable onClose)
    {
        super("Sign in/Sign up (Close to exit the program)", "user.png", onClose);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        //Add the signing in function -> show the signing up panel when closing it
        cardPanel.add(SIGN_IN_TITLE, new SignInAccountPanel(SIGN_IN_TITLE,"Sign in", onSignIn, new ButtonTextCallback("Sign up a new username", (e) -> cardLayout.show(cardPanel, SIGN_UP_TITLE))));
        //Add the signing up function -> show the signing in panel when closing it
        cardPanel.add(SIGN_UP_TITLE, new SignUpAccountPanel(SIGN_UP_TITLE,"Sign up", onSignUp, new ButtonTextCallback("Back to sign in", (e) -> cardLayout.show(cardPanel, SIGN_IN_TITLE))));
        cardLayout.show(cardPanel, SIGN_IN_TITLE);
        this.getContentPane().add(cardPanel);

        setMinimumSize(new Dimension(700, 200));
        pack();
        //Make the frame stands in the center of the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
