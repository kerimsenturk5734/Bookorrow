package com.example.bookborrow;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Messages {
    public static  void warningMessage(String message, Context activity){
        AlertDialog.Builder alert=new AlertDialog.Builder(activity);
        alert
                .setTitle("Uyarı")
                .setMessage(message)
                .setIcon(R.drawable.warning)
                .setCancelable(false)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
    public static  void infoMessage(String message, Context activity){
        AlertDialog.Builder alert=new AlertDialog.Builder(activity);
        alert
                .setTitle("Bilgi")
                .setMessage(message)
                .setIcon(R.drawable.info)
                .setCancelable(false)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    public static  void infoMessageOnBackPressed(String message, Context activity){
        AlertDialog.Builder alert=new AlertDialog.Builder(activity);
        alert
                .setTitle("Bilgi")
                .setMessage(message)
                .setIcon(R.drawable.info)
                .setCancelable(false)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).show();


    }
}
