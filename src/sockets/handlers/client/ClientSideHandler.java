package sockets.handlers.client;

import sockets.handlers.CommunicateSocket;
import sockets.protocols.Packet;

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

    public ClientSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Runnable onClose) {
        super(socket, onReceivedMessage, onClose);
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

    public void sendAsync(Packet packet, DoubleConsumer showProgress)
    {
        new AsynchronousSender(socket, packet, showProgress);
    }
}
