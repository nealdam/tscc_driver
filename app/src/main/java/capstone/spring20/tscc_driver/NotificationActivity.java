package capstone.spring20.tscc_driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;

public class NotificationActivity extends Fragment {

    ListView listView;
    Button btnBack;
    List<RouteNotification> routeList = new ArrayList<>();
    ArrayAdapter<RouteNotification> routeListAdapter;
    MyDatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_notification, container,false);
        setupBasic(rootView);

        routeListAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, android.R.id.text1, routeList);
        listView.setAdapter(routeListAdapter);
        return rootView;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setupBasic();

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationActivity.this.onBackPressed();
            }
        });
        routeListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, android.R.id.text1, routeList);
        listView.setAdapter(routeListAdapter);

    }*/

    private void setupBasic(View rootView) {
        db = new MyDatabaseHelper(getActivity());
        routeList = db.getAllRouteNotification();
        Collections.sort(routeList);

        listView = rootView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RouteNotification routeNotification = (RouteNotification) parent.getItemAtPosition(position);
                boolean isActive = routeNotification.isActive();
                if (isActive) {
                    RouteFragment routeFragment = new RouteFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("routeNotification", routeNotification);
                    routeFragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, routeFragment).commit();
                } else {
                    Toast.makeText(getActivity(), "Mission complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
