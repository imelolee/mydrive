package org.mydrive.mappers;

import org.apache.ibatis.annotations.Param;

public interface FileShareMapper<T, P> extends BaseMapper<T, P> {

    Integer updateByShareId(@Param("bean") T t, @Param("shareId") String shareId);

    Integer deleteByShareId(@Param("shareId") String shareId);

    T selectByShareId(@Param("shareId") String shareId);

    Integer deleteFileShareBatch(@Param("shareIdArray") String[] shareIdArray, @Param("userId") String userId);

    void updateShareShowCount(@Param("shareId") String shareId);


}
