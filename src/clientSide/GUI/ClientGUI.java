package clientSide.GUI;

import clientSide.GUI.chat.ChatFrame;
import clientSide.GUI.connect.ConnectFrame;
import clientSide.GUI.login.LoginFrame;
import clientSide.messages.FileMessage;
import clientSide.messages.Message;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;
import sockets.protocols.packet.Packet;

import javax.swing.*;
import java.util.function.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:10 PM
 * Description: Managing client GUIs
 */
public class ClientGUI {

    /**
     * The login frame (sign in/sign up)
     */
    private final LoginFrame loginFrame;

    /**
     * The chat frame
     */
    private final ChatFrame chatFrame;

    /**
     * The connecting frame showing when the user loses connection
     */
    private final ConnectFrame connectFrame;

    /**
     * Get the chat frame
     * @return the chat frame property
     */
    public ChatFrame getChatFrame() {
        return chatFrame;
    }

    /**
     * Construct a new client GUI with callbacks
     * @param onSendMsg sending message callback (called when the user sends a text message)
     * @param onSendFile sending file callback (called when the user sends a file message)
     * @param onSignIn signing in callback (called when the user wants to create a new account)
     * @param onSignUp signing up callback (called when the user wants to sign in the system)
     * @param onClose closing program callback (called when the user closes the connecting frame or the login frame)
     * @param onCloseChatFrame closing chat frame callback
     * @param reconnect reconnecting callback (called when the user tries to connect to the server again)
     */
    public ClientGUI(Consumer<Message> onSendMsg,
                     BiConsumer<FileMessage, IntConsumer> onSendFile,
                     Function<Account, Packet> onSignIn,
                     Predicate<AccountFullInformation> onSignUp,
                     Runnable onClose,
                     Runnable onCloseChatFrame,
                     Runnable reconnect)
    {
        //Create the frames
        connectFrame = new ConnectFrame(reconnect, onClose);
        loginFrame = new LoginFrame(onSignIn, onSignUp, onClose);
        chatFrame = new ChatFrame(onSendMsg, onSendFile, onCloseChatFrame);
    }

    /**
     * Dispose the client GUI (dispose all the frames)
     */
    public void dispose()
    {
        connectFrame.disposeNoTrigger();
        loginFrame.disposeNoTrigger();
        chatFrame.disposeNoTrigger();
    }

    /**
     * Show the connecting frame (and hide the others)
     */
    public void showConnectFrame()
    {
        loginFrame.setVisible(false);
        chatFrame.setVisible(false);
        connectFrame.setVisible(true);
    }

    /**
     * Show the login frame (and hide the others)
     */
    public void showLoginFrame()
    {
        connectFrame.setVisible(false);
        chatFrame.setVisible(false);
        loginFrame.setVisible(true);
    }

    /**
     * Show the chat frame (and hide the others)
     */
    public void showChatFrame()
    {
        connectFrame.setVisible(false);
        loginFrame.setVisible(false);
        chatFrame.setVisible(true);
    }
}
