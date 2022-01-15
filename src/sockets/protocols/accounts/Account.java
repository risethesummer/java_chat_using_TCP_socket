package sockets.protocols.accounts;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Server.login
 * Created by NhatLinh - 19127652
 * Date 12/20/2021 - 11:43 PM
 * Description: The class containing username and password of a user (used for signing in)
 */

/**
 * @param username the username
 * @param password the hashed password
 */
public record Account (String username, byte[] password) implements Serializable {

    /**
     * Create a new username object with string password
     * @param username the username
     * @param password the string password
     */
    public Account(String username, String password)
    {
        this(username, hashPassword(password));
    }

    private static byte[] hashPassword(String password)
    {
        try
        {
            //Use MD5 algorithm to hash password
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(password.getBytes(StandardCharsets.US_ASCII));
        }
        catch (Exception e)
        {
            return new byte[16];
        }
    }
}
