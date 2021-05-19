package com.guohua.north_bulb.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by techno-110 on 4/7/17.
 */
public class ChooseFavColorDilaog {

    static AlertDialog.Builder alertDialog;
    static AlertDialog dialog;
    public static void showFavAlertDialog(Context context){

        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Please select color from Color Pelette!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        dialog = alertDialog.create();
        dialog.show();
    }
}
