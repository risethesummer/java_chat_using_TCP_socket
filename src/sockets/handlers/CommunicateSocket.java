package sockets.handlers;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;
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
    protected boolean isShouldStop = false;

    public void setShouldStop(boolean shouldStop) {
        isShouldStop = shouldStop;
    }
    public UUID getSessionID() {
        return sessionID;
    }
    public boolean isClosed()
    {
        return !socket.isConnected();
    }


    public CommunicateSocket(Socket socket, Consumer<Packet> onReceivedMessage)
    {
        this.socket = socket;
        this.receivedMessageCallback = onReceivedMessage;
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
        Packet response = new Packet(null, CommandType.NOTHING, null);
        do
        {
            try
            {
                response = receiveMessage();
            }
            catch (Exception e){}
        }
        while (!response.cmd().equals(expected));
        return response;
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
                OutputStream getSt = socket.getOutputStream();
                ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                stream.writeObject(msg);
                stream.flush();
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

    public abstract void close();

}
