package capstone.spring20.tscc_driver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.entity.CollectJobResponse;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationActivity extends Fragment {

    ListView listView;
    List<RouteNotification> routeList = new ArrayList<>();
    List<CollectJobResponse> collectJobList = new ArrayList<>();
    ArrayAdapter<RouteNotification> routeListAdapter;
    ArrayAdapter<CollectJobResponse> collectJobAdapter;
    MyDatabaseHelper db;
    String jwtToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_notification, container, false);

        setupBasic(rootView);

        return rootView;
    }

    private void setupBasic(View rootView) {
        listView = rootView.findViewById(R.id.listview);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("JWT", MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("token", "");

        TSCCDriverClient client = ApiController.getTsccDriverClient();
        Call<List<CollectJobResponse>> call = client.getCollectJobs(jwtToken, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        call.enqueue(new Callback<List<CollectJobResponse>>() {
            @Override
            public void onResponse(Call<List<CollectJobResponse>> call, @NotNull Response<List<CollectJobResponse>> response) {
                //lấy data từ server
                collectJobList = response.body();
                Collections.sort(collectJobList);
                //set vào UI
                collectJobAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_expandable_list_item_1, android.R.id.text1, collectJobList);
                listView.setAdapter(collectJobAdapter);
            }
            @Override
            public void onFailure(Call<List<CollectJobResponse>> call, Throwable t) {
            }
        });

    }
}