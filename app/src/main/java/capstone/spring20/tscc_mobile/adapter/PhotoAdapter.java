package capstone.spring20.tscc_mobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import capstone.spring20.tscc_mobile.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

    private Context myContext;
    private List<Uri> listData;

    public PhotoAdapter(Context myContext) {
        this.myContext = myContext;
    }

    public void setPhotos (List<Uri> listData) {
        this.listData = listData;
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext ()).inflate ( R.layout.item_photo, parent, false );
        return new PhotoViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = listData.get ( position );
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap ( myContext.getContentResolver (), uri );
            holder.imgPhoto.setImageBitmap ( bitmap );
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public int getItemCount() {
        return null == listData ? 0 : listData.size ();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super ( itemView );
            imgPhoto = itemView.findViewById ( R.id.img_photo );
        }
    }
}
