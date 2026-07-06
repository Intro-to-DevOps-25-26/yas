package com.yas.search.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessagesUtils {

    static ResourceBundle messageBundle = ResourceBundle.getBundle("messages.messages",
            Locale.getDefault());

    public static String getMessage(String errorCode, Object... var2) {
        String message;
        try {
            message = messageBundle.getString(errorCode);
        } catch (MissingResourceException ex) {
            // case message_code is not defined.
            message = errorCode;
        }
        return formatMessage(message, var2);
    }

    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        String formatted = message;
        for (Object arg : args) {
            formatted = formatted.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        return formatted;
    }
}
