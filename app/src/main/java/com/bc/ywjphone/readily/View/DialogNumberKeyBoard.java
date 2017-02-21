package com.bc.ywjphone.readily.View;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bc.ywjphone.readily.R;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
public class DialogNumberKeyBoard {
    View view;
    Context context;
    AlertDialog show;
    private StringBuffer sb;
    private TextView text;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
            btnX, btnP, btnOk, btnNo;

    EditText payout_enter_amount_et;

    public DialogNumberKeyBoard(Context context, EditText payout_enter_amount_et) {
        this.context = context;
        this.payout_enter_amount_et = payout_enter_amount_et;
        view = LayoutInflater.from(context).inflate(R.layout.number_keyboard, null);
        show = new AlertDialog.Builder(context)
                .setView(view)
                .show();
        initDialogView(context);
    }

    //初始化 dialog中的view
    private void initDialogView(Context context) {
        text = (TextView) view.findViewById(R.id.txt_pay_amount);
        btn0 = (Button) view.findViewById(R.id.btn_0);
        btn1 = (Button) view.findViewById(R.id.btn_1);
        btn2 = (Button) view.findViewById(R.id.btn_2);
        btn3 = (Button) view.findViewById(R.id.btn_3);
        btn4 = (Button) view.findViewById(R.id.btn_4);
        btn5 = (Button) view.findViewById(R.id.btn_5);
        btn6 = (Button) view.findViewById(R.id.btn_6);
        btn7 = (Button) view.findViewById(R.id.btn_7);
        btn8 = (Button) view.findViewById(R.id.btn_8);
        btn9 = (Button) view.findViewById(R.id.btn_9);
        btnP = (Button) view.findViewById(R.id.btn_t);
        btnX = (Button) view.findViewById(R.id.btn_d);
        btnOk = (Button) view.findViewById(R.id.btn_num_ok);
        btnNo = (Button) view.findViewById(R.id.btn_num_no);
        sb = new StringBuffer();
        btn0.setOnClickListener(new ShowDialogListener());
        btn1.setOnClickListener(new ShowDialogListener());
        btn2.setOnClickListener(new ShowDialogListener());
        btn3.setOnClickListener(new ShowDialogListener());
        btn4.setOnClickListener(new ShowDialogListener());
        btn5.setOnClickListener(new ShowDialogListener());
        btn6.setOnClickListener(new ShowDialogListener());
        btn7.setOnClickListener(new ShowDialogListener());
        btn8.setOnClickListener(new ShowDialogListener());
        btn9.setOnClickListener(new ShowDialogListener());
        btnX.setOnClickListener(new ShowDialogListener());
        btnP.setOnClickListener(new ShowDialogListener());
        btnOk.setOnClickListener(new ShowDialogListener());
        btnNo.setOnClickListener(new ShowDialogListener());
        text.setFocusable(false);
        text.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 关闭软键盘，这样当点击该edittext的时候，不会弹出系统自带的输入法
                text.setInputType(InputType.TYPE_NULL);
                return false;
            }
        });

    }

    public static boolean isContain(String s1, String s2) {
        return s1.contains(s2);
    }

    public class ShowDialogListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_num_ok:
                    String number = text.getText().toString();
                    payout_enter_amount_et.setText(number);
                    show.dismiss();//消失
                    break;
                case R.id.btn_num_no:
                    show.dismiss();//消失
                    break;
                case R.id.btn_t:
                    String str = sb.toString();
                    boolean b = isContain(str, ".");
                    if (!b) {
                        if (sb.length() == 0) {
                            sb.append("0");
                        }
                        sb.append(btnP.getText().toString().trim());
                        text.setText(sb.toString().trim());
                    }
                    break;
                case R.id.btn_d:
                    if (sb.length() - 1 >= 0) {
                        sb.delete(sb.length() - 1, sb.length());
                        text.setText(sb.toString().trim());
                    }
                    break;
                case R.id.btn_0:
                    sb.append(btn0.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_1:
                    sb.append(btn1.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_2:
                    sb.append(btn2.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_3:
                    sb.append(btn3.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_4:
                    sb.append(btn4.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_5:
                    sb.append(btn5.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_6:
                    sb.append(btn6.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_7:
                    sb.append(btn7.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_8:
                    sb.append(btn8.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
                case R.id.btn_9:
                    sb.append(btn9.getText().toString().trim());
                    text.setText(sb.toString().trim());
                    break;
            }
        }
    }
}
