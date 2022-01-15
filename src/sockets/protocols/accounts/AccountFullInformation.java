package sockets.protocols.accounts;

import java.io.Serializable;

/**
 * serverSide.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:49 PM
 * Description: The class containing a user username and the displayed name (used for signing up)
 */

/**
 * @param account the username information (username, password)
 * @param displayedName the displayed name of the username (showed in the chat)
 */
public record AccountFullInformation(Account account, String displayedName) implements Serializable {
}
