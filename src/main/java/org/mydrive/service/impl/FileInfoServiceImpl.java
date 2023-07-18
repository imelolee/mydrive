package org.mydrive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.mydrive.component.RedisComponent;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.UploadResultDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.mydrive.entity.enums.*;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.exception.BusinessException;
import org.mydrive.mappers.UserInfoMapper;
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

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

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
        if (StringTools.isEmpty(fileId)) {
            fileId = StringTools.getRandomNumber(Constants.LENGTH_10);
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