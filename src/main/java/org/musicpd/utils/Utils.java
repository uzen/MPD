package org.musicpd.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class Utils {

    public static class PortNumberFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart) +
                        source.subSequence(start, end) +
                        destTxt.substring(dend);
                try {
                    int port = Integer.parseInt(resultingTxt);
                    if (port > 65535) {
                        return "";
                    }
                    if (port < 1) {
                        return "";
                    }
                } catch (NumberFormatException e) {
                    return "";
                }
            }
            return null;
        }
    }
}