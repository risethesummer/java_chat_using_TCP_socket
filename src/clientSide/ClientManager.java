package clientSide;
import clientSide.GUI.ClientGUI;
import clientSide.GUI.utilities.FileMessage;
import clientSide.GUI.utilities.Message;
import serverSide.accounts.Account;
import serverSide.accounts.AccountFullInformation;
import serverSide.accounts.AccountShowInformation;
import sockets.handlers.client.ClientConnectAsynchronous;
import sockets.handlers.client.ClientSideHandler;
import sockets.protocols.CommandType;
import sockets.protocols.FileTransfer;
import sockets.protocols.Packet;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.DoubleConsumer;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:55 PM
 * Description: ...
 */
public class ClientManager {
    private ClientSideHandler handler = null;
    private final ClientGUI gui;
    private String userName = null;
    public static void main(String[] args)
    {
        new ClientManager();
    }

    ClientManager()
    {
        new ClientConnectAsynchronous(this::connectToServer);
        gui = new ClientGUI(this::sendMessage, this::sendFile, this::signIn, this::signUp, this::close, this::signOut, () -> new ClientConnectAsynchronous(this::reconnect));
    }

    private void connectToServer(Socket s)
    {
        handler = new ClientSideHandler(s, this::handlingMessage, this::handlingDisconnect);
        handler.setShouldStop(true);
        handler.start();
    }

    private void reconnect(Socket s)
    {
        connectToServer(s);
        gui.showLoginFrame();
    }

    private void handlingDisconnect()
    {
        handler = null;
        gui.showConnectFrame();
    }

    private boolean checkFailConnection()
    {
        if (handler == null)
        {
            handlingDisconnect();
            return true;
        }
        return false;
    }

    private Packet signIn(Account account)
    {
        try
        {
            if (checkFailConnection()){
                return null;
            }
            Packet response = handler.sendAndWaitForResponse(new Packet(handler.getSessionID(), CommandType.SIGN_IN, account), CommandType.RESPONSE);
            if (response.state())
            {
                this.userName = account.account();
                gui.getChatFrame().loadUsers((ArrayList<AccountShowInformation>)response.getPayloadAsObject());
                gui.showChatFrame();
                handler.setShouldStop(false);
                synchronized (handler)
                {
                    handler.notify();
                }
            }
            return response;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private void sendFile(FileMessage msg, DoubleConsumer showProgress)
    {
        try
        {
            if (checkFailConnection()){
                return;
            }
            handler.sendAsync(new Packet(handler.getSessionID(), userName, msg.user(), CommandType.SEND_FILE, msg.file()), showProgress);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void receiveFile(Packet msg)
    {
        try
        {
            gui.getChatFrame().addNewChatLog(new Message(msg.sender(), (String)msg.getPayloadAsObject()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void receiveMessage(Packet msg)
    {
        try
        {
            gui.getChatFrame().addNewFile(new FileMessage(msg.sender(), (FileTransfer)msg.getPayloadAsObject()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void sendMessage(Message msg)
    {
        try
        {
            if (checkFailConnection()){
                return;
            }
            handler.sendMsg(new Packet(handler.getSessionID(), userName, msg.user(), CommandType.SEND_MSG, msg.content()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean signUp(AccountFullInformation account)
    {
        try
        {
            if (checkFailConnection()){
                return false;
            }

            Packet response = handler.sendAndWaitForResponse(new Packet(handler.getSessionID(), CommandType.SIGN_UP, account));
            return response.state();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void signOut()
    {
        try
        {
            if (checkFailConnection()){
                return;
            }

            handler.sendMsg(new Packet(handler.getSessionID(), this.userName, CommandType.SIGN_OUT));
            handler.setShouldStop(true);
            gui.showLoginFrame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void handlingMessage(Packet msg)
    {
        if (msg != null)
        {
            switch (msg.cmd()) {
                case SEND_MSG -> receiveMessage(msg);
                case SEND_FILE -> receiveFile(msg);
                case NOTIFY_IN -> {
                    AccountShowInformation user = new AccountShowInformation(msg.sender(), (String) msg.getPayloadAsObject());
                    if (user.account().equals(userName))
                        return;
                    gui.getChatFrame().onlineUser(user);
                }
                case NOTIFY_OUT -> {
                    String user = msg.sender();
                    if (user.equals(userName))
                        return;
                    gui.getChatFrame().offlineUser(user);
                }
                case DISCONNECT -> handlingDisconnect();
            }
        }
    }

    public void close()
    {
        try
        {
            handler.sendMsg(new Packet(handler.getSessionID(), CommandType.DISCONNECT));
            handler.close();
            handler.join();
            gui.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
