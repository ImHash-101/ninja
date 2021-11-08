package top.goup.update;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import top.goup.ninja.R;

public class BaseDialog extends Dialog implements View.OnClickListener {

    Context mContext;

    TextView mTitle;
    TextView mContent;
    Button mConfirm;

    public BaseDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public BaseDialog(Context context, String title, String content) {
        this(context);
        mContext = context;
        initView();
        mTitle.setText(title);
        mContent.setText(content);
    }


    private void initView() {
        setContentView(R.layout.base_dialog);
        mContent = findViewById(R.id.dialog_context);
        mConfirm = findViewById(R.id.dialog_confirm);
        mTitle = findViewById(R.id.dialog_title);
        mConfirm.setOnClickListener(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_confirm:
                dismiss();
                break;
        }
    }
}
