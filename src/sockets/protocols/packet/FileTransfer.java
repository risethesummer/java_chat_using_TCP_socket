package sockets.protocols.packet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

/**
 * sockets.protocols
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:03 PM
 * Description: Represent a files payload
 * @param name the name of the file
 * @param content the content of the file in form of byte array
 */
public record FileTransfer (String name, byte[] content) implements Serializable {


    /**
     * Create a file transfer object by an absolute path
     * @param path the path of the files (on disk)
     * @return the file transfer object containing information of the files in the path
     * @throws FileNotFoundException throw if there is no file in the path
     */
    public static FileTransfer getFile(String path) throws FileNotFoundException
    {
        try
        {
            String name = new File(path).getName();
            FileInputStream stream = new FileInputStream(path);
            //Read all bytes of the files stream
            byte[] content = stream.readAllBytes();
            stream.close();
            return new FileTransfer(name, content);
        }
        catch (Exception e)
        {
            throw new FileNotFoundException("Can not find files");
        }
    }
}
