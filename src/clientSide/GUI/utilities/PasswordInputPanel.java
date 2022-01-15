package clientSide.GUI.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 11/29/2021 - 9:06 PM
 * Description: An input panel with hidden text field
 */
public class PasswordInputPanel extends JPanel {

    /**
     * The field for inputting password
     */
    private final JPasswordField passwordField;

    /**
     * Create a panel with text on the label
     * @param labelText the text shown on the label
     */
    public PasswordInputPanel(String labelText)
    {
        super(new BorderLayout());
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JLabel label = new JLabel(labelText);

        //To make sure the panel not too high
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        add(Box.createHorizontalStrut(20));
        add(label);
        add(Box.createHorizontalStrut(10));
        add(passwordField);
        add(Box.createHorizontalStrut(20));
    }

    /**
     * Get current information on the text field
     * @return the hidden information
     */
    public String getPassword()
    {
        return new String(passwordField.getPassword());
    }
}
