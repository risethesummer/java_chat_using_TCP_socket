package clientSide.GUI.utilities;

import java.awt.event.ActionListener;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 11/30/2021 - 11:41 AM
 * Description: Storing text and callback of a button
 */
public record ButtonTextCallback(String text, ActionListener callback) {
}
