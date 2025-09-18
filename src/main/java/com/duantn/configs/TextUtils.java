package com.duantn.configs;

import org.springframework.stereotype.Component;

@Component("textUtils")
public class TextUtils {

    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 4)
            return "******";
        return "*******" + phone.substring(phone.length() - 3);
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return "****";
        int index = email.indexOf("@");
        String name = email.substring(0, index);
        String domain = email.substring(index);
        int visibleChars = Math.max(1, name.length() / 2);
        return name.substring(0, visibleChars) + "***" + domain;
    }
}
