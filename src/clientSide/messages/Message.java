package clientSide.messages;

import static utilities.StringHandler.addDateTime;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 11:07 PM
 * Description: Representing a text message
 * @param user the sender of the message
 * @param content the content of the message
 */
public record Message(String user, String content) {

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
