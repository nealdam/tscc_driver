package capstone.spring20.tscc_driver.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import capstone.spring20.tscc_driver.R;
import capstone.spring20.tscc_driver.entity.CollectJobResponse;

public class CustomListView extends ArrayAdapter<CollectJobResponse> {

    Context context;

    public CustomListView(Context context, int resourceId, List<CollectJobResponse> items) {
        super ( context, resourceId, items );
        this.context = context;
    }

    private class ViewHolder {
        ImageView icon;
        TextView txtTitle;
        TextView txtDate;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CollectJobResponse rowItem = getItem ( position );
        LayoutInflater li = (LayoutInflater) context.getSystemService ( Activity.LAYOUT_INFLATER_SERVICE );
        if (convertView == null) {
            convertView = li.inflate ( R.layout.list_noti_items, null );
            holder = new ViewHolder ();
            holder.txtDate = (TextView) convertView.findViewById ( R.id.noti_date );
            holder.txtTitle = (TextView) convertView.findViewById ( R.id.noti_title );
            holder.icon = (ImageView) convertView.findViewById ( R.id.noti_icon );
            convertView.setTag ( holder );
        } else {
            holder = (ViewHolder) convertView.getTag ();
        }

        String oldFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String newFormat = "E, dd/MM 'lúc' HH:mm";

        try {
            SimpleDateFormat sformat = new SimpleDateFormat(oldFormat);
            Date date = sformat.parse(rowItem.getCreatAt ());
            sformat.applyPattern ( newFormat );
            String newDate = sformat.format ( date );
            holder.txtDate.setText(newDate);
        } catch (ParseException e) {
            holder.txtDate.setText ( rowItem.getCreatAt () );
            e.printStackTrace ();
        }

        switch (rowItem.getTrashStatus ()) {

            case "PROCESSING":
                holder.icon.setImageResource ( R.drawable.ic_watch_later_60dp );
                holder.txtTitle.setText (R.string.trash_processing);
                break;
            case "CANCEL":
                holder.icon.setImageResource ( R.drawable.ic_cancel_red_60dp );
                holder.txtTitle.setText (R.string.trash_cancel);
                break;
            case "DONE":
                holder.icon.setImageResource ( R.drawable.ic_check_circle_green_60dp );
                holder.txtTitle.setText (R.string.trash_done);
                break;
        }

        return  convertView;
    }
}
