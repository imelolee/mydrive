package org.mydrive.component;

import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SysSettingsDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

@Component("redisComponent")
public class RedisComponent{
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

    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EX_ONE_DAY);
    }

    public UserSpaceDto getUserSpaceUse(String userId){
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (spaceDto == null) {
            spaceDto = new UserSpaceDto();
            // TODO 查询用户已使用空间大小
            spaceDto.setUseSpace(0L);
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUsespace() * Constants.MB);
            saveUserSpaceUse(userId, spaceDto);
        }
        return spaceDto;
    }

}
