package sockets.handlers.server;

import sockets.handlers.IntermediateSocket;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * sockets
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:13 PM
 * Description: The intermediate socket class in the server side
 */
public class ServerSideHandler extends IntermediateSocket {

    private Consumer<UUID> onClose;

    /**
     * Create a new socket
     * @param socket the base socket used for sending and receiving data
     * @param onReceivedMessage the receiving callback will be called when the socket receives any message
     * @param onClose the closing callback will be called when the socket is closed
     */
    public ServerSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Consumer<UUID> onClose) {
        super(socket, onReceivedMessage);
        this.onClose = onClose;
        //Create random session for the client
        sessionID = UUID.randomUUID();
        //Do first touch to send the session id to the client
        doFirstTouch();
    }

    /**
     * Do first touch with the client to send the session id
     */
    @Override
    public void doFirstTouch() {
        try
        {
            //Send a packet containing the session id
            sendMsg(new Packet(sessionID, CommandType.FIRST_TOUCH));
        }
        catch (SocketException e)
        {
            if (onClose != null)
                onClose.accept(this.sessionID);
        }
    }

    /**
     * The main task is to continue receiving data while the socket is not closed
     */
    @Override
    public void run()
    {
        super.run();
        //Call the closing callback when the thread stops (the socket is closed)
        if (onClose != null)
            onClose.accept(this.sessionID);
    }

    /**
     * Close the socket
     */
    @Override
    public void close()
    {
        try
        {
            //If actively close the socket -> no need to call the callback
            onClose = null;
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
