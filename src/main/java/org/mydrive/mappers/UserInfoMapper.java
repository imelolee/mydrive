package org.mydrive.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

public interface UserInfoMapper<T, P> extends BaseMapper<T, P> {

    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);


    Integer deleteByUserId(@Param("userId") String userId);

    T selectByUserId(@Param("userId") String userId);


    Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

    Integer deleteByEmail(@Param("email") String email);


    T selectByEmail(@Param("email") String email);

    Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);


    Integer deleteByQqOpenId(@Param("qqOpenId") String qqOpenId);

    T selectByQqOpenId(@Param("qqOpenId") String qqOpenId);

    Integer updateByNickName(@Param("bean") T t, @Param("nickName") String nickName);

    Integer deleteByNickName(@Param("nickName") String nickName);

    T selectByNickName(@Param("nickName") String nickName);

    Integer updateUserSpace(@Param("userId") String userId, @Param("useSpace") Long useSpace, @Param("totalSpace") Long totalSpace);
}
