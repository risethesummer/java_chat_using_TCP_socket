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
 * Description: The thread for connecting to the server asynchronously
 */
public class ClientConnectAsynchronous extends Thread{

    /**
     * Callback when connecting to the server successfully
     */
    private final Consumer<Socket> onSuccess;

    /**
     * Mark the thread should terminate
     */
    private boolean shouldStop = false;

    /**
     * Create a new thread
     * @param onSuccess the callback called when connecting to the server successfully
     */
    public ClientConnectAsynchronous(Consumer<Socket> onSuccess)
    {
        this.onSuccess = onSuccess;
        start();
    }

    /**
     * Try to connect to the server until the stop flag is true
     */
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
                //Connect to the server
                Socket socket = new Socket(SERVER_IP,SERVER_PORT);
                onSuccess.accept(socket);
                success = true;
            }
            catch(IOException e)
            {
                try {
                    //Connect again after 1 second to make the thread not run too fast
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Set the stop flag
     * @param shouldStop the set value
     */
    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }
}
