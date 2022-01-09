package clientSide.GUI;

import clientSide.GUI.chat.ChatFrame;
import clientSide.GUI.connect.ConnectFrame;
import clientSide.GUI.login.LoginFrame;
import clientSide.GUI.utilities.FileMessage;
import clientSide.GUI.utilities.Message;
import serverSide.accounts.Account;
import serverSide.accounts.AccountFullInformation;
import sockets.protocols.Packet;

import java.util.function.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:10 PM
 * Description: ...
 */
public class ClientGUI {

    private final LoginFrame loginFrame;
    private final ChatFrame chatFrame;
    private final ConnectFrame connectFrame;

    public ChatFrame getChatFrame() {
        return chatFrame;
    }


    public ClientGUI(Consumer<Message> onSendMsg,
                     BiConsumer<FileMessage, DoubleConsumer> onSendFile,
                     Function<Account, Packet> onSignIn,
                     Predicate<AccountFullInformation> onSignUp,
                     Runnable onClose,
                     Runnable onCloseChatFrame,
                     Runnable reconnect)
    {
        connectFrame = new ConnectFrame(reconnect, onClose);
        loginFrame = new LoginFrame(onSignIn, onSignUp, onClose);
        chatFrame = new ChatFrame(onSendMsg, onSendFile, onCloseChatFrame);
    }

    public void dispose()
    {
        connectFrame.disposeNoTrigger();
        loginFrame.disposeNoTrigger();
        chatFrame.disposeNoTrigger();
    }

    public void showConnectFrame()
    {
        loginFrame.setVisible(false);
        chatFrame.setVisible(false);
        connectFrame.setVisible(true);
    }

    public void showLoginFrame()
    {
        connectFrame.setVisible(false);
        chatFrame.setVisible(false);
        loginFrame.setVisible(true);
    }

    public void showChatFrame()
    {
        connectFrame.setVisible(false);
        loginFrame.setVisible(false);
        chatFrame.setVisible(true);
    }
}
