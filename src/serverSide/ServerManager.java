package serverSide;

import serverSide.GUI.ServerGUI;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;
import sockets.protocols.accounts.AccountManager;
import sockets.protocols.accounts.AccountShowInformation;
import sockets.handlers.server.AsynchronousOpenSocket;
import sockets.handlers.server.ServerSideHandler;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;
import java.net.ServerSocket;
import java.util.*;

import static sockets.protocols.packet.ErrorContent.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:42 PM
 * Description: ...
 */
public class ServerManager {

    public static final String SERVER_IP = "localhost";
    public static final Integer SERVER_PORT = 19500;
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
            //If current socket is closed or not initialized
            if (socket == null || socket.isClosed())
            {
                socket = new ServerSocket(SERVER_PORT);
                new AsynchronousOpenSocket(socket, s -> {
                    ServerSideHandler handler = new ServerSideHandler(s, this::handlingMessage, this::disconnectUser);
                    handlers.put(handler.getSessionID(), handler);
                    handler.start();
                });
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
                case DISCONNECT -> disconnectUser(msg.sessionID());
                case RECONNECT ->  reconnectUser(msg);
            }
        }
    }

    private void reconnectUser(Packet msg)
    {
        Packet packet;
        if (accountToSessionID.contains(msg.sender()))
            packet = new Packet(msg.sessionID(), CommandType.RESPONSE, getOnlineUsers(msg.sender()));
        else
            packet = new Packet(msg.sessionID(), CommandType.RESPONSE, false);
        sendMsg(packet);
    }


    private void disconnectUser(UUID sessionID)
    {
        try
        {
            ServerSideHandler handler = handlers.get(sessionID);
            if (handler != null && !handler.isClosed())
            {
                try
                {
                    handler.close();
                    handler.join();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            handlers.remove(sessionID);
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
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, getOnlineUsers(account.account()));
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

    private ArrayList<AccountShowInformation> getOnlineUsers(String except)
    {
        ArrayList<AccountShowInformation> users = new ArrayList<>();
        for (AccountFullInformation acc : accountManager.getAccounts().values())
            if (!acc.account().account().equals(except))
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
            accountToSessionID.remove(msg.sender());
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
        //Get sender id to not send the message to the sender
        UUID senderID = msg.sender() == null ? null : accountToSessionID.get(msg.sender());
        //For each handler, if it's not the sender, send the msg to it
        for (Map.Entry<UUID, ServerSideHandler> handler : handlers.entrySet())
            //If not the handler of the sender
            if (!handler.getKey().equals(senderID))
                handler.getValue().sendMsg(msg);
    }

    private void close()
    {
        try
        {
            if (socket != null && !socket.isClosed())
            {
                sendAll(new Packet(CommandType.DISCONNECT));
                for (ServerSideHandler handler : handlers.values())
                {
                    if (!handler.isClosed())
                    {
                        try
                        {
                            handler.close();
                            handler.join();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                socket.close();
                socket = null;
            }
            handlers.clear();
            accountToSessionID.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
