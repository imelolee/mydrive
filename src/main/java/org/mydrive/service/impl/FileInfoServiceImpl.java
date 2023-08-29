package org.mydrive.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.server.ExportException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
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
import org.mydrive.utils.DateUtils;
import org.mydrive.utils.ProcessUtils;
import org.mydrive.utils.ScaleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.query.SimplePage;
import org.mydrive.mappers.FileInfoMapper;
import org.mydrive.service.FileInfoService;
import org.mydrive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;


/**
 * FileInfoService
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

    @Resource
    @Lazy
    private FileInfoServiceImpl fileInfoService;

    /**
     * findListByParam
     *
     * @param param
     * @return
     */
    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectList(param);
    }

    /**
     * findCountByParam
     *
     * @param param
     * @return
     */
    @Override
    public Integer findCountByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectCount(param);
    }

    /**
     * findListByPage
     *
     * @param param
     * @return
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
     * add
     *
     * @param bean
     * @return
     */
    @Override
    public Integer add(FileInfo bean) {
        return this.fileInfoMapper.insert(bean);
    }

    /**
     * addBatch
     *
     * @param listBean
     * @return
     */
    @Override
    public Integer addBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertBatch(listBean);
    }

    /**
     * addOrUpdateBatch
     *
     * @param listBean
     * @return
     */
    @Override
    public Integer addOrUpdateBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * updateByParam
     *
     * @param bean
     * @param param
     * @return
     */
    @Override
    public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.updateByParam(bean, param);
    }

    /**
     * deleteByParam
     *
     * @param param
     * @return
     */
    @Override
    public Integer deleteByParam(FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.deleteByParam(param);
    }

    /**
     * getFileInfoByFileId
     *
     * @param fileId
     * @return
     */
    @Override
    public FileInfo getFileInfoByFileId(String fileId) {
        return this.fileInfoMapper.selectByFileId(fileId);
    }

    /**
     * getFileInfoByFileIdAndUserId
     *
     * @param fileId
     * @param userId
     * @return
     */
    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * updateFileInfoByFileId
     *
     * @param bean
     * @param fileId
     * @return
     */
    @Override
    public Integer updateFileInfoByFileId(FileInfo bean, String fileId) {
        return this.fileInfoMapper.updateByFileId(bean, fileId);
    }

    /**
     * deleteFileInfoByFileId
     *
     * @param fileId
     * @return
     */
    @Override
    public Integer deleteFileInfoByFileId(String fileId) {
        return this.fileInfoMapper.deleteByFileId(fileId);
    }

    /**
     * getFileInfoByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public FileInfo getFileInfoByUserId(String userId) {
        return this.fileInfoMapper.selectByUserId(userId);
    }

    /**
     * updateFileInfoByUserId
     *
     * @param bean
     * @param userId
     * @return
     */
    @Override
    public Integer updateFileInfoByUserId(FileInfo bean, String userId) {
        return this.fileInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * deleteFileInfoByUserId
     *
     * @param userId
     * @return
     */
    @Override
    public Integer deleteFileInfoByUserId(String userId) {
        return this.fileInfoMapper.deleteByUserId(userId);
    }


    /**
     * uploadFile
     *
     * @param webUserDto
     * @param fileId
     * @param file
     * @param fileName
     * @param filePid
     * @param fileMd5
     * @param chunkIndex
     * @param chunks
     * @return
     */
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
                // fast upload
                if (!dbFileList.isEmpty()) {
                    FileInfo dbFile = dbFileList.get(0);
                    // get file size already used
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
                    // rename file
                    fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
                    dbFile.setFileName(fileName);
                    this.fileInfoMapper.insert(dbFile);

                    resultDto.setStatus(UploadStatusEnum.UPLOAD_SECONDS.getCode());
                    // update user's space
                    updateUseSpace(webUserDto, dbFile.getFileSize());

                    return resultDto;
                }
            }

            // get temp size
            Long currentTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            if (file.getSize() + currentTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }

            // get current temp folder
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = webUserDto.getUserId() + fileId;

            tempFileFolder = new File(tempFolderName + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }

            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            // save temp size
            redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnum.UPLOADING.getCode());

                return resultDto;
            }
            redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());

            // update and union when the last part upload success
            String month = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
            String fileSuffix = StringTools.getFileNameSuffix(fileName);
            // real filename
            String realFileName = currentUserFolderName + fileSuffix;
            FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            // auto rename
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
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileInfo.getFileId(), webUserDto);
                }
            });


            return resultDto;

        } catch (BusinessException e) {
            logger.error("File upload failed", e);
            uploadSuccess = false;
            throw e;
        } catch (Exception e) {
            logger.error("File upload failed", e);
            uploadSuccess = false;
        } finally {
            if (!uploadSuccess && tempFileFolder != null) {
                try {
                    FileUtils.deleteDirectory(tempFileFolder);
                } catch (IOException e) {
                    logger.error("Delete temp folder failed", e);
                }
            }
        }
        return resultDto;
    }

    /**
     * autoRename
     *
     * @param filePid
     * @param userId
     * @param fileName
     * @return
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
     * updateUseSpace
     *
     * @param webUserDto
     * @param useSize
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

    /**
     * transferFile
     *
     * @param fileId
     * @param webUserDto
     */
    @Async
    public void transferFile(String fileId, SessionWebUserDto webUserDto) {
        Boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileTypeEnum fileTypeEnum = null;
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, webUserDto.getUserId());
        try {
            if (fileInfo == null || !FileStatusEnum.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            // temp folder
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = webUserDto.getUserId() + fileId;
            File fileFolder = new File(tempFolderName + currentUserFolderName);

            String fileSuffix = StringTools.getFileNameSuffix(fileInfo.getFileName());
            String month = DateUtils.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
            // target folder
            String targetFolerName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFolder = new File(targetFolerName + "/" + month);
            if (targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            // real filename
            String realFileName = currentUserFolderName + fileSuffix;
            targetFilePath = targetFolder.getPath() + "/" + realFileName;

            // union all files
            union(fileFolder.getPath(), targetFilePath, fileInfo.getFileName(), true);
            // cut the video
            fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            if (FileTypeEnum.VIDEO == fileTypeEnum) {
                cutFile4Video(fileId, targetFilePath);
                // get video cover
                cover = month + "/" + currentUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolerName + "/" + cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (FileTypeEnum.IMAGE == fileTypeEnum) {
                // get image cover
                cover = month + "/" + realFileName.replace(".", "_.");
                String coverPath = targetFolerName + "/" + cover;
                Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath), false);
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            logger.error("File transfer failed, fileId {}, userId {}", fileId, webUserDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFileSize(new File(targetFilePath).length());
            updateInfo.setFileCover(cover);
            updateInfo.setStatus(transferSuccess ? FileStatusEnum.USING.getStatus() : FileStatusEnum.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId, webUserDto.getUserId(), updateInfo, FileStatusEnum.TRANSFER.getStatus());
        }
    }

    /**
     * union
     *
     * @param dirPath
     * @param toFilePath
     * @param fileName
     * @param delSource
     */
    private void union(String dirPath, String toFilePath, String fileName, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("フォルダが存在しません");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdir();
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                File chunkFile = new File(dirPath + "/" + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    logger.error("File merge failed", e);
                    throw new BusinessException("ファイルのマージに失敗しました");
                } finally {
                    readFile.close();
                }
            }
        } catch (Exception e) {
            logger.error("File {} merge failed", fileName, e);
            throw new BusinessException("ファイル" + fileName + "のマージに失敗しました");
        } finally {
            if (null != writeFile) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (delSource && dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * cutFile4Video
     *
     * @param fileId
     * @param videoFilePath
     */
    private void cutFile4Video(String fileId, String videoFilePath) {
        // create folder with the same filename
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder + "/" + Constants.TS_NAME;
        // generate .ts
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        // generate index .m3u8 and .ts
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath(), fileId);
        ProcessUtils.executeCommand(cmd, false);
        // delete index.tx
        new File(tsPath).delete();

    }

    /**
     * newFolder
     *
     * @param filePid
     * @param userId
     * @param folderName
     * @return
     */
    @Override
    public FileInfo newFolder(String filePid, String userId, String folderName) {
        checkFileName(filePid, userId, folderName, FileFolderTypeEnum.FOLDER.getType());
        Date currentDate = new Date();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(StringTools.getRandomString(Constants.LENGTH_10));
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileName(folderName);
        fileInfo.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        fileInfo.setCreateTime(currentDate);
        fileInfo.setLastUpdateTime(currentDate);
        fileInfo.setStatus(FileStatusEnum.USING.getStatus());
        fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
        this.fileInfoMapper.insert(fileInfo);

        return fileInfo;
    }

    /**
     * checkFileName
     *
     * @param filePid
     * @param userId
     * @param fileName
     * @param folderType
     */
    private void checkFileName(String filePid, String userId, String fileName, Integer folderType) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFolderType(folderType);
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());

        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            throw new BusinessException("このディレクトリに同じ名前のファイルが存在します，名前を変更してください");
        }
    }

    /**
     * rename rename
     *
     * @param fileId
     * @param userId
     * @param fileName
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo rename(String fileId, String userId, String fileName) {
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (null == fileInfo) {
            throw new BusinessException("ファイルが存在しません");
        }

        String filePid = fileInfo.getFilePid();
        checkFileName(filePid, userId, fileName, fileInfo.getFolderType());
        // get file suffix
        if (FileFolderTypeEnum.FILE.getType().equals(fileInfo.getFolderType())) {
            fileName = fileName + StringTools.getFileNameSuffix(fileInfo.getFileName());
        }
        Date currentDate = new Date();
        FileInfo dbInfo = new FileInfo();
        dbInfo.setFileName(fileName);
        dbInfo.setLastUpdateTime(currentDate);
        this.fileInfoMapper.updateByFileIdAndUserId(dbInfo, fileId, userId);
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 1) {
            throw new BusinessException("ファイル" + fileName + "が存在します");
        }
        fileInfo.setFileName(fileName);
        fileInfo.setLastUpdateTime(currentDate);

        return fileInfo;
    }

    /**
     * changeFileFolder
     *
     * @param fileIds
     * @param filePid
     * @param userId
     */
    @Override
    public void changeFileFolder(String fileIds, String filePid, String userId) {
        if (fileIds.equals(filePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (!Constants.ZERO_STRING.equals(filePid)) {
            FileInfo fileInfo = this.getFileInfoByFileIdAndUserId(filePid, userId);
            if (null == fileInfo || !FileDelFlagEnum.USING.getFlag().equals(fileInfo.getDelFlag())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }
        String[] fileIdArray = fileIds.split(",");

        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        List<FileInfo> dbFileList = this.findListByParam(fileInfoQuery);
        Map<String, FileInfo> dbFileNameMap = dbFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (data1, data2) -> data2));
        // query selected file
        fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(fileIdArray);
        List<FileInfo> selectFileList = this.findListByParam(fileInfoQuery);
        // rename selected file
        for (FileInfo item : selectFileList) {
            Date currentDate = new Date();
            FileInfo rootFileInfo = dbFileNameMap.get(item.getFileName());
            // if file with same name exists, rename the file
            FileInfo updateInfo = new FileInfo();
            if (rootFileInfo != null) {
                String fileName = StringTools.rename(item.getFileName());
                updateInfo.setFileName(fileName);
            }
            updateInfo.setFilePid(filePid);
            updateInfo.setLastUpdateTime(currentDate);
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, item.getFileId(), userId);
        }
    }

    /**
     * removeFile2RecyleBatch
     *
     * @param userId
     * @param fileIds
     */
    @Override
    public void removeFile2RecyleBatch(String userId, String fileIds) {
        String[] fileIdArray = fileIds.split(",");
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(fileIdArray);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);
        if (fileInfoList.isEmpty()) {
            return;
        }
        List<String> delFilePidList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            // put the file and its children file into recycle
            findAllSubFolderFileList(delFilePidList, userId, fileInfo.getFileId(), FileDelFlagEnum.USING.getFlag());
        }
        if (!delFilePidList.isEmpty()) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setRecoveryTime(new Date());
            updateInfo.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
            this.fileInfoMapper.updateFileDelFlagBatch(updateInfo, userId, delFilePidList, null, FileDelFlagEnum.USING.getFlag());
        }
        // put selected file into recycle
        List<String> delFileId = Arrays.asList(fileIdArray);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRecoveryTime(new Date());
        fileInfo.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFilePidList, FileDelFlagEnum.USING.getFlag());
    }

    /**
     * findAllSubFolderFileList
     *
     * @param fileIdList
     * @param userId
     * @param fileId
     * @param delFlag
     */
    private void findAllSubFolderFileList(List<String> fileIdList, String userId, String fileId, Integer delFlag) {
        fileIdList.add(fileId);
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(fileId);
        fileInfoQuery.setDelFlag(delFlag);
        fileInfoQuery.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileList(fileIdList, userId, fileInfo.getFileId(), delFlag);
        }
    }

    /**
     * recoveryFileBatch
     *
     * @param userId
     * @param fileIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoveryFileBatch(String userId, String fileIds) {
        String[] fileIdArray = fileIds.split(",");
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(fileIdArray);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);
        List<String> delFileSubFolderFileList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())) {
                findAllSubFolderFileList(delFileSubFolderFileList, userId, fileInfo.getFileId(), FileDelFlagEnum.RECYCLE.getFlag());
            }
        }
        // get all file at root folder
        fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(Constants.ZERO_STRING);
        List<FileInfo> allRootFileList = this.findListByParam(fileInfoQuery);

        Map<String, FileInfo> rootFileMap = allRootFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (data1, data2) -> data2));

        // update all selected file as "using"
        if (!delFileSubFolderFileList.isEmpty()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
            this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, delFileSubFolderFileList, null, FileDelFlagEnum.RECYCLE.getFlag());
        }
        // update selected file as "using" and move to root folder
        List<String> delFileIdList = Arrays.asList(fileIdArray);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfo.setFilePid(Constants.ZERO_STRING);
        fileInfo.setLastUpdateTime(new Date());
        fileInfo.setRecoveryTime(null);
        this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnum.RECYCLE.getFlag());

        // rename selected file
        for (FileInfo item : fileInfoList) {
            FileInfo rootFileInfo = rootFileMap.get(item.getFileName());
            // if file with the same name exists
            if (rootFileInfo != null) {
                String fileName = StringTools.rename(item.getFileName());
                FileInfo updateInfo = new FileInfo();
                updateInfo.setFileName(fileName);
                this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, item.getFileId(), userId);
            }
        }

    }

    /**
     * delFileBatch
     *
     * @param userId
     * @param fileIds
     * @param adminOp
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFileBatch(String userId, String fileIds, Boolean adminOp) {
        String[] fileIdArray = fileIds.split(",");
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(fileIdArray);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(fileInfoQuery);

        // delete local storage file
        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo.getFolderType() == 0) {
                delLocalFile(fileInfo);
            }
        }

        List<String> delFileSubFolderFileIdList = new ArrayList<>();
        // get child folder id
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())) {
                findAllSubFolderFileList(delFileSubFolderFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnum.RECYCLE.getFlag());
            }
        }

        FileInfo delFileInfo = new FileInfo();
        delFileInfo.setDelFlag(FileDelFlagEnum.DEL.getFlag());

        // delete file in the child folder
        if (!delFileSubFolderFileIdList.isEmpty()) {
            this.fileInfoMapper.updateFileDelFlagBatch(delFileInfo, userId, delFileSubFolderFileIdList, null, FileDelFlagEnum.RECYCLE.getFlag());
        }
        // delete selected folder
        this.fileInfoMapper.updateFileDelFlagBatch(delFileInfo, userId, null, Arrays.asList(fileIdArray), FileDelFlagEnum.RECYCLE.getFlag());
        // update use space
        Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        UserInfo userInfo = new UserInfo();
        userInfo.setUseSpace(useSpace);
        this.userInfoMapper.updateByUserId(userInfo, userId);
        // set cache
        UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(userId);
        userSpaceDto.setUseSpace(useSpace);
        redisComponent.saveUserSpaceUse(userId, userSpaceDto);

    }

    /**
     * delLocalFile
     *
     * @param fileInfo
     */
    void delLocalFile(FileInfo fileInfo) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(fileInfo.getUserId());
        fileInfoQuery.setFileMd5(fileInfo.getFileMd5());
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        // delete only one file with the same md5
        if (count <= 1) {
            if (fileInfo.getFileCategory() == 1) {
                // delete video & cover & part files
                try {
                    FileUtils.deleteQuietly(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath()));
                    FileUtils.deleteQuietly(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFileCover()));
                    FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + StringTools.getFileNameNoSuffix(fileInfo.getFilePath())));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    if (fileInfo.getFileCover() != null) {
                        FileUtils.deleteQuietly(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFileCover()));
                    }
                    FileUtils.deleteQuietly(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * checkRootFilePid
     *
     * @param rootFilePid
     * @param userId
     * @param fileId
     */
    @Override
    public void checkRootFilePid(String rootFilePid, String userId, String fileId) {
        if (StringTools.isEmpty(fileId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (rootFilePid.equals(fileId)) {
            return;
        }
        checkFilePid(rootFilePid, fileId, userId);
    }

    /**
     * checkFilePid
     *
     * @param rootFilePid
     * @param fileId
     * @param userId
     */
    private void checkFilePid(String rootFilePid, String fileId, String userId) {
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (null == fileInfo) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (Constants.ZERO_STRING.equals(fileInfo.getFilePid())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (fileInfo.getFilePid().equals(rootFilePid)) {
            return;
        }
        checkFilePid(rootFilePid, fileInfo.getFilePid(), userId);
    }

    /**
     * saveShare
     *
     * @param shareRootFilePid
     * @param shareFileIds
     * @param myFolderId
     * @param shareUserId
     * @param currentUserId
     */
    @Override
    public void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String currentUserId) {
        String[] shareFileIdArray = shareFileIds.split(",");
        // target file list
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(currentUserId);
        fileInfoQuery.setFilePid(myFolderId);
        List<FileInfo> currentFileList = this.fileInfoMapper.selectList(fileInfoQuery);
        Map<String, FileInfo> currentFileMap = currentFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (data1, data2) -> data2));
        // selected file
        fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(shareUserId);
        fileInfoQuery.setFileIdArray(shareFileIdArray);
        List<FileInfo> shareFileList = this.fileInfoMapper.selectList(fileInfoQuery);
        // rename selected file
        List<FileInfo> copyFileList = new ArrayList<>();
        Date currentDate = new Date();
        for (FileInfo item : shareFileList) {
            FileInfo haveFile = currentFileMap.get(item.getFileName());
            if (haveFile != null) {
                item.setFileName(StringTools.rename(item.getFileName()));
            }
            findAllSubFile(copyFileList, item, shareUserId, currentUserId, currentDate, myFolderId);
        }
        this.fileInfoMapper.insertBatch(copyFileList);
    }

    /**
     * findAllSubFile
     *
     * @param copyFileList
     * @param fileInfo
     * @param sourceUserId
     * @param currentUserId
     * @param currentDate
     * @param newFilePid
     */
    private void findAllSubFile(List<FileInfo> copyFileList, FileInfo fileInfo, String sourceUserId,
                                String currentUserId, Date currentDate, String newFilePid) {
        String sourceFileId = fileInfo.getFileId();
        fileInfo.setCreateTime(currentDate);
        fileInfo.setLastUpdateTime(currentDate);
        fileInfo.setFilePid(newFilePid);
        fileInfo.setUserId(currentUserId);
        String newFileId = StringTools.getRandomString(Constants.LENGTH_10);
        fileInfo.setFileId(newFileId);
        copyFileList.add(fileInfo);
        if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())) {
            FileInfoQuery fileInfoQuery = new FileInfoQuery();
            fileInfoQuery.setFilePid(sourceFileId);
            fileInfoQuery.setUserId(sourceUserId);
            List<FileInfo> sourceFileList = this.fileInfoMapper.selectList(fileInfoQuery);
            for (FileInfo item : sourceFileList) {
                findAllSubFile(copyFileList, item, sourceUserId, currentUserId, currentDate, newFileId);
            }
        }
    }

    @Override
    public void cleanExpiredFile() {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        fileInfoQuery.setQueryExpire(true);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
        Map<String, List<FileInfo>> fileInfoMap = fileInfoList.stream().collect(Collectors.groupingBy(FileInfo::getUserId));
        for (Map.Entry<String, List<FileInfo>> entry : fileInfoMap.entrySet()) {
            List<String> fileIds = entry.getValue().stream().map(p -> p.getFileId()).collect(Collectors.toList());
            fileInfoService.delFileBatch(entry.getKey(), String.join(",", fileIds), false);
        }
    }

}