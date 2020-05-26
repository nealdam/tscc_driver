package capstone.spring20.tscc_mobile.Api;

public class ApiController {

//    public static String base_url1 = "http://172.96.189.47:5000/";
    //public static String base_url1 = "http://192.168.1.72:5000/";
    public static String base_url1 = "http://10.0.2.2:5000/";

    public static TSCCClient getTsccClient() {
        return RetrofitClient.getClient(base_url1).create(TSCCClient.class);
    }
}
