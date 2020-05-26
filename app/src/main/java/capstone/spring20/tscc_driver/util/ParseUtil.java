package capstone.spring20.tscc_driver.util;

public class ParseUtil {

    public static int tryParseStringtoInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
