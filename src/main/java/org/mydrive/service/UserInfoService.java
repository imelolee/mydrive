package org.mydrive.service;

import java.util.List;

import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.springframework.stereotype.Service;


public interface UserInfoService {

    List<UserInfo> findListByParam(UserInfoQuery param);

    Integer findCountByParam(UserInfoQuery param);

    PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

    Integer add(UserInfo bean);

    Integer addBatch(List<UserInfo> listBean);

    Integer addOrUpdateBatch(List<UserInfo> listBean);

    Integer updateByParam(UserInfo bean, UserInfoQuery param);

    Integer deleteByParam(UserInfoQuery param);

    UserInfo getUserInfoByUserId(String userId);


    Integer updateUserInfoByUserId(UserInfo bean, String userId);


    Integer deleteUserInfoByUserId(String userId);


    UserInfo getUserInfoByEmail(String email);

    Integer updateUserInfoByEmail(UserInfo bean, String email);

    Integer deleteUserInfoByEmail(String email);


    UserInfo getUserInfoByNickName(String nickName);


    Integer updateUserInfoByNickName(UserInfo bean, String nickName);

    Integer deleteUserInfoByNickName(String nickName);

    void register(String email, String nickName, String password);

    SessionWebUserDto login(String email, String password);

    void resetPwd(String email, String password);
}