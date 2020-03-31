package capstone.spring20.tscc_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import capstone.spring20.tscc_driver.entity.RouteNotification;
import capstone.spring20.tscc_driver.util.MyDatabaseHelper;

public class NotificationActivity extends AppCompatActivity {

    ListView listView;
    List<RouteNotification> routeList = new ArrayList<>();
    ArrayAdapter<RouteNotification> routeListAdapter;
    MyDatabaseHelper db = new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setupBasic();

        routeListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, android.R.id.text1, routeList);
        listView.setAdapter(routeListAdapter);

    }

    private void setupBasic() {
        routeList = db.getAllRouteNotification();
        Collections.sort(routeList);

        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RouteNotification routeNotification = (RouteNotification) parent.getItemAtPosition(position);
                boolean isActive = routeNotification.isActive();
                if (isActive) {
                    Intent intent = new Intent(NotificationActivity.this, RouteActivity.class);
                    intent.putExtra("routeNotification", routeNotification);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NotificationActivity.this, RouteHistoryActivity.class);
                    intent.putExtra("routeNotification", routeNotification);
                    startActivity(intent);
                }
            }
        });
    }
}
