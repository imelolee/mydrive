package org.mydrive.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.mydrive.component.RedisComponent;
import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.UploadResultDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.mydrive.entity.enums.*;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.exception.BusinessException;
import org.mydrive.mappers.UserInfoMapper;
import org.mydrive.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.query.SimplePage;
import org.mydrive.mappers.FileInfoMapper;
import org.mydrive.service.FileInfoService;
import org.mydrive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件信息表 业务接口实现
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
    private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private AppConfig appConfig;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileInfo> list = this.findListByParam(param);
        PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(FileInfo bean) {
        return this.fileInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.deleteByParam(param);
    }

    /**
     * 根据FileId获取对象
     */
    @Override
    public FileInfo getFileInfoByFileId(String fileId) {
        return this.fileInfoMapper.selectByFileId(fileId);
    }

    /**
     * 根据FileId修改
     */
    @Override
    public Integer updateFileInfoByFileId(FileInfo bean, String fileId) {
        return this.fileInfoMapper.updateByFileId(bean, fileId);
    }

    /**
     * 根据FileId删除
     */
    @Override
    public Integer deleteFileInfoByFileId(String fileId) {
        return this.fileInfoMapper.deleteByFileId(fileId);
    }

    /**
     * 根据UserId获取对象
     */
    @Override
    public FileInfo getFileInfoByUserId(String userId) {
        return this.fileInfoMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId修改
     */
    @Override
    public Integer updateFileInfoByUserId(FileInfo bean, String userId) {
        return this.fileInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    @Override
    public Integer deleteFileInfoByUserId(String userId) {
        return this.fileInfoMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
                                      String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        UploadResultDto resultDto = new UploadResultDto();
        Boolean uploadSuccess = true;
        File tempFileFolder = null;

        try {

            if (StringTools.isEmpty(fileId)) {
                fileId = StringTools.getRandomString(Constants.LENGTH_10);
            }
            resultDto.setFileId(fileId);
            Date currentDate = new Date();
            UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());

            if (chunkIndex == 0) {
                FileInfoQuery infoQuery = new FileInfoQuery();
                infoQuery.setFileMd5(fileMd5);
                infoQuery.setSimplePage(new SimplePage(0, 1));
                infoQuery.setStatus(FileStatusEnum.USING.getStatus());
                List<FileInfo> dbFileList = this.fileInfoMapper.selectList(infoQuery);
                // 秒传
                if (!dbFileList.isEmpty()) {
                    FileInfo dbFile = dbFileList.get(0);
                    // 判断文件使用大小
                    if (dbFile.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }
                    dbFile.setFileId(fileId);
                    dbFile.setFilePid(filePid);
                    dbFile.setUserId(webUserDto.getUserId());
                    dbFile.setCreateTime(currentDate);
                    dbFile.setLastUpdateTime(currentDate);
                    dbFile.setStatus(FileStatusEnum.USING.getStatus());
                    dbFile.setDelFlag(FileDelFlagEnum.USING.getFlag());
                    dbFile.setFileMd5(fileMd5);
                    // 文件重命名
                    fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
                    dbFile.setFileName(fileName);
                    this.fileInfoMapper.insert(dbFile);

                    resultDto.setStatus(UploadStatusEnum.UPLOAD_SECONDS.getCode());
                    // 更新用户使用空间
                    updateUseSpace(webUserDto, dbFile.getFileSize());

                    return resultDto;
                }
            }

            // 判断磁盘空间
            Long currentTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            if (file.getSize() + currentTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }

            // 暂存临时目录
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = webUserDto.getUserId() + fileId;

            tempFileFolder = new File(tempFolderName + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }

            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnum.UPLOADING.getCode());
                // 保存临时大小
                redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
                return resultDto;
            }
            // 最后一个分片上传完成，记录数据库，异步合并
            String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
            String fileSuffix = StringTools.getFileNameSuffix(fileName);
            // 真实文件名
            String realFileName = currentUserFolderName + fileSuffix;
            FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            // 自动重命名
            fileName = autoRename(filePid, webUserDto.getUserId(), fileName);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setUserId(webUserDto.getUserId());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(fileName);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(currentDate);
            fileInfo.setLastUpdateTime(currentDate);
            fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnum.getType());
            fileInfo.setStatus(FileStatusEnum.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnum.FILE.getType());
            fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
            this.fileInfoMapper.insert(fileInfo);

            Long totalSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            updateUseSpace(webUserDto, totalSize);

            resultDto.setStatus(UploadStatusEnum.UPLOAD_FINISH.getCode());

            return resultDto;

        } catch (BusinessException e) {
            logger.error("文件上传失败", e);
            uploadSuccess = false;
            throw e;
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            uploadSuccess = false;
        } finally {
            if (!uploadSuccess && tempFileFolder != null) {
                try {
                    FileUtils.deleteDirectory(tempFileFolder);
                } catch (IOException e) {
                    logger.error("删除临时目录失败", e);
                }
            }
        }
        return resultDto;
    }

    /**
     * 文件重命名
     */
    private String autoRename(String filePid, String userId, String fileName) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfoQuery.setFileName(fileName);
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            fileName = StringTools.rename(fileName);
        }
        return fileName;
    }

    /**
     * 更新用户使用空间
     */
    public void updateUseSpace(SessionWebUserDto webUserDto, Long useSize) {
        Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSize, null);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
        spaceDto.setUseSpace(spaceDto.getUseSpace() + useSize);
        redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);

    }
}