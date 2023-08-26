package org.mydrive.service;

import java.util.List;

import org.mydrive.entity.dto.SessionShareDto;
import org.mydrive.entity.query.FileShareQuery;
import org.mydrive.entity.po.FileShare;
import org.mydrive.entity.vo.PaginationResultVO;


public interface FileShareService {

    List<FileShare> findListByParam(FileShareQuery param);


    Integer findCountByParam(FileShareQuery param);

    PaginationResultVO<FileShare> findListByPage(FileShareQuery param);


    Integer add(FileShare bean);

    Integer addBatch(List<FileShare> listBean);


    Integer addOrUpdateBatch(List<FileShare> listBean);


    Integer updateByParam(FileShare bean, FileShareQuery param);


    Integer deleteByParam(FileShareQuery param);


    FileShare getFileShareByShareId(String shareId);


    Integer updateFileShareByShareId(FileShare bean, String shareId);


    Integer deleteFileShareByShareId(String shareId);

    void saveShare(FileShare fileShare);

    void deleteFileShareBatch(String[] shareIdArray, String userId);

    SessionShareDto checkShareCode(String shareId, String code);
}