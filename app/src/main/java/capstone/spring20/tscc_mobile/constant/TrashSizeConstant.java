package capstone.spring20.tscc_mobile.constant;

public class TrashSizeConstant {

    public static String getTrashSizeId(String size){
        if (size.equals("SMALL"))
            return "1";
        if (size.equals("NORMAL"))
            return "2";
        if (size.equals("MEDIUM"))
            return "3";
        if (size.equals("BIG"))
            return "4";
        return "0";
    }

}
