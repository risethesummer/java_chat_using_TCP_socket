package sockets.handlers;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:19 PM
 * Description: An utility socket containing useful methods for transferring data (send and receive)
 */
public abstract class IntermediateSocket extends Thread {

    /**
     * The base socket used for sending and receiving data
     */
    protected final Socket socket;

    /**
     * The callback will be called when the socket receives any message
     */
    protected final Consumer<Packet> receivedMessageCallback;
    /**
     * The session id of the socket (for the server recognises which user the packet belongs to)
     */
    protected UUID sessionID;
    /**
     * Mark the socket should stop receiving data (can be back when mark the flag is true)
     */
    protected boolean isShouldStop = false;

    /**
     * Set stop receiving data flag
     * @param shouldStop the stop value (true: stop, false: continue receiving data)
     */
    public void setShouldStop(boolean shouldStop) {
        isShouldStop = shouldStop;
    }

    /**
     * Get the session id
     * @return the session id
     */
    public UUID getSessionID() {
        return sessionID;
    }

    /**
     * Define the socket is connecting or not
     * @return the connected status
     */
    public boolean isClosed()
    {
        return !socket.isConnected();
    }

    /**
     * Create a new object with the base socket and the receiving callback
     * @param socket the base socket used for sending and receiving data
     * @param onReceivedMessage the receiving callback will be called when the socket receives any message
     */
    public IntermediateSocket(Socket socket, Consumer<Packet> onReceivedMessage)
    {
        this.socket = socket;
        try
        {
            //Set timeout to avoid program crash (infinite loop)
            socket.setSoTimeout(1000);
        }
        catch (Exception e) {}
        this.receivedMessageCallback = onReceivedMessage;
    }

    /**
     * Method for getting session id (first touch between client and server)
     */
    public abstract void doFirstTouch();

    /**
     * The main task is to continue receiving data while the socket is not closed
     */
    @Override
    public void run()
    {
        do {
            try {
                //Continue receiving messages and calling the callback till the socket is closed
                receivedMessageCallback.accept(receiveMessage());

                //If stop flag is marked
                if (isShouldStop)
                {
                    synchronized (this)
                    {
                        //Make the thread sleep until it's notified
                        this.wait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!socket.isClosed());
    }

    /**
     * Try to receive message
     * @return the message result (null if it gets timeout exception)
     */
    public Packet receiveMessage()
    {
        try {
            ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
            Packet msg;
            try
            {
                msg = (Packet)stream.readObject();
            }
            catch (Exception readException)
            {
                msg = null;
            }
            return msg;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Try to receive message matched the expected type command
     * @param expected the expected type of the message
     * @return the message result (null if it gets timeout exception)
     */
    public Packet receiveMessage(CommandType expected) throws SocketException
    {
        int maxTry = 30;
        Packet response;
        //Try to get a message having the type (loop until get it)
        for (int time = 0; time < maxTry; time++)
        {
            try
            {
                response = receiveMessage();
                //If it gets the appropriate message
                if (response.cmd().equals(expected))
                    return response;
            }
            catch (Exception e){}
        }

        throw new SocketException("Exceed try times");
    }

    /**
     * Send a message through the base socket
     * @param msg the message is sent
     */
    public void sendMsg(Packet msg) throws SocketException
    {
        if (!socket.isClosed())
        {
            try {
                //Try to open an object stream through the stream of the base socket
                ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                //Write data to the object stream
                stream.writeObject(msg);
                stream.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new SocketException("Can not send the message");
            }
        }
    }


    /**
     * Send a message and wait for its exact type response (because we can receive a message not matched the type we want)
     * @param msg the message is sent
     * @param expected the expected command type of the response
     * @return the exact response for the message
     */
    public Packet sendAndWaitForResponse(Packet msg, CommandType expected) throws SocketException
    {
        //Mark sure can send data through the socket
        if (!socket.isClosed() && socket.isConnected())
        {
            //Send message
            sendMsg(msg);
            //Get response matched the expected type
            return receiveMessage(expected);
        }
        return null;
    }

    /**
     * Close the socket
     */
    public abstract void close();
}
