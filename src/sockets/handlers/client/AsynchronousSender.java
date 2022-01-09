package sockets.handlers.client;
import sockets.protocols.Packet;

import java.io.*;
import java.net.Socket;
import java.util.function.DoubleConsumer;

/**
 * sockets.handlers
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:39 PM
 * Description: ...
 */
public class AsynchronousSender extends Thread {

    private final Socket socket;
    private final Packet packet;
    private final DoubleConsumer showProcess;

    public AsynchronousSender(Socket socket, Packet packet, DoubleConsumer showProcess)
    {
        this.socket = socket;
        this.packet = packet;
        this.showProcess = showProcess;
    }

    @Override
    public void run()
    {
        try
        {
            if (!socket.isClosed())
            {
                try {
                    ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
                    ObjectOutputStream stream = new ObjectOutputStream(arrayStream);
                    stream.writeObject(packet);
                    stream.flush();
                    stream.close();

                    byte[] sentContent = arrayStream.toByteArray();

                    BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());
                    for (int send = 0; send < sentContent.length; send++)
                    {
                        outStream.write(sentContent[send]);
                        showProcess.accept((double)send / sentContent.length);
                    }
                    outStream.flush();
                    outStream.close();
                }
                catch (Exception e)
                {
                    showProcess.accept(0);
                }
            }
        }
        catch (Exception e)
        {
            showProcess.accept(0);
        }
    }
}
