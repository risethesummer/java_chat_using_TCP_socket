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
    private boolean shouldStop = false;

    public ClientConnectAsynchronous(Consumer<Socket> onSuccess)
    {
        this.onSuccess = onSuccess;
        start();
    }

    @Override
    public void run()
    {
        boolean success = false;
        while (!success)
        {
            try
            {
                if (shouldStop)
                    return;
                Socket socket = new Socket(SERVER_IP,SERVER_PORT);
                onSuccess.accept(socket);
                success = true;
            }
            catch(IOException e)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }
}
