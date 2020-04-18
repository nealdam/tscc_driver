package capstone.spring20.tscc_driver.util;

import capstone.spring20.tscc_driver.entity.TrashArea;

public class IconUtil {
    public static String getIconName(TrashArea t, int numOrder) {
        String iconName = "";

        String type = t.getType().getName().toLowerCase();
        String status = t.getStatus().getName().toLowerCase();
        switch (type) {
            case "recycle":
                iconName += "recycle";
                switch (status) {
                    case "processing":
                        iconName += "_processing_";
                        iconName += numOrder;
                        break;
                    case "canceled":
                        iconName += "_canceled";
                        break;
                    case "done":
                        iconName += "_done";
                        break;
                }
                break;
            case "organic":
                iconName += "organic";
                switch (status) {
                    case "processing":
                        iconName += "_processing_";
                        iconName += numOrder;
                        break;
                    case "canceled":
                        iconName += "_canceled";
                        break;
                    case "done":
                        iconName += "_done";
                        break;
                }
                break;
            case "other":
                iconName += "other";
                switch (status) {
                    case "processing":
                        iconName += "_processing_";
                        iconName += numOrder;
                        break;
                    case "canceled":
                        iconName += "_canceled";
                        break;
                    case "done":
                        iconName += "_done";
                        break;
                }
                break;
        }

        return iconName.isEmpty() ? "default" : iconName ;
    }
}
