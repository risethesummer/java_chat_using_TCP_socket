package sockets.protocols;

import java.io.Serializable;

/**
 * sockets.protocols
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:03 PM
 * Description: ...
 */
public record FileTransfer (String name, byte[] content) implements Serializable {
}
