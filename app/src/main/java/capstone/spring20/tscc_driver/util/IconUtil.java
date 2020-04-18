package capstone.spring20.tscc_driver.util;

import capstone.spring20.tscc_driver.entity.TrashArea;

public class IconUtil {
    public static String getIconName(TrashArea t, int numOrder) {
        String iconName = "";

        String type = t.getType().getName();
        String status = t.getStatus().getName();
        switch (type) {
            case "RECYCLE":
                iconName += "RECYCLE";
                switch (status) {
                    case "PROCESSING":
                        iconName += "_PROCESSING_";
                        iconName += numOrder;
                        break;
                    case "CANCELED":
                        iconName += "_CANCELED";
                        break;
                    case "DONE":
                        iconName += "_DONE";
                        break;
                }
                break;
            case "ORGANIC":
                iconName += "ORGANIC";
                switch (status) {
                    case "PROCESSING":
                        iconName += "_PROCESSING_";
                        iconName += numOrder;
                        break;
                    case "CANCELED":
                        iconName += "_CANCELED";
                        break;
                    case "DONE":
                        iconName += "_DONE";
                        break;
                }
                break;
            case "OTHER":
                iconName += "OTHER";
                switch (status) {
                    case "PROCESSING":
                        iconName += "_PROCESSING_";
                        iconName += numOrder;
                        break;
                    case "CANCELED":
                        iconName += "_CANCELED";
                        break;
                    case "DONE":
                        iconName += "_DONE";
                        break;
                }
                break;
        }

        return iconName.isEmpty() ? "default" : iconName ;
    }
}
