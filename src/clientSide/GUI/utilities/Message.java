package clientSide.GUI.utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 11:07 PM
 * Description: ...
 */
public record Message(String user, String content) {

    @Override
    public String toString()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return user + " (" + LocalDateTime.now().format(formatter) + ")";
    }
}
