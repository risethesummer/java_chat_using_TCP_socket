package sockets.protocols.accounts;

import java.io.Serializable;

/**
 * serverSide.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 10:36 PM
 * Description: The class containing displayed information of a user
 * @param username the username
 * @param displayedName the displayed name showed in the chat
 */
public record AccountShowInformation(String username, String displayedName) implements Serializable {
}
