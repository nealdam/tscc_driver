package capstone.spring20.tscc_driver.util;

public class BooleanUtil {
    public static boolean toBoolean(String s) {
        if (s.equals("1"))
            return true;
        return false;
    }
}
