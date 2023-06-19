package com.example.passwordmanager.Config;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import com.example.passwordmanager.R;

public class DialogPrompt {
    Dialog dialog;
    Button yes;
    Button no;

    public DialogPrompt(Context context, DialogBehaviour dialogBehaviour){

        dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog));
        }

        yes = dialog.findViewById(R.id.yes);
        no = dialog.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBehaviour.onYesClick();
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBehaviour.onNoClick();
                dialog.dismiss();
            }
        });
    }

    public void show(){
        dialog.show();
    }
}
