package clientSide.GUI.utilities;

import sockets.protocols.packet.FileTransfer;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 12:02 PM
 * Description: ...
 */
public record FileMessage (String user, FileTransfer file) {
}
