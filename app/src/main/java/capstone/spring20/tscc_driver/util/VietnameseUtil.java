package capstone.spring20.tscc_driver.util;

public class VietnameseUtil {

    public static String getTrashType(String type) {
        if (type.toLowerCase().equals("recycle"))
            return "Tái chế";
        if (type.toLowerCase().equals("organic"))
            return "Hữu cơ";
        return "Loại khác";
    }
}
