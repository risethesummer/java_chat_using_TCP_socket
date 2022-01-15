package serverSide.accounts;

import sockets.protocols.accounts.Account;
import sockets.protocols.accounts.AccountFullInformation;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Server.login
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:09 PM
 * Description: Store user accounts like a database
 */
public class AccountManager {

    /**
     * Store accounts in the buffer
     */
    private TreeMap<String, AccountFullInformation> accounts;

    /**
     * Get accounts buffer
     * @return the accounts buffer
     */
    public TreeMap<String, AccountFullInformation> getAccounts() {
        return accounts;
    }

    /**
     * Create a new account manager (just called from the server manager)
     * @param dataPath the path of the files storing data
     */
    public AccountManager(String dataPath)
    {
        //Load data stored on the disk
        loadData(dataPath);
    }

    /**
     * Check an account exists or not
     * @param account the account to be checked
     * @return the check result (true: success, false: failure)
     */
    public boolean checkAccount(Account account)
    {
        try
        {
            byte[] password = accounts.get(account.username()).account().password();
            //Check matched password
            if (password != null)
                return Arrays.equals(password, account.password());
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Add a new account to the database
     * @param account the added account
     * @return the added status (true: success, false: failure)
     */
    public boolean addAccount(AccountFullInformation account)
    {
        try
        {
            //If the username exists in the system -> can not add
            if (accounts.containsKey(account.account().username()))
                return false;
            accounts.put(account.account().username(), new AccountFullInformation(account.account(), account.displayedName()));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Load users data from a given path
     * @param path the path of the loaded files
     */
    public void loadData(String path)
    {
        try
        {
            ObjectInputStream dataStream = new ObjectInputStream(new FileInputStream(path));
            //noinspection unchecked
            accounts = (TreeMap<String, AccountFullInformation>)dataStream.readObject();
            dataStream.close();
        }
        catch (Exception e)
        {
            //If catch an exception when loading data files -> the data does not exist
            //Use a new empty data
            accounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
    }

    /**
     * Save current data to the disk asynchronously
     * @param path the path of the saved files
     */
    public void saveData(String path)
    {
        try
        {
            //Create a new thread to save asynchronously
            new SaveFileAsync(accounts, path).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
