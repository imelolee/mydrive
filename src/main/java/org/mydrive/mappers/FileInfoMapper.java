package org.mydrive.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {

    Integer updateByFileId(@Param("bean") T t, @Param("fileId") String fileId);

    Integer deleteByFileId(@Param("fileId") String fileId);

    T selectByFileId(@Param("fileId") String fileId);

    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

    Integer deleteByUserId(@Param("userId") String userId);

    T selectByUserId(@Param("userId") String userId);

    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    Long selectUseSpace(@Param("userId") String userId);

    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
                                       @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

    void updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

    void updateFileDelFlagBatch(@Param("bean") T fileInfo, @Param("userId") String userId,
                                @Param("filePidList") List<String> filePidList, @Param("fileIdList") List<String> fileIdList,
                                @Param("oldDelFlag") Integer oldDelFlag);

    void delFileBatch(@Param("userId") String userId, @Param("filePidList") List<String> filePidList,
                      @Param("fileIdList") List<String> fileIdList, @Param("oldDelFlag") Integer oldDelFlag);
}
