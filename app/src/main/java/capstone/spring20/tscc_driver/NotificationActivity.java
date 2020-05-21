package capstone.spring20.tscc_driver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import capstone.spring20.tscc_driver.Api.ApiController;
import capstone.spring20.tscc_driver.Api.TSCCDriverClient;
import capstone.spring20.tscc_driver.adapters.CustomListView;
import capstone.spring20.tscc_driver.entity.CollectJobResponse;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

public class NotificationActivity extends Fragment {

    ListView listView;
    List<CollectJobResponse> rowItems;
    List<RouteNotification> routeList = new ArrayList<>();
    List<CollectJobResponse> collectJobList;
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

    private void setupBasic(final View rootView) {
        //bỏ strict để có thể call api sync
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listView = rootView.findViewById(R.id.listview);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("JWT", MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("token", "");

        TSCCDriverClient client = ApiController.getTsccDriverClient();
        Call<List<CollectJobResponse>> call = client.getCollectJobs(jwtToken, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        try {
            collectJobList = call.execute().body();
            if (collectJobList != null) {
                Collections.sort(collectJobList);
                //set vào UI
                collectJobAdapter = new CustomListView(getActivity(), R.layout.list_noti_items, collectJobList);
                listView.setAdapter(collectJobAdapter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}