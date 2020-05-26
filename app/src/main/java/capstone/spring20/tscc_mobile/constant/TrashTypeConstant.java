package capstone.spring20.tscc_mobile.constant;

public class TrashTypeConstant {

    public static String getTrashTypeId(String type) {
        if (type.equals("ORGANIC"))
            return "1";
        if (type.equals("RECYCLE"))
            return "2";
        if (type.equals("OTHER"))
            return "3";
        return "0";
    }
    
}
