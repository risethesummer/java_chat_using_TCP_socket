package clientSide.GUI.utilities;

import utilities.ImageRepository;

import javax.swing.*;

/**
 * clientSide.GUI
 * Created by NhatLinh - 19127652
 * Date 1/9/2022 - 9:53 PM
 * Description: Representing a panel with two ways of disposing (active or passive)
 */
public class TwoWaysDisposeFrame extends JFrame {

    /**
     * Callback when closing the panel
     */
    private final Runnable onClose;

    /**
     * Create a panel
     * @param title the title of the panel
     * @param imgPath the path of the image for showing on the top bar
     * @param onClose the callback when closing the panel
     */
    public TwoWaysDisposeFrame(String title, String imgPath, Runnable onClose)
    {
        super(title);
        this.onClose = onClose;
        setIconImage(ImageRepository.getInstance().getImage(imgPath, ImageRepository.SMALL));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
    }

    /**
     * Dispose the panel from another place
     */
    public void disposeNoTrigger()
    {
        super.dispose();
    }

    /**
     * Dispose the panel by clicking the close button
     */
    @Override
    public void dispose()
    {
        //Not actually dispose the panel but calling the callback instead
        onClose.run();
    }
}
