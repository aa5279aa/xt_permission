package com.xt.permission.permissionhepler;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by peng_j on 2015/12/24.
 */
public class PermissionSettingDialog extends Dialog {
    public PermissionSettingDialog(Context context) {
        super(context);
    }

    public PermissionSettingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        final int TITLE_Id = RelativeLayout.NO_ID + 10;//greater than 0
        final int MESSAGE_Id = RelativeLayout.NO_ID + 11;
        final int LEFT_BUTTON_Id = RelativeLayout.NO_ID + 12;
        final int RIGHT_BUTTON_Id = RelativeLayout.NO_ID + 13;


        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public PermissionSettingDialog create() {
            final PermissionSettingDialog dialog = new PermissionSettingDialog(context);
            RelativeLayout layout = new RelativeLayout(context);


            TextView titleText = new TextView(context);
            titleText.setId(TITLE_Id);
            titleText.setText(title);

            TextView messsageText = new TextView(context);
            messsageText.setId(MESSAGE_Id);
            messsageText.setText(message);


            Button leftBtn = new Button(context);
            leftBtn.setId(LEFT_BUTTON_Id);
            leftBtn.setText(negativeButtonText);
            leftBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    negativeButtonClickListener.onClick(dialog,
                            DialogInterface.BUTTON_POSITIVE);
                }
            });


            Button rightBtn = new Button(context);
            rightBtn.setId(RIGHT_BUTTON_Id);
            rightBtn.setText(positiveButtonText);
            rightBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    positiveButtonClickListener.onClick(dialog,
                            DialogInterface.BUTTON_POSITIVE);
                }
            });

            RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams messageLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams leftLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rightLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            titleLp.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.ALIGN_PARENT_TOP);
            messageLp.addRule(RelativeLayout.BELOW, TITLE_Id);
            messageLp.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.ALIGN_PARENT_TOP);
            leftLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            leftLp.addRule(RelativeLayout.BELOW, MESSAGE_Id);
            rightLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightLp.addRule(RelativeLayout.BELOW, MESSAGE_Id);

            layout.addView(titleText, titleLp);
            layout.addView(messsageText, messageLp);
            layout.addView(leftBtn, leftLp);
            layout.addView(rightBtn, rightLp);
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
