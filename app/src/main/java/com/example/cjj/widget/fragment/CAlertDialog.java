package com.example.cjj.widget.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.cjj.widget.R;

/**
 * Created by CJJ on 2016/8/18.
 *
 */
public class CAlertDialog extends DialogFragment {

    private static final String TAG = "CAlertDialog";
    private boolean[] checkedItems = {true,false,false,false,false,true};
    private int items = R.array.items;
    private boolean alertShow;
    private boolean called = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog: ");
        if (called)return null;
        called = true;
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Lorem PSUM")
                .setTitle("alert")
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertShow = false;
                        dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        alertShow = false;
                    }
                })
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                })
                .create();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putBoolean("alertShow",alertShow);
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        Log.i(TAG, "show: ");
        alertShow = true;
    }

}

