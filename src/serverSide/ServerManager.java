package serverSide;

import serverSide.GUI.ServerGUI;
import serverSide.accounts.AccountManager;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;
import sockets.protocols.accounts.AccountShowInformation;
import sockets.handlers.server.AcceptConnectionAsync;
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
 * Description: The class managing the server activities
 */
public class ServerManager {

    /**
     * The fixed server host
     */
    public static final String SERVER_IP = "localhost";

    /**
     * The fixed server port
     */
    public static final Integer SERVER_PORT = 19500;

    /**
     * The path storing users data
     */
    private final String DATA_PATH = "data.dat";

    /**
     * Store current available sockets
     */
    private final Hashtable<UUID, ServerSideHandler> handlers = new Hashtable<>();

    /**
     * Store current online users (map from username to session id)
     */
    private final Hashtable<String, UUID> accountToSessionID = new Hashtable<>();

    /**
     * Manage user accounts
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


    private ServerManager()
    {
        gui = new ServerGUI(this::openServer, this::close, this::closeProgram);
    }

    /**
     * Open the server for accepting incoming connections
     */
    private void openServer()
    {
        try
        {
            //If current socket is closed or not initialized
            if (socket == null || socket.isClosed())
            {
                socket = new ServerSocket(SERVER_PORT);
                //Use another thread for accepting connections (not block the GUI)
                new AcceptConnectionAsync(socket, s -> {
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

    /**
     * Handling messages from clients by its type
     * @param msg the message from clients
     */
    private void handlingMessage(Packet msg)
    {
        if (msg != null)
        {
            switch (msg.cmd()) {
                //Send files or send message
                case SEND_FILE, SEND_MSG -> transferMessage(msg);
                case SIGN_IN -> signIn(msg);
                case SIGN_UP -> signUp(msg);
                case SIGN_OUT -> signOut(msg);
                case DISCONNECT -> disconnectUser(msg.sessionID());
                case RECONNECT ->  reconnectUser(msg);
            }
        }
    }

    /**
     * Handling for a client want to reconnect (in case the client has signed in before)
     * @param msg the reconnect request from the client
     */
    private void reconnectUser(Packet msg)
    {
        Packet packet;
        //Check the user has signed in or not (check from current online users)
        //Response a success message if the user has signed, otherwise response a failure message
        if (accountToSessionID.contains(msg.sender()))
            packet = new Packet(msg.sessionID(), CommandType.RESPONSE, getOnlineUsers(msg.sender()));
        else
            packet = new Packet(msg.sessionID(), CommandType.RESPONSE, false);
        sendMsg(packet);
    }

    /**
     * Handling for a client want to disconnect from the server
     * @param sessionID the session id of the client
     */
    private void disconnectUser(UUID sessionID)
    {
        try
        {
            //Stop the receiving messages thread of the client (end the handler thread)
            ServerSideHandler handler = handlers.get(sessionID);
            if (handler != null && !handler.isClosed())
            {
                try
                {
                    //Wait for the handler thread to end
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

    /**
     * Handling for a signing in request
     * @param msg the signing in message
     */
    private void signIn(Packet msg)
    {
        try
        {
            //Get account information from the message's payload
            Account account = (Account)msg.getPayloadAsObject();
            Packet response;
            //Determine the account is currently used
            if (accountToSessionID.containsKey(account.username()))
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, false, ACCOUNT_BEING_USED);
            //Determine the username and the password are matched in the database
            else if (!accountManager.checkAccount(account))
                 response = new Packet(msg.sessionID(), CommandType.RESPONSE, false, ACCOUNT_NOT_MATCHED);
            //Pass all -> the information is fine
            else
            {
                accountToSessionID.put(account.username(), msg.sessionID());
                //The response message has current online users (to first show in the chat panel of the client)
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, getOnlineUsers(account.username()));
                //Notify to the other clients that a new user has signed in
                sendAll(new Packet(account.username(), CommandType.NOTIFY_IN, accountManager.getAccounts().get(account.username()).displayedName()));
                //Add new online user
                gui.addLog(account.username() + " has just signed in");
                gui.addOnlineUser(account.username());
            }
            //Response the action status to the client
            sendMsg(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get current online users in the system
     * @param except the excluded user (will not be taken)
     * @return the online users
     */
    private ArrayList<AccountShowInformation> getOnlineUsers(String except)
    {
        ArrayList<AccountShowInformation> users = new ArrayList<>();
        //For each user, if the user is not the excepted one -> get its information
        for (String acc : accountToSessionID.keySet())
            if (!acc.equals(except))
                users.add(new AccountShowInformation(acc, accountManager.getAccounts().get(acc).displayedName()));
        return users;
    }

    /**
     * Handling for a signing up request
     * @param msg the signing up request
     */
    private void signUp(Packet msg)
    {
        try
        {
            //Get sign up information from the message's payload
            AccountFullInformation account = (AccountFullInformation)msg.getPayloadAsObject();
            Packet response;
            //Try to add the account to the database
            if (accountManager.addAccount(account))
            {
                //If the user is added successfully
                //Save current database to disk synchronously
                accountManager.saveData(DATA_PATH);
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, true);
                gui.addLog(account.account() + " has just been signed up");
            }
            else
                //Response failure message to the user if the username has existed
                response = new Packet(msg.sessionID(), CommandType.RESPONSE, false);
            //Response the action status back to the client
            sendMsg(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Transfer a message from a client (sender) to another client (receiver)
     * @param msg the message
     */
    private void transferMessage(Packet msg)
    {
        try
        {
            //Get session id of the receiver
            UUID receiverID = accountToSessionID.get(msg.receiver());
            if (receiverID != null)
            {
                //Get the handler of the receiver and send the message
                ServerSideHandler handler = handlers.get(receiverID);
                handler.sendMsg(msg);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handling for a signing out request
     * @param msg the signing out request
     */
    private void signOut(Packet msg)
    {
        try
        {
            accountToSessionID.remove(msg.sender());
            //Notify the other clients that a user has just signed out
            sendAll(new Packet(msg.sender(), CommandType.NOTIFY_OUT));
            gui.deleteUser(msg.sender());
            gui.addLog(msg.sender() + " has just signed out");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send a message based on the content
     * @param msg the message is sent
     */
    private void sendMsg(Packet msg)
    {
        //Get the handler by the message's session id
        ServerSideHandler handler = handlers.get(msg.sessionID());
        if (handler != null)
            handler.sendMsg(msg);
    }

    /**
     * Send a message to all current online users
     * @param msg the message is sent
     */
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

    /**
     * Close the server socket accepting connections
     */
    private void close()
    {
        try
        {
            if (socket != null && !socket.isClosed())
            {
                //Notify online users that the server is off
                sendAll(new Packet(CommandType.DISCONNECT));
                for (ServerSideHandler handler : handlers.values())
                {
                    try
                    {
                        handler.close();
                        //Wait for the handler thread ends
                        handler.join();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                socket.close();
            }
            handlers.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void closeProgram()
    {
        close();
    }

    /**
     * Create a new server manager instance
     * @param args terminal arguments (not used)
     */
    public static void main(String[] args)
    {
        new ServerManager();
    }
}
