package serverSide.accounts;

import java.io.Serializable;

/**
 * serverSide.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:49 PM
 * Description: ...
 */
public record AccountFullInformation(Account account, String displayedName) implements Serializable {
}
