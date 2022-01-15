package utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * utilities
 * Created by NhatLinh - 19127652
 * Date 1/14/2022 - 12:19 PM
 * Description: The class including some string utility functions
 */
public class StringHandler {

    /**
     * Add date time to a string (example: "myString" -> "myString (13:55:30)")
     * @param content the string used to add datetime
     * @return the formatted string
     */
    public static String addDateTime(String content)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return content + " (" + LocalDateTime.now().format(formatter) + ")";
    }

    /**
     * Break a string to multi-lines string
     * @param wordEachLine how many words should be on one line
     * @param content the string to be broken
     * @return the lines of the original string
     */
    public static String getLines(String content, int wordEachLine)
    {
        try
        {
            int lineCount = (content.length() / wordEachLine) + 1;
            StringBuilder builder = new StringBuilder(lineCount);
            char[] charContent = content.toCharArray();
            for (int i = 0; i < lineCount - 1; i++)
                builder.append(charContent, i * wordEachLine, wordEachLine).append('\n');
            int leftLen = content.length() - (wordEachLine * (lineCount - 1));
            builder.append(charContent, wordEachLine * (lineCount - 1), leftLen);
            return builder.toString();
        }
        catch (Exception e)
        {
            return content;
        }
    }
}
