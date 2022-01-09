package clientSide.GUI;

import javax.swing.*;

/**
 * clientSide.GUI
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 9:53 PM
 * Description: ...
 */
public class TwoWaysDisposeFrame extends JFrame {

    private final Runnable onClose;

    public TwoWaysDisposeFrame(String title, Runnable onClose)
    {
        super(title);
        this.onClose = onClose;
    }

    public void disposeNoTrigger()
    {
        super.dispose();
    }

    @Override
    public void dispose()
    {
        onClose.run();
        super.dispose();
    }
}
