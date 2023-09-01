package org.mydrive.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    @Value("${project.folder}")
    private String projectFolder;

    public String getProjectFolder() {
        return projectFolder;
    }

}
