package clientSide.messages;

import sockets.protocols.packet.FileTransfer;

import java.util.ArrayList;

import static utilities.StringHandler.addDateTime;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:02 PM
 * Description: Representing a file message
 * @param user the sender of the message
 * @param files the files
 */
public record FileMessage (String user, ArrayList<FileTransfer> files) {

    /**
     * Get the string of datetime and the user
     * @return the user sending the message with datetime (example: linh (11:15:29))
     */
    @Override
    public String toString()
    {
        return addDateTime(user);
    }
}
