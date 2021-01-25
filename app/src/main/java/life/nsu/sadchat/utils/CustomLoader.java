package life.nsu.sadchat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import life.nsu.sadchat.R;

public class CustomLoader {

    private final Dialog dialog;

    public CustomLoader(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.custom_dialog_loader);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void show() {
        dialog.show();
    }


    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
