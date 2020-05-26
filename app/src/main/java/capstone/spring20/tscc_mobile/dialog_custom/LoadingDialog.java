package capstone.spring20.tscc_mobile.dialog_custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import capstone.spring20.tscc_mobile.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( activity );

        LayoutInflater inflater = activity.getLayoutInflater ();
        builder.setView ( inflater.inflate ( R.layout.custom_dialog, null ) );
        builder.setCancelable ( false );

        dialog = builder.create ();
        dialog.show ();
    }

    public void dismissDialog() {
        dialog.dismiss ();
    }
}
