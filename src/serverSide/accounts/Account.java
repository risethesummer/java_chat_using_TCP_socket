package serverSide.accounts;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Server.login
 * Created by NhatLinh - 19127652
 * Date 12/20/2021 - 11:43 PM
 * Description: ...
 */
public record Account (String account, byte[] password) implements Serializable {

    public Account(String account, String password)
    {
        this(account, hashPassword(password));
    }

    private static byte[] hashPassword(String password)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(password.getBytes(StandardCharsets.US_ASCII));
        }
        catch (Exception e)
        {
            return new byte[16];
        }
    }
}
