package sockets.handlers.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * sockets.handlers.server
 * Created by NhatLinh - 19127652
 * Date 1/10/2022 - 7:59 AM
 * Description: The thread for accepting connections asynchronously
 */
public class AcceptConnectionAsync extends Thread {

    /**
     * Callback when accepting a new connection
     */
    private final Consumer<Socket> onConnect;
    /**
     * The socket accepting connection
     */
    private final ServerSocket serverSock;

    /**
     * Create a new thread
     * @param serverSock the socket accepting connection
     * @param onConnect the callback when accepting a new connection
     */
    public AcceptConnectionAsync(ServerSocket serverSock, Consumer<Socket> onConnect)
    {
        this.onConnect = onConnect;
        this.serverSock = serverSock;
        start();
    }

    /**
     * Continue accepting connections until the socket is closed
     */
    @Override
    public void run()
    {
        while (!serverSock.isClosed())
        {
            try
            {
                //Call the callback whenever accepting a new connection
                Socket ss = serverSock.accept();
                onConnect.accept(ss);
            }
            catch (Exception e)
            {
            }
        }
    }
}
