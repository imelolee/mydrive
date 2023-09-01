package org.mydrive.service;

import java.util.List;

import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.UploadResultDto;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;


public interface FileInfoService {

    List<FileInfo> findListByParam(FileInfoQuery param);

    Integer findCountByParam(FileInfoQuery param);

    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    Integer add(FileInfo bean);

    Integer addBatch(List<FileInfo> listBean);

    Integer addOrUpdateBatch(List<FileInfo> listBean);

    Integer updateByParam(FileInfo bean, FileInfoQuery param);

    Integer deleteByParam(FileInfoQuery param);

    FileInfo getFileInfoByFileId(String fileId);

    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

    Integer updateFileInfoByFileId(FileInfo bean, String fileId);

    Integer deleteFileInfoByFileId(String fileId);

    FileInfo getFileInfoByUserId(String userId);

    Integer updateFileInfoByUserId(FileInfo bean, String userId);

    Integer deleteFileInfoByUserId(String userId);

    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
                               String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    FileInfo newFolder(String filePid, String userId, String folderName);

    FileInfo rename(String fileId, String userId, String fileName);

    void changeFileFolder(String fileIds, String filePid, String userId);

    void removeFile2RecyleBatch(String userId, String fileIds);

    void recoveryFileBatch(String userId, String fileIds);

    void delFileBatch(String userId, String fileIds, Boolean adminOp);

    void checkRootFilePid(String rootFilePid, String userId, String fileId);

    void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String currentUserId);


    void cleanExpiredFile();
}