package serverSide.accounts;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * serverSide.accounts
 * Created by NhatLinh - 19127652
 * Date 1/12/2022 - 3:54 PM
 * Description: The thread for saving data asynchronously
 */
public class SaveFileAsync extends Thread {

    /**
     * The path to save
     */
    private final String path;

    /**
     * The data to be saved
     */
    private final Serializable data;

    /**
     * Create a new save files thread
     * @param data the data to be saved
     * @param path the path to save the files
     */
    public SaveFileAsync(Serializable data, String path)
    {
        this.data = data;
        this.path = path;
    }

    /**
     * Try to save the files (terminate when finish saving)
     */
    @Override
    public void run()
    {
        try
        {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(path));
            stream.writeObject(data);
            stream.flush();
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
