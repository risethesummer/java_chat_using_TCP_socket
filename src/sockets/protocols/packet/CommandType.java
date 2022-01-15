package sockets.protocols.packet;

import java.io.Serializable;

/**
 * sockets.protocol
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:25 PM
 * Description: Describe which type of information the packet contains
 */
public enum CommandType implements Serializable {
    /**
     * For sign up request
     */
    SIGN_UP,
    /**
     * For sign in request
     */
    SIGN_IN,
    /**
     * For response packet from the server to clients
     */
    RESPONSE,
    /**
     * For sign out notification
     */
    SIGN_OUT,
    /**
     * For sending text message
     */
    SEND_MSG,
    /**
     * For sending files message
     */
    SEND_FILE,
    /**
     * For the first touch action between the server and clients
     */
    FIRST_TOUCH,
    /**
     * For the server to notify to clients that someone has signed in
     */
    NOTIFY_IN,
    /**
     * For the server to notify to clients that someone has signed out
     */
    NOTIFY_OUT,
    /**
     * For the server to notify it will turn off the connection
     */
    DISCONNECT,
    /**
     * For reconnect request from clients
     */
    RECONNECT
}
