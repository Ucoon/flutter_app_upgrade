package tech.ucoon.flutter_app_upgrade.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import tech.ucoon.flutter_app_upgrade.R;


public class OpenUnKnownSettingDialog extends Dialog implements View.OnClickListener {
    private View.OnClickListener onClickListener;
    public TextView tvContent;
    public Button   dialogPositive;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OpenUnKnownSettingDialog(Context context, String msg) {
        super(context, R.style.EasyDialogStyle);
        init(context,msg);
    }

    private void init(Context context,String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_open_unknown_setting_layout, null);
        dialogPositive = view.findViewById(R.id.btnConfirm);
        dialogPositive.setOnClickListener(this);
        tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(msg);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        lp.width = (int) (display.getWidth() * 0.72);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnConfirm){
            if (onClickListener != null) {
                dismiss();
                onClickListener.onClick(v);
            }
        }
    }
}
