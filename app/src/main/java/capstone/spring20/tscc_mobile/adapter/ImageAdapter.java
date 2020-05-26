package capstone.spring20.tscc_mobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context myContext;
    List<Bitmap> imageArray;

    public ImageAdapter(Context myContext, List<Bitmap> imageArray) {
        this.myContext = myContext;
        this.imageArray = imageArray;
    }

    @Override
    public int getCount() {
        return imageArray.size ();
    }

    @Override
    public Object getItem(int position) {
        return imageArray.get ( position );
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ImageView imageView = new ImageView ( myContext );
        imageView.setImageBitmap ( imageArray.get ( position ) );
        imageView.setScaleType ( ImageView.ScaleType.CENTER_CROP );
        imageView.setLayoutParams ( new GridView.LayoutParams ( 400,400 ) );

        return imageView;
    }

    public void removeImage(int position) {
        imageArray.remove ( position );
    }
}
