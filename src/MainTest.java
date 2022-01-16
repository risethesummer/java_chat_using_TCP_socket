import java.net.URL;

/**
 * PACKAGE_NAME
 * Created by NhatLinh - 19127652
 * Date 1/16/2022 - 2:55 PM
 * Description: ...
 */
public class MainTest {

    public static void main(String[] args)
    {
         URL input = MainTest.class.getClassLoader().getResource("chat.png");
         System.out.println(input);
    }
}
