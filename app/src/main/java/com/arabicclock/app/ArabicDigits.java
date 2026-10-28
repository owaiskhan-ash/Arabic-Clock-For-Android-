package com.arabicclock.app;

final class ArabicDigits {
    static String convert(String value) {
        StringBuilder result = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            if (c >= '0' && c <= '9') c = (char) ('٠' + c - '0');
            result.append(c);
        }
        return result.toString();
    }
}
