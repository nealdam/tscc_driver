package capstone.spring20.tscc_mobile.constant;

public class TrashWidthConstant {

    public static String getTrashWidthId(String width){
        if (width.equals("SMALL"))
            return "1";
        if (width.equals("NORMAL"))
            return "2";
        if (width.equals("MEDIUM"))
            return "3";
        if (width.equals("BIG"))
            return "4";
        return "0";
    }
}
