package capstone.spring20.tscc_driver.Api;

import java.util.List;

import capstone.spring20.tscc_driver.entity.CollectJob;
import capstone.spring20.tscc_driver.entity.CollectJobResponse;
import capstone.spring20.tscc_driver.entity.Employee;
import capstone.spring20.tscc_driver.entity.TrashArea;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TSCCDriverClient {

    @GET("/api/trash-areas/{id}")
    Call<TrashArea> getTrashAreaById(@Header("Authorization") String authorization,
                                     @Path("id") int id);

    @PUT("/api/trash-areas/update-status/{id}")
    Call<TrashArea> updateTrashAreaStatus(@Header("Authorization") String authorization,
                                          @Path("id") int id,
                                          @Body TrashArea trashArea);

    @GET("/api/employees/{email}/{token}")
    Call<Employee> updateFCMToken(@Header("Authorization") String authorization,
                                  @Path("email") String email, @Path("token") String token);

    @GET("/api/collect-jobs/complete/{id}")
    Call<CollectJob> completeCollectJob(@Header("Authorization") String authorization,
                                        @Path("id") int id);

    @GET("/api/collect-jobs/driver/{email}")
    Call<List<CollectJobResponse>> getCollectJobs(@Header("Authorization") String authorization,
                                                  @Path("email") String email);

    @GET("/api/employees/email/{email}")
    Call<Employee> getDriver(@Header("Authorization") String authorization,
                             @Path("email") String email);

}
