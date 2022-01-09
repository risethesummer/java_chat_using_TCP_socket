package clientSide.GUI.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 11/29/2021 - 8:56 PM
 * Description: A panel consisting of a label and a text field
 */
public class InputPanel extends JPanel {

    private final JTextField inputField;

    /**
     * Create an input panel assigning original text for the label
     * @param labelText the text shown on the label
     */
    public InputPanel(String labelText)
    {
        this(labelText, "");
    }

    /**
     * Create an input panel assigning original text for the label and the text field
     * @param labelText the text shown on the label
     * @param firstShowInField the text shown on the text field
     */
    public InputPanel(String labelText, String firstShowInField)
    {
        super();

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JLabel label = new JLabel(labelText);

        inputField = new JTextField(firstShowInField);
        inputField.setMinimumSize(new Dimension(50, 20));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        add(Box.createHorizontalStrut(20));
        add(label);
        add(Box.createHorizontalStrut(10));
        add(inputField);
        add(Box.createHorizontalStrut(20));
    }

    /**
     * Get input field property
     * @return input field property
     */
    public JTextField getInputField()
    {
        return this.inputField;
    }
}
