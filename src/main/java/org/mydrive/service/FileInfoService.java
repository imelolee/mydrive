package org.mydrive.service;

import java.util.List;

import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.UploadResultDto;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件信息表 业务接口
 */
public interface FileInfoService {

    /**
     * 根据条件查询列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileInfoQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    /**
     * 新增
     */
    Integer add(FileInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileInfo> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<FileInfo> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(FileInfo bean, FileInfoQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(FileInfoQuery param);

    /**
     * 根据FileId查询对象
     */
    FileInfo getFileInfoByFileId(String fileId);

    /**
     * 根据FileId和UserId获取对象
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);


    /**
     * 根据FileId修改
     */
    Integer updateFileInfoByFileId(FileInfo bean, String fileId);


    /**
     * 根据FileId删除
     */
    Integer deleteFileInfoByFileId(String fileId);


    /**
     * 根据UserId查询对象
     */
    FileInfo getFileInfoByUserId(String userId);


    /**
     * 根据UserId修改
     */
    Integer updateFileInfoByUserId(FileInfo bean, String userId);


    /**
     * 根据UserId删除
     */
    Integer deleteFileInfoByUserId(String userId);


    /**
     * 文件上传
     */
    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
                               String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

}