package org.mydrive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.mydrive.component.RedisComponent;
import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.SysSettingsDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.mydrive.entity.enums.UserStatusEnum;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.exception.BusinessException;
import org.mydrive.mappers.FileInfoMapper;
import org.springframework.stereotype.Service;

import org.mydrive.entity.enums.PageSize;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.query.SimplePage;
import org.mydrive.mappers.UserInfoMapper;
import org.mydrive.service.UserInfoService;
import org.mydrive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * UserInfoService
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private AppConfig appConfig;

    /**
     * findListByParam
     *
     * @param param
     * @return
     */
    @Override
    public List<UserInfo> findListByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectList(param);
    }

    /**
     * findCountByParam
     *
     * @param param
     * @return
     */
    @Override
    public Integer findCountByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectCount(param);
    }

    /**
     * findListByPage
     *
     * @param param
     * @return
     */
    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(param);
        PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * add
     *
     * @param bean
     * @return
     */
    @Override
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    /**
     * addBatch
     *
     * @param listBean
     * @return
     */
    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    /**
     * addOrUpdateBatch
     *
     * @param listBean
     * @return
     */
    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * updateByParam
     *
     * @param bean
     * @param param
     * @return
     */
    @Override
    public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
        StringTools.checkParam(param);
        return this.userInfoMapper.updateByParam(bean, param);
    }

    /**
     * deleteByParam
     *
     * @param param
     * @return
     */
    @Override
    public Integer deleteByParam(UserInfoQuery param) {
        StringTools.checkParam(param);
        return this.userInfoMapper.deleteByParam(param);
    }

    /**
     * getUserInfoByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * updateUserInfoByUserId
     *
     * @param bean
     * @param userId
     * @return
     */
    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * deleteUserInfoByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public Integer deleteUserInfoByUserId(String userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }

    /**
     * getUserInfoByEmail
     *
     * @param email
     * @return
     */
    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    /**
     * updateUserInfoByEmail
     *
     * @param bean
     * @param email
     * @return
     */
    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    /**
     * deleteUserInfoByEmail
     *
     * @param email
     * @return
     */
    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * getUserInfoByNickName
     * @param nickName
     * @return
     */
    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return this.userInfoMapper.selectByNickName(nickName);
    }

    /**
     * updateUserInfoByNickName
     * @param bean
     * @param nickName
     * @return
     */
    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return this.userInfoMapper.updateByNickName(bean, nickName);
    }

    /**
     * deleteUserInfoByNickName
     * @param nickName
     * @return
     */
    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return this.userInfoMapper.deleteByNickName(nickName);
    }

    /**
     * register
     * @param email
     * @param nickName
     * @param password
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null != userInfo) {
            throw new BusinessException("メールは既に存在します");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
        if (null != nickNameUser) {
            throw new BusinessException("ユーザー名は既に存在します");
        }
        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setUserId(userId);
        newUserInfo.setNickName(nickName);
        newUserInfo.setEmail(email);
        newUserInfo.setPassword(StringTools.encodeByMd5(password));
        newUserInfo.setJoinTime(new Date());
        newUserInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        newUserInfo.setUseSpace(0L);

        SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
        newUserInfo.setTotalSpace(sysSettingsDto.getUserInitUsespace() * Constants.MB);

        this.userInfoMapper.insert(newUserInfo);
    }

    /**
     * login
     * @param email
     * @param password
     * @return
     */
    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null == userInfo || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("メールアドレスまたはパスワードが間違っています");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("アカウントが無効になっています");
        }
        // update last login time
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());

        this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());

        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());


        // user space info
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
        userSpaceDto.setUseSpace(useSpace);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);

        return sessionWebUserDto;
    }

    /**
     * resetPwd
     * @param email
     * @param password
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null == userInfo) {
            throw new BusinessException("メールが存在しません");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringTools.encodeByMd5(password));
        this.userInfoMapper.updateByEmail(updateInfo, email);
    }
}