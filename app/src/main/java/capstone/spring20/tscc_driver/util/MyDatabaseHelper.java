package capstone.spring20.tscc_driver.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.spring20.tscc_driver.entity.RouteNotification;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Notification_manager";
    private static final String TAG = "MyDatabaseHelper";

    String TABLE_NAME = "Route_notification";
    String COLUMN_ROUTE_ID = "id";
    String COLUMN_ROUTE_ORIGIN = "origin";
    String COLUMN_ROUTE_DESTINATION = "destination";
    String COLUMN_ROUTE_WAYPOINTS = "waypoints";
    String COLUMN_ROUTE_LOCATIONS = "locations";
    String COLUMN_ROUTE_TRASHAREAIDLIST = "trash_area_id_list";
    String COLUMN_ROUTE_RECEIVEDDATE = "received_date";
    String COLUMN_ROUTE_ACTIVE = "active";
    String COLUMN_ROUTE_COLLECTJOBID = "collect_job_id";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate sqlite...");
        //tạo bảng
        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ROUTE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ROUTE_ORIGIN + " TEXT,"
                + COLUMN_ROUTE_DESTINATION + " TEXT,"
                + COLUMN_ROUTE_WAYPOINTS + " TEXT,"
                + COLUMN_ROUTE_LOCATIONS + " TEXT,"
                + COLUMN_ROUTE_TRASHAREAIDLIST + " TEXT,"
                + COLUMN_ROUTE_RECEIVEDDATE + " TEXT,"
                + COLUMN_ROUTE_ACTIVE + " TEXT,"
                + COLUMN_ROUTE_COLLECTJOBID + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Và tạo lại.
        onCreate(db);
    }

    public RouteNotification getActiveRouteNotification(){
        RouteNotification route = null;

        String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_ROUTE_ACTIVE + " = 1"
                + " ORDER BY " + COLUMN_ROUTE_RECEIVEDDATE + " DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            route = new RouteNotification();
            route.setId(cursor.getInt(0));
            route.setOrigin(cursor.getString(1));
            route.setDestination(cursor.getString(2));
            route.setWaypoints(cursor.getString(3));
            route.setLocations(cursor.getString(4));
            route.setTrashAreaIdList(cursor.getString(5));
            route.setReceivedDate(DatetimeUtil.toDate(cursor.getString(6)));
            route.setActive(BooleanUtil.toBoolean(cursor.getString(7)));
            route.setCollectJobId(cursor.getString(8));
        }

        cursor.close();
        db.close();

        return route;
    }
    public List<RouteNotification> getAllRouteNotification() {
        List<RouteNotification> list = new ArrayList<>();
        //query all
        String sql = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        //duyệt trên con trỏ, thêm vào list
        if (cursor.moveToFirst()) {
            do {
                RouteNotification route = new RouteNotification();
                route.setId(cursor.getInt(0));
                route.setOrigin(cursor.getString(1));
                route.setDestination(cursor.getString(2));
                route.setWaypoints(cursor.getString(3));
                route.setLocations(cursor.getString(4));
                route.setTrashAreaIdList(cursor.getString(5));
                route.setReceivedDate(DatetimeUtil.toDate(cursor.getString(6)));
                route.setActive(BooleanUtil.toBoolean(cursor.getString(7)));
                route.setCollectJobId(cursor.getString(8));

                list.add(route);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public void addRouteNotification(RouteNotification route) {
        Log.d(TAG, "addRouteNotification: " + route);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUTE_ORIGIN, route.getOrigin());
        values.put(COLUMN_ROUTE_DESTINATION, route.getDestination());
        values.put(COLUMN_ROUTE_WAYPOINTS, route.getWaypoints());
        values.put(COLUMN_ROUTE_LOCATIONS, route.getLocations());
        values.put(COLUMN_ROUTE_TRASHAREAIDLIST, route.getTrashAreaIdList());
        values.put(COLUMN_ROUTE_RECEIVEDDATE, DatetimeUtil.getCurrentDatetime());
        values.put(COLUMN_ROUTE_ACTIVE, "1");// active = true
        values.put(COLUMN_ROUTE_COLLECTJOBID, route.getCollectJobId());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deactiveRouteNotification(int id) {
        String sql = "UPDATE " + TABLE_NAME + " SET "
                + COLUMN_ROUTE_ACTIVE + " = " + "0 "
                + "WHERE " + COLUMN_ROUTE_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUTE_ACTIVE, "0");
        db.update(TABLE_NAME, values, "id = "+id, null);
        db.close();
    }
}
