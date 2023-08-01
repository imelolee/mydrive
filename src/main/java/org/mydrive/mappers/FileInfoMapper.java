package org.mydrive.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件信息表 数据库操作接口
 */
public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据FileId更新
     */
    Integer updateByFileId(@Param("bean") T t, @Param("fileId") String fileId);


    /**
     * 根据FileId删除
     */
    Integer deleteByFileId(@Param("fileId") String fileId);


    /**
     * 根据FileId获取对象
     */
    T selectByFileId(@Param("fileId") String fileId);


    /**
     * 根据UserId更新
     */
    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);


    /**
     * 根据UserId删除
     */
    Integer deleteByUserId(@Param("userId") String userId);


    /**
     * 根据UserId获取对象
     */
    T selectByUserId(@Param("userId") String userId);

    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    Long selectUseSpace(@Param("userId") String userId);

    void updateByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId, @Param("bean") T t);

    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
                                       @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

    void updateByFileIdAndUserId(@Param("bean") T dbInfo, @Param("fileId") String fileId, @Param("userId") String userId);

    void updateFileDelFlagBatch(@Param("bean") T fileInfo, @Param("userId") String userId,
                                @Param("filePidList") List<String> filePidList, @Param("fileIdList") List<String> fileIdList,
                                @Param("oldDelFlag") Integer oldDelFlag);

    void delFileBatch(@Param("userId") String userId, @Param("filePidList") List<String> filePidList,
                      @Param("fileIdList") List<String> fileIdList, @Param("oldDelFlag") Integer oldDelFlag);
}
