package sockets.handlers.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * sockets.handlers.server
 * Created by NhatLinh - 19127652
 * Date 1/10/2022 - 7:59 AM
 * Description: ...
 */
public class AsynchronousOpenSocket extends Thread {

    private final Consumer<Socket> onConnect;
    private final ServerSocket serverSock;

    public AsynchronousOpenSocket(ServerSocket serverSock, Consumer<Socket> onConnect)
    {
        this.onConnect = onConnect;
        this.serverSock = serverSock;
        start();
    }

    @Override
    public void run()
    {
        while (!serverSock.isClosed())
        {
            try
            {
                Socket ss = serverSock.accept();
                onConnect.accept(ss);
            }
            catch (Exception e)
            {
            }
        }
    }
}
