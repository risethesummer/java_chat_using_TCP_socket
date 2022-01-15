package clientSide.GUI.utilities;

import utilities.ImageRepository;

import javax.swing.*;
import java.awt.*;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/13/2022 - 8:28 PM
 * Description: A button with image icon
 */
public class ButtonImage extends JButton {

    /**
     * Create a new button
     * @param imgName the name of the image showing on the button
     * @param size the size of the image
     * @param toolTip the tooltip displayed on the button
     */
    public ButtonImage(String imgName, int size, String toolTip)
    {
        super(new ImageIcon(ImageRepository.getInstance().getImage(imgName, size)));
        setToolTipText(toolTip);
        setBackground(Color.WHITE);
        //To constraint the button not to get large
        setMaximumSize(new Dimension(size, size));
    }
}
