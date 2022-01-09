package serverSide;

import serverSide.accounts.Account;
import serverSide.accounts.AccountFullInformation;
import serverSide.accounts.AccountManager;
import serverSide.accounts.AccountShowInformation;
import sockets.handlers.server.ServerSideHandler;
import sockets.protocols.CommandType;
import sockets.protocols.Packet;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static sockets.protocols.ErrorContent.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:42 PM
 * Description: ...
 */
public class ServerManager {

    public static final String SERVER_IP = "localhost";
    public static final Integer SERVER_PORT = 3400;
    private final String DATA_PATH = "data.dat";

    /**
     * Store current available sockets
     */
    private final Hashtable<UUID, ServerSideHandler> handlers = new Hashtable<>();

    private final Hashtable<String, UUID> accountToSessionID = new Hashtable<>();
    /**
     * Manage accounts
     */
    private final AccountManager accountManager = new AccountManager(DATA_PATH);

    /**
     * The opened socket of server (accepting connections)
     */
    private ServerSocket socket;

    /**
     * GUI for server handling
     */
    private final ServerGUI gui;

    public ServerManager()
    {
        gui = new ServerGUI(this::openServer, this::close, this::closeProgram);
    }

    private void openServer()
    {
        try
        {
            socket = new ServerSocket(SERVER_PORT);
            while (!socket.isClosed())
            {
                Socket ss = socket.accept();
                ServerSideHandler handler = new ServerSideHandler(ss, this::handlingMessage, null);
                handlers.put(handler.getSessionID(), handler);
                handler.start();
            }
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
                case SEND_FILE, SEND_MSG -> transferMessage(msg);
                case SIGN_IN -> signIn(msg);
                case SIGN_UP -> signUp(msg);
                case SIGN_OUT -> signOut(msg);
                case DISCONNECT -> disconnectUser(msg);
            }
        }
    }

    private void disconnectUser(Packet msg)
    {
        try
        {
            handlers.remove(msg.sessionID());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void signIn(Packet msg)
    {
        try
        {
            Account account = (Account)msg.getPayloadAsObject();
            Packet response;
            if (!accountManager.checkAccount(account))
                 response = new Packet(msg.sessionID(), CommandType.RESPONSE, false, ACCOUNT_NOT_MATCHED);
            else if (accountToSessionID.contains(account.account()))
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, false, ACCOUNT_BEING_USED);
            else
            {
                accountToSessionID.put(account.account(), msg.sessionID());
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, getOnlineUsers());
                sendAll(new Packet(account.account(), CommandType.NOTIFY_IN));
                gui.addLog(account.account() + " has just signed in");
                gui.addOnlineUser(account.account());
            }
            sendMsg(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ArrayList<AccountShowInformation> getOnlineUsers()
    {
        ArrayList<AccountShowInformation> users = new ArrayList<>();
        for (AccountFullInformation acc : accountManager.getAccounts().values())
            users.add(new AccountShowInformation(acc.account().account(), acc.displayedName()));
        return users;
    }

    private void signUp(Packet msg)
    {
        try
        {
            AccountFullInformation account = (AccountFullInformation)msg.getPayloadAsObject();
            Packet response;
            if (accountManager.addAccount(account))
            {
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, true);
                gui.addLog(account.account() + " has just been signed up");
            }
            else
            {
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, false);
            }
            sendMsg(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void transferMessage(Packet msg)
    {
        try
        {
            UUID receiverID = accountToSessionID.get(msg.receiver());
            if (receiverID != null)
            {
                ServerSideHandler handler = handlers.get(receiverID);
                handler.sendMsg(msg);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void signOut(Packet msg)
    {
        try
        {
            accountToSessionID.remove(msg.receiver());
            handlers.remove(msg.sessionID());
            sendAll(new Packet(msg.sender(), CommandType.NOTIFY_OUT));
            gui.deleteUser(msg.sender());
            gui.addLog(msg.sender() + " has just signed out");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void sendMsg(Packet msg)
    {
        ServerSideHandler handler = handlers.get(msg.sessionID());
        if (handler != null)
            handler.sendMsg(msg);
    }

    private void sendAll(Packet msg)
    {
        UUID senderID = accountToSessionID.get(msg.sender());
        for (Map.Entry<UUID, ServerSideHandler> handler : handlers.entrySet())
            if (!handler.getKey().equals(senderID))
                handler.getValue().sendMsg(msg);
    }

    private void close()
    {
        sendAll(new Packet(CommandType.DISCONNECT));
        handlers.clear();
        accountToSessionID.clear();
    }

    private void closeProgram()
    {
        close();
        accountManager.saveData(DATA_PATH);
    }


    public static void main(String[] args)
    {
        new ServerManager();
    }
}
