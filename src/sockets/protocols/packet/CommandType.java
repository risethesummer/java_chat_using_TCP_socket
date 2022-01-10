package sockets.protocols.packet;

import java.io.Serializable;

/**
 * sockets.protocol
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:25 PM
 * Description: ...
 */
public enum CommandType implements Serializable {
    SIGN_UP, SIGN_IN, RESPONSE, SIGN_OUT, SEND_MSG, SEND_FILE, FIRST_TOUCH, NOTIFY_IN, NOTIFY_OUT, NOTHING, DISCONNECT, RECONNECT
}
