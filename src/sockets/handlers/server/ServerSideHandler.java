package sockets.handlers.server;

import sockets.handlers.CommunicateSocket;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * sockets
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:13 PM
 * Description: ...
 */
public class ServerSideHandler extends CommunicateSocket {

    private Consumer<UUID> onClose;

    public ServerSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Consumer<UUID> onClose) {
        super(socket, onReceivedMessage);
        this.onClose = onClose;
        sessionID = UUID.randomUUID();
        doFirstTouch();
    }

    @Override
    public boolean doFirstTouch() {
        return sendMsg(new Packet(sessionID, CommandType.FIRST_TOUCH));
    }

    @Override
    public void run()
    {
        super.run();
        if (onClose != null)
            onClose.accept(this.sessionID);
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
