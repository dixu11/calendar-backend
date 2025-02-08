package szlicht.daniel.calendar.common.java;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JavaUtils {
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
