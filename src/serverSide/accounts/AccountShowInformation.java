package serverSide.accounts;

import java.io.Serializable;

/**
 * serverSide.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 10:36 PM
 * Description: ...
 */
public record AccountShowInformation(String account, String displayedName) implements Serializable {
}
