package sockets.handlers.client;

import sockets.handlers.CommunicateSocket;
import sockets.protocols.packet.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/**
 * Client
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:00 PM
 * Description: ...
 */
public class ClientSideHandler extends CommunicateSocket {

    private Runnable onClose;


    public ClientSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Runnable onClose) {
        super(socket, onReceivedMessage);
        this.onClose = onClose;
        doFirstTouch();
    }

    @Override
    public boolean doFirstTouch()
    {
        Packet msg = receiveMessage();
        if (msg == null)
            return false;
        sessionID = msg.sessionID();
        return true;
    }

    @Override
    public void run()
    {
        super.run();
        if (onClose != null)
            //Try to trigger closing event
            onClose.run();
    }

    public void sendAsync(Packet packet, DoubleConsumer showProgress)
    {
        new AsynchronousSender(socket, packet, showProgress);
    }

    @Override
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
