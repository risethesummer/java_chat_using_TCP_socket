package sockets.handlers.server;

import sockets.handlers.CommunicateSocket;
import sockets.protocols.CommandType;
import sockets.protocols.Packet;

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

    public ServerSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Runnable onClose) {
        super(socket, onReceivedMessage, onClose);
        sessionID = UUID.randomUUID();
    }

    @Override
    public boolean doFirstTouch() {
        return sendMsg(new Packet(sessionID, CommandType.FIRST_TOUCH));
    }
}
