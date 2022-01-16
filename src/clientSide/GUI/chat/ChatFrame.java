package clientSide.GUI.chat;
import clientSide.GUI.utilities.TwoWaysDisposeFrame;
import clientSide.GUI.chat.panels.ChatContentPanel;
import clientSide.GUI.chat.panels.messages.fileMessages.FileMessagePanel;
import clientSide.messages.FileMessage;
import clientSide.GUI.utilities.LabelImage;
import clientSide.messages.Message;
import sockets.protocols.accounts.AccountShowInformation;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static clientSide.GUI.chat.panels.ChatContentPanel.OTHER;
import static utilities.StringHandler.addDateTime;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:40 PM
 * Description: The frame showing other users and their chat sections
 */
public class ChatFrame extends TwoWaysDisposeFrame {

    /**
     * Each tab for a user chat section
     */
    private final JTabbedPane userTab = new JTabbedPane(SwingConstants.LEFT);
    /**
     * Store index of each user in the users tab
     */
    private final Hashtable<String, Integer> chatIndexes = new Hashtable<>();
    /**
     * Callback for sending messages
     */
    private final Consumer<Message> onSendMsg;
    /**
     * Callback for sending files
     */
    private final BiConsumer<FileMessage, IntConsumer> onSendFile;

    /**
     * System log model
     */
    private final DefaultListModel<String> logModel = new DefaultListModel<>();

    /**
     * Create a new frame
     * @param onSendMsg sending messages callback
     * @param onSendFile sending files callback
     * @param onClose closing the frame callback
     */
    public ChatFrame(Consumer<Message> onSendMsg, BiConsumer<FileMessage, IntConsumer> onSendFile, Runnable onClose)
    {
        super("Chat (close to sign out)", "chat.png", onClose);
        this.onSendMsg = onSendMsg;
        this.onSendFile = onSendFile;
        userTab.add("system", new JList<>(logModel));
        //Show blue color for tabs receiving messages
        userTab.addChangeListener(l -> {
            try
            {
                int index = userTab.getSelectedIndex();
                if (index != 0)
                {
                    ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
                    //Just set white color for the tab of an online user
                    if (chat.getIsOnline())
                        userTab.setBackgroundAt(userTab.getSelectedIndex(), Color.WHITE);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        //Set the selected tabs' default color is gray
        UIManager.put("TabbedPane.selected", Color.GRAY);
        getContentPane().add(userTab);
        setMinimumSize(new Dimension(700, 700));
        //Stand in the center of the screen
        setLocationRelativeTo(null);
    }


    /**
     * Show status (online/offline) of the other users (or add new online users)
     * @param users the online users list
     */
    public void reloadUsers(List<AccountShowInformation> users)
    {
        try
        {
            Set<String> currentStoredUsers = chatIndexes.keySet();
            //Get displayed information of the users
            HashMap<String, String> loadUsersToName = new HashMap<>();
            for (AccountShowInformation user : users)
                loadUsersToName.put(user.username(), user.displayedName());
            //Get new online users
            Set<String> newUsers = new HashSet<>(loadUsersToName.keySet());
            //Get the users have been offline when being disconnected
            Set<String> offlineUsers = new HashSet<>(currentStoredUsers);

            //Offline users = current users - new users
            offlineUsers.removeAll(newUsers);
            for (String user : offlineUsers)
                offlineUser(user);

            //Online users = new users - current users
            newUsers.removeAll(currentStoredUsers);
            for (String user : newUsers)
                addNewOnlineUser(new AccountShowInformation(user, loadUsersToName.get(user)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a panel for a new online user
     * @param user the new online user
     */
    public void addNewOnlineUser(AccountShowInformation user)
    {
        try
        {
            userTab.add(user.displayedName() + " (online)", new ChatContentPanel(user, onSendMsg, onSendFile));
            LabelImage tabLabel = new LabelImage("online.png", user.displayedName() + " (online)");
            //Design the tab using a tab label
            userTab.setTabComponentAt(userTab.getTabCount() - 1, tabLabel);
            userTab.setBackgroundAt(userTab.getTabCount() - 1, Color.WHITE);
            chatIndexes.put(user.username(), userTab.getTabCount() - 1);
            userTab.updateUI();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set a user's status to online (or add a chat section for him)
     * @param user the online user
     */
    public void onlineUser(AccountShowInformation user)
    {

        try
        {
            //Get tab index of the user (actually get the chat panel of the user)
            Integer index = chatIndexes.get(user.username());

            addLog(user.displayedName() + " is online");

            //The action of adding online user
            Runnable addRun;

            //The user has in the system (no need to add the user)
            if (index != null)
            {
                ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
                //Set the title of the panel to online status
                String title = user.displayedName() + " (online)";
                //Mark the user as online
                chat.setOnline(true);

                addRun = () -> {
                    //Set the GUI of the chat panel as online
                    LabelImage tabLabel = new LabelImage("online.png", title);
                    userTab.setTabComponentAt(index, tabLabel);
                    userTab.setBackgroundAt(index, Color.WHITE);
                    userTab.setTitleAt(index, title);
                    userTab.updateUI();
                };
            }
            else
                //If the user is not in the system -> create a new chat panel for him
                addRun = ()  -> addNewOnlineUser(user);

            //If currently running on the GUI event thread -> run on the current thread
            //Else, runs on the GUI thread
            if (SwingUtilities.isEventDispatchThread())
                addRun.run();
            else
                SwingUtilities.invokeAndWait(addRun);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set a user's status to offline
     * @param user the offline user
     */
    public void offlineUser(String user)
    {
        try
        {
            Integer index = chatIndexes.get(user);
            //Just mark the user as offline when the user has been in the system
            if (index != null)
            {
                //Get new offline title
                String title = userTab.getTitleAt(index).replace("online", "offline");
                ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
                addLog(chat.getUser().displayedName() + " is offline");
                //Mark the user as offline
                chat.setOnline(false);
                SwingUtilities.invokeAndWait(() -> {
                    //Set the GUI of the chat panel as offline
                    LabelImage tabLabel = new LabelImage("offline.png", title);
                    userTab.setTabComponentAt(index, tabLabel);
                    userTab.setBackgroundAt(index, Color.RED);
                    userTab.setTitleAt(index, title);
                    userTab.updateUI();
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a log to the system tab
     * @param log the log
     */
    public void addLog(String log)
    {
        logModel.addElement(addDateTime(log));
    }

    /**
     * Add a file message to a user's chat section
     * @param file the file message
     */
    public void addNewFile(FileMessage file)
    {
        Integer index = chatIndexes.get(file.user());
        //If we could find the chat panel of the user
        if (index != null)
        {
            //Notify that the user has sent a new message if the client was not in the user's tab
            if (userTab.getSelectedIndex() != index)
                userTab.setBackgroundAt(index, Color.CYAN);
            FileMessagePanel filePanel = new FileMessagePanel(file);
            ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
            chat.addMsg(filePanel, OTHER);
        }
    }

    /**
     * Add a new text message to a user's chat section
     * @param msg the text message
     */
    public void addNewChatLog(Message msg)
    {
        Integer index = chatIndexes.get(msg.user());
        //If we could find the chat panel of the user
        if (index != null)
        {
            //Notify that the user has sent a new message if the client was not in the user's tab
            if (userTab.getSelectedIndex() != index)
                userTab.setBackgroundAt(index, Color.CYAN);
            ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
            chat.addMsg(msg, OTHER);
        }
    }
}
