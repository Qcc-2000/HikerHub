package edu.northeastern.hikerhub.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewUtils {

    public static class JumpTextWatcher implements TextWatcher {
        private EditText mThisView = null;
        private View mNextView = null;

        public JumpTextWatcher(EditText vThis, View vNext) {
            super();
            mThisView = vThis;
            if (vNext != null) {
                mNextView = vNext;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.indexOf("/r") >= 0 || str.indexOf("\n") >= 0) {
                /**
                 * If a carriage return or line feed is found, replace it with a null character
                 */
                mThisView.setText(str.replace("/r", "").replace("\n", ""));
                if (mNextView != null) {
                    /**
                     * If the jump control is not empty, let the next control get the focus,
                     * here you can directly implement the login function
                     */
                    mNextView.requestFocus();
                    if (mNextView instanceof Button) {
                        Button et = (Button) mNextView;
                        /**
                         *If the jump control is Button,
                         *then call click method
                         */
                        et.callOnClick();
                    }
                }
            }
        }
    }

}
