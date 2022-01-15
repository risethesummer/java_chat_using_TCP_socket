package sockets.handlers.client;

import sockets.handlers.IntermediateSocket;
import sockets.protocols.packet.CommandType;
import sockets.protocols.packet.Packet;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Client
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:00 PM
 * Description: The intermediate socket class in the client side
 */
public class ClientSideHandler extends IntermediateSocket {

    private Runnable onClose;

    /**
     * Create a new socket
     * @param socket the base socket used for sending and receiving data
     * @param onReceivedMessage the receiving callback will be called when the socket receives any message
     * @param onClose the closing callback will be called when the socket is closed
     */
    public ClientSideHandler(Socket socket, Consumer<Packet> onReceivedMessage, Runnable onClose) {
        super(socket, onReceivedMessage);
        this.onClose = onClose;
        doFirstTouch();
    }

    /**
     * Do first touch with the server to receive the session id
     */
    @Override
    public void doFirstTouch()
    {
        //Try to receive a first touch packet to get the session id
        Packet msg = receiveMessage(CommandType.FIRST_TOUCH);
        if (msg == null)
            return;
        sessionID = msg.sessionID();
    }

    /**
     * The main task is to continue receiving data while the socket is not closed
     */
    @Override
    public void run()
    {
        super.run();
        if (onClose != null)
            //Try to trigger closing event
            onClose.run();
    }

    /**
     * Send a message asynchronously
     * @param packet the message is sent
     * @param showProgress the callback to get current progress
     */
    public void sendAsync(Packet packet, IntConsumer showProgress)
    {
        //Create a new thread to send message asynchronously
        new SendMessageAsync(socket, packet, showProgress).start();
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
            //To avoid deadlock if the flag is true before
            setShouldStop(false);
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
