package sockets.protocols.packet;

/**
 * sockets.protocols
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 3:54 PM
 * Description: Store some error responses in a packet
 */
public interface ErrorContent {

    /**
     * Account is not in the system error
     */
    String ACCOUNT_NOT_MATCHED = "Account and password are not matched!";
    /**
     * Account is being used error
     */
    String ACCOUNT_BEING_USED = "Account is being used by another device!";
}
