package com.isaiko.etheroll.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ExtensionsUtils {

    public static void ToastInTask(String toastText, Context context){
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, toastText , Toast.LENGTH_LONG).show());
    }
}
