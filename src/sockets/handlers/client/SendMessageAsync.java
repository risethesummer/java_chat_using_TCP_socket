package sockets.handlers.client;
import sockets.protocols.packet.Packet;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/**
 * sockets.handlers
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:39 PM
 * Description: The thread for sending a message asynchronously
 */
public class SendMessageAsync extends Thread {

    /**
     * The socket for sending data
     */
    private final Socket socket;
    /**
     * The packet is sent
     */
    private final Packet packet;
    /**
     * The callback to get current progress
     */
    private final IntConsumer showProgress;

    /**
     *
     * @param socket the socket used for sending data
     * @param packet the message is sent
     * @param showProgress the callback to get current progress
     */
    public SendMessageAsync(Socket socket, Packet packet, IntConsumer showProgress)
    {
        this.socket = socket;
        this.packet = packet;
        this.showProgress = showProgress;
    }

    /**
     * Run until send the packet successfully
     */
    @Override
    public void run()
    {
        try
        {
            if (!socket.isClosed())
            {
                try {
                    //Create a new array stream to convert to sent packet to a byte array
                    ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
                    ObjectOutputStream stream = new ObjectOutputStream(arrayStream);
                    stream.writeObject(packet);
                    stream.flush();
                    stream.close();

                    byte[] sentContent = arrayStream.toByteArray();
                    BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());
                    for (int send = 0; send < sentContent.length; send++)
                    {
                        //Send every byte of the packet
                        outStream.write(sentContent[send]);
                        //Callback to give curren progress
                        showProgress.accept(send * 100 / sentContent.length);
                    }

                    //Because we can only 99 (divide integers)
                    //So we need to fill 100 in person
                    showProgress.accept(100);
                    outStream.flush();
                }
                catch (Exception e)
                {
                    showProgress.accept(0);
                }
            }
        }
        catch (Exception e)
        {
            showProgress.accept(0);
        }
    }
}
