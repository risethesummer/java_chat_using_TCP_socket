package sockets.handlers.client;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

import static serverSide.ServerManager.SERVER_IP;
import static serverSide.ServerManager.SERVER_PORT;

/**
 * sockets.handlers
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 9:04 PM
 * Description: ...
 */
public class ClientConnectAsynchronous extends Thread{

    private final Consumer<Socket> onSuccess;

    public ClientConnectAsynchronous(Consumer<Socket> onSuccess)
    {
        this.onSuccess = onSuccess;
    }

    @Override
    public void run()
    {
        boolean success = false;
        while (!success)
        {
            try
            {
                Socket socket = new Socket(SERVER_IP,SERVER_PORT);
                onSuccess.accept(socket);
                success = true;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
