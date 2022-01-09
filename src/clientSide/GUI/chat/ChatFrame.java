package clientSide.GUI.chat;
import clientSide.GUI.TwoWaysDisposeFrame;
import clientSide.GUI.chat.panels.ChatContentPanel;
import clientSide.GUI.chat.panels.FileMessagePanel;
import clientSide.GUI.utilities.FileMessage;
import clientSide.GUI.utilities.Message;
import serverSide.accounts.AccountShowInformation;

import javax.swing.*;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/**
 * clientSide.GUI.chat
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 2:40 PM
 * Description: ...
 */
public class ChatFrame extends TwoWaysDisposeFrame {

    private final JTabbedPane userTab = new JTabbedPane();
    private final Hashtable<String, Integer> chatIndexes = new Hashtable<>();
    private final Consumer<Message> onSendMsg;
    private final BiConsumer<FileMessage, DoubleConsumer> onSendFile;

    public ChatFrame(Consumer<Message> onSendMsg, BiConsumer<FileMessage, DoubleConsumer> onSendFile, Runnable onClose)
    {
        super("Chat", onClose);
        this.onSendMsg = onSendMsg;
        this.onSendFile = onSendFile;
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();
    }

    public void loadUsers(List<AccountShowInformation> users)
    {
        try
        {
            chatIndexes.clear();
            SwingUtilities.invokeAndWait(userTab::removeAll);

            for (AccountShowInformation user : users)
            {
                ChatContentPanel chat = new ChatContentPanel(user, onSendMsg, onSendFile);
                SwingUtilities.invokeAndWait(() -> userTab.add(user.displayedName() + " (online)", chat));
                chatIndexes.put(user.account(), userTab.getTabCount() - 1);
            }

            SwingUtilities.invokeAndWait(userTab::updateUI);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addNewOnlineUser(AccountShowInformation user)
    {
        try
        {
            SwingUtilities.invokeAndWait(() -> {
                userTab.add(user.displayedName(), new ChatContentPanel(user, onSendMsg, onSendFile));
                chatIndexes.put(user.account(), userTab.getTabCount() - 1);
                userTab.updateUI();
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onlineUser(AccountShowInformation user)
    {

        try
        {
            Integer index = chatIndexes.get(user.account());
            if (index != null)
            {
                ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
                String title = userTab.getTitleAt(index).replace("offline", "online");
                chat.setOnline(true);
                SwingUtilities.invokeAndWait(() -> {
                    userTab.setTitleAt(index, title);
                    userTab.updateUI();
                });
            }
            else
                addNewOnlineUser(user);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void offlineUser(String user)
    {
        try
        {
            Integer index = chatIndexes.get(user);
            if (index != null)
            {
                String title = userTab.getTitleAt(index).replace("online", "offline");
                ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
                chat.setOnline(false);
                SwingUtilities.invokeAndWait(() -> {
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

    public void addNewFile(FileMessage file)
    {
        Integer index = chatIndexes.get(file.user());
        if (index != null)
        {
            FileMessagePanel filePanel = new FileMessagePanel(file);
            ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
            chat.addMsg(file.user(), filePanel);
        }
    }

    public void addNewChatLog(Message msg)
    {
        Integer index = chatIndexes.get(msg.user());
        if (index != null)
        {
            ChatContentPanel chat = (ChatContentPanel)userTab.getComponentAt(index);
            chat.addMsg(msg);
        }
    }
}
