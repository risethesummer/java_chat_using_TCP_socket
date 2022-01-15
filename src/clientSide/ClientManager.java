package clientSide;
import clientSide.GUI.ClientGUI;
import clientSide.messages.FileMessage;
import clientSide.messages.Message;
import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;
import sockets.protocols.accounts.AccountShowInformation;
import sockets.handlers.client.ClientConnectAsynchronous;
import sockets.handlers.client.ClientSideHandler;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.FileTransfer;
import sockets.protocols.packet.Packet;

import java.net.Socket;
import java.util.ArrayList;
import java.util.function.IntConsumer;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:55 PM
 * Description: The class managing the client activities
 */
public class ClientManager {
    private ClientConnectAsynchronous connectThread;
    private ClientSideHandler handler = null;
    private String userName = null;
    /**
     * GUI of the client
     */
    private final ClientGUI gui;

    /**
     * Create a new client manager
     * @param args terminal arguments (not used)
     */
    public static void main(String[] args)
    {
        new ClientManager();
    }

    private ClientManager()
    {
        //Try to connect to the server asynchronously after create the object
        connectThread = new ClientConnectAsynchronous(this::reconnect);
        //Create the GUI
        gui = new ClientGUI(this::sendMessage, this::sendFile, this::signIn, this::signUp, this::close, this::signOut, () -> {
            //If it currently has a connecting thread -> no need to run a new one
            if (!connectThread.isAlive())
                connectThread = new ClientConnectAsynchronous(this::reconnect);
        });
    }

    /**
     * Callback when the client socket is created
     * @param s the client socket
     */
    private void reconnect(Socket s)
    {
        //Create a new handler
        handler = new ClientSideHandler(s, this::handlingMessage, this::handlingDisconnect);
        //Just receiving messages after signing in (stop it when just being created)
        handler.setShouldStop(true);
        handler.start();
        requestReconnect();
    }

    /**
     * Request to recover the connection to the server
     */
    private void requestReconnect()
    {
        //The username is not null -> has signed in before
        if (userName != null)
        {
            Packet response = handler.sendAndWaitForResponse(new Packet(handler.getSessionID(), userName, CommandType.RECONNECT), CommandType.RESPONSE);
            if (response.state())
            {
                //If the user has signed in before -> recover information (show chat frame)
                gui.getChatFrame().reloadUsers((ArrayList<AccountShowInformation>)response.getPayloadAsObject());
                gui.showChatFrame();
                //Start to receiving messages because the user has signed in
                handler.setShouldStop(false);
                return;
            }
        }
        //If the user is new to the program -> show login frame
        gui.showLoginFrame();
    }

    /**
     * Handling when losing the connection from the server
     */
    private void handlingDisconnect()
    {
        //Destroy the current handler to create a new one
        if (handler != null && handler.isAlive())
        {
            try
            {
                handler.close();
                synchronized (handler)
                {
                    //Notify to destroy the handler (it's being stopped)
                    handler.notify();
                }
                handler.join();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        handler = null;
        //Show reconnect frame for the user to reconnect
        gui.showConnectFrame();
    }

    /**
     * Check the status of the connection to the server
     * @return the check result (true: loss connection, false: fine)
     */
    private boolean checkFailConnection()
    {
        if (handler == null || handler.isClosed())
        {
            //If the client has lost the connection -> try to connect to the server again
            handlingDisconnect();
            return true;
        }
        return false;
    }

    /**
     * Request to sign in
     * @param account the account used to sign in
     * @return the response message from the server
     */
    private Packet signIn(Account account)
    {
        try
        {
            if (checkFailConnection()){
                return null;
            }

            //Send signing in request and wait for the response from the server
            Packet response = handler.sendAndWaitForResponse(new Packet(handler.getSessionID(), CommandType.SIGN_IN, account), CommandType.RESPONSE);
            if (response.state())
            {
                this.userName = account.username();
                //Show current online users
                gui.getChatFrame().reloadUsers((ArrayList<AccountShowInformation>)response.getPayloadAsObject());
                gui.showChatFrame();
                //Start to receiving messages (because we don't need to receive message when signing in or reconnecting)
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

    /**
     * Request to send files
     * @param msg the files message
     * @param showProgress the callback to show current progress
     */
    private void sendFile(FileMessage msg, IntConsumer showProgress)
    {
        try
        {
            if (checkFailConnection()){
                return;
            }
            //Send the files asynchronously because it's size can be large (avoid waiting too long)
            handler.sendAsync(new Packet(handler.getSessionID(), userName, msg.user(), CommandType.SEND_FILE, msg.files()), showProgress);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handling for receiving files from the server
     * @param msg the files packet
     */
    private void receiveFile(Packet msg)
    {
        try
        {
            //Add the files to the GUI
            gui.getChatFrame().addNewFile(new FileMessage(msg.sender(), (ArrayList<FileTransfer>)msg.getPayloadAsObject()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handling for receiving text message from the server
     * @param msg the message content
     */
    private void receiveMessage(Packet msg)
    {
        try
        {
            //Add the chat log to the GUI
            gui.getChatFrame().addNewChatLog(new Message(msg.sender(), new String(msg.payload())));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Request to send a text message to another client
     * @param msg the message content
     */
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

    /**
     * Request to sign up a new account
     * @param account the signing up account
     * @return the action status (true: success, false: failure)
     */
    private boolean signUp(AccountFullInformation account)
    {
        try
        {
            if (checkFailConnection()){
                return false;
            }
            //Wait for the response
            Packet response = handler.sendAndWaitForResponse(new Packet(handler.getSessionID(), CommandType.SIGN_UP, account));
            return response.state();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Request to sign out from the program
     */
    private void signOut()
    {
        try
        {
            if (checkFailConnection()){
                return;
            }
            //Notify to the server that the user has logged out
            handler.sendMsg(new Packet(handler.getSessionID(), this.userName, CommandType.SIGN_OUT));
            //Mark as not login
            userName = null;
            //Make the socket stop receiving messages
            handler.setShouldStop(true);
            gui.showLoginFrame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handling messages from the server
     * @param msg the message
     */
    private void handlingMessage(Packet msg)
    {
        if (msg != null)
        {
            switch (msg.cmd()) {
                case SEND_MSG -> receiveMessage(msg);
                case SEND_FILE -> receiveFile(msg);
                case NOTIFY_IN -> {
                    //When someone signs in -> create a new chat section for him
                    AccountShowInformation user = new AccountShowInformation(msg.sender(), new String(msg.payload()));
                    //Ignore if the message from the client itself
                    if (user.username().equals(userName))
                        return;
                    gui.getChatFrame().onlineUser(user);
                }
                case NOTIFY_OUT -> {
                    //When someone signs out -> mark him as offline
                    String user = msg.sender();
                    if (user.equals(userName))
                        return;
                    gui.getChatFrame().offlineUser(user);
                }
                case DISCONNECT -> handlingDisconnect();
            }
        }
    }

    /**
     * Close the program
     */
    public void close()
    {
        try
        {
            //Wait for the connecting thread to end
            if (connectThread != null && connectThread.isAlive())
            {
                connectThread.setShouldStop(true);
                connectThread.join();
            }
            //Wait for the handler thread to end
            if (handler != null && handler.isAlive())
            {
                handler.close();
                synchronized (handler)
                {
                    handler.notify();
                }
                handler.join();
            }
            //Dispose the GUI
            gui.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
