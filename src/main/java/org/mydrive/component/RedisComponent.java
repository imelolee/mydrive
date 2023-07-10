package org.mydrive.component;

import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SysSettingsDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public SysSettingsDto getSysSettingsDto(){
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (null == sysSettingsDto){
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }
}
