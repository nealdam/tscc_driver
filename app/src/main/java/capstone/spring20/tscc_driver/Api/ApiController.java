package capstone.spring20.tscc_driver.Api;

public class ApiController {
    public static String base_url1 = "http://192.168.1.72:5000/";
    //public static String base_url1 = "http://10.0.2.2:5000/";

    public static TSCCDriverClient getTsccDriverClient() {
        return RetrofitClient.getClient(base_url1).create(TSCCDriverClient.class);
    }
}
