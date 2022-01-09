package sockets.handlers;
import sockets.protocols.CommandType;
import sockets.protocols.Packet;
import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 12/16/2021 - 2:19 PM
 * Description: ...
 */
public abstract class CommunicateSocket extends Thread {

    protected final Socket socket;
    protected final Consumer<Packet> receivedMessageCallback;
    protected UUID sessionID;
    private Runnable onClose;

    public void setShouldStop(boolean shouldStop) {
        isShouldStop = shouldStop;
    }

    private boolean isShouldStop = false;

    public UUID getSessionID() {
        return sessionID;
    }

    public CommunicateSocket(Socket socket, Consumer<Packet> onReceivedMessage, Runnable onClose)
    {
        this.socket = socket;
        this.receivedMessageCallback = onReceivedMessage;
        this.onClose = onClose;
        doFirstTouch();
    }

    public abstract boolean doFirstTouch();

    @Override
    public void run()
    {
        do {
            try {
                //Continue receiving messages till the socket is closed
                if (isShouldStop)
                {
                    synchronized (this)
                    {
                        this.wait();
                    }
                }
                receivedMessageCallback.accept(receiveMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!socket.isClosed());

        if (onClose != null)
            //Try to trigger closing event
            onClose.run();
    }

    public Packet sendAndWaitForResponse(Packet msg)
    {
        try
        {
            sendMsg(msg);
            return receiveMessage();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Packet sendAndWaitForResponse(Packet msg, CommandType expected)
    {
        try
        {
            sendMsg(msg);
            return receiveMessage(expected);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Packet receiveMessage(CommandType expected)
    {
        Packet reponse = new Packet(null, CommandType.NOTHING, null);
        do
        {
            try
            {
                reponse = receiveMessage();
            }
            catch (Exception e){}
        }
        while (!reponse.cmd().equals(expected));
        return reponse;
    }


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
            e.printStackTrace();
            return null;
        }
    }

    public boolean sendMsg(Packet msg)
    {
        if (!socket.isClosed())
        {
            try {
                ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                stream.writeObject(msg);
                stream.flush();
                stream.close();
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void close()
    {
        try
        {
            onClose = null;
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
