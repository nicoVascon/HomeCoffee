package ipleiria.pdm.homecoffee.components;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import ipleiria.pdm.homecoffee.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;
    private TextView textViewLoadingDialogMainText;
    private TextView textViewLoadingDialogSubText;

    public LoadingDialog(Activity myactivity)
    {
        activity= myactivity;
    }

    public void startLoadingDialog(){
        startLoadingDialog(false);
    }

    public void startLoadingDialog(boolean cancelable)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_dialog_loading,null);
                textViewLoadingDialogMainText = view.findViewById(R.id.textViewLoadingDialogMainText);
                textViewLoadingDialogSubText = view.findViewById(R.id.textViewLoadingDialogSubText);
                textViewLoadingDialogSubText.setVisibility(View.GONE);
                builder.setView(view);
                builder.setCancelable(cancelable);

                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void setSubText(String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(text == null || text.trim().isEmpty()){
                    textViewLoadingDialogSubText.setVisibility(View.GONE);
                    return;
                }
                textViewLoadingDialogSubText.setText(text);
                textViewLoadingDialogSubText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setMainText(String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(text == null || text.trim().isEmpty()){
                    textViewLoadingDialogMainText.setVisibility(View.GONE);
                    return;
                }
                textViewLoadingDialogMainText.setText(text);
                textViewLoadingDialogMainText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void dismisDialog()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }
}