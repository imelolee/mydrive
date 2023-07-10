package org.mydrive.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingsDto implements Serializable {
    private Integer userInitUsespace = 5;

    public Integer getUserInitUsespace() {
        return userInitUsespace;
    }

    public void setUserInitUsespace(Integer userInitUsespace) {
        this.userInitUsespace = userInitUsespace;
    }
}
