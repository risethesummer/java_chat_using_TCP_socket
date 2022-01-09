package serverSide.accounts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Server.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:09 PM
 * Description: ...
 */
public class AccountManager {

    public Hashtable<String, AccountFullInformation> getAccounts() {
        return accounts;
    }

    private Hashtable<String, AccountFullInformation> accounts;

    public AccountManager(String dataPath)
    {
        loadData(dataPath);
    }

    public boolean checkAccount(Account account)
    {
        byte[] password = accounts.get(account.account()).account().password();
        //Check matched password
        if (password != null)
            return Arrays.equals(password, account.password());
        return false;
    }

    public boolean addAccount(AccountFullInformation account)
    {
        //If the account exists in the system
        if (accounts.contains(account.account()))
            return false;
        accounts.put(account.account().account(), new AccountFullInformation(account.account(), account.displayedName()));
        return true;
    }

    public void loadData(String path)
    {
        try
        {
            ObjectInputStream dataStream = new ObjectInputStream(new FileInputStream(path));
            accounts = (Hashtable<String, AccountFullInformation>)dataStream.readObject();
            dataStream.close();
        }
        catch (Exception e)
        {
            //If catch an exception when loading data file
            //Use a new empty data
            accounts = new Hashtable<>();
        }
    }

    public void saveData(String path)
    {
        try
        {
            ObjectOutputStream dataStream = new ObjectOutputStream(new FileOutputStream(path));
            dataStream.writeObject(accounts);
            dataStream.flush();
            dataStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
