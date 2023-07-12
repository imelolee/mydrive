package org.mydrive.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {
    @Value("meloligen@gmail.com")
    private String sendUserName;

    @Value("${admin.emails}")
    private String adminEmails;

    public String getAdminEmails() {
        return adminEmails;
    }

    public String getSendUserName() {
        return sendUserName;
    }
}
