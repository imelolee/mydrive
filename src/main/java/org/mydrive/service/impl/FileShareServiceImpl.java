package org.mydrive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionShareDto;
import org.mydrive.entity.enums.ResponseCodeEnum;
import org.mydrive.entity.enums.ShareValidTypeEnum;
import org.mydrive.exception.BusinessException;
import org.mydrive.utils.DateUtils;
import org.springframework.stereotype.Service;

import org.mydrive.entity.enums.PageSize;
import org.mydrive.entity.query.FileShareQuery;
import org.mydrive.entity.po.FileShare;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.query.SimplePage;
import org.mydrive.mappers.FileShareMapper;
import org.mydrive.service.FileShareService;
import org.mydrive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * FileShareService
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * findListByParam
     * @param param
     * @return
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery param) {
        return this.fileShareMapper.selectList(param);
    }

    /**
     * findCountByParam
     * @param param
     * @return
     */
    @Override
    public Integer findCountByParam(FileShareQuery param) {
        return this.fileShareMapper.selectCount(param);
    }

    /**
     * findListByPage
     * @param param
     * @return
     */
    @Override
    public PaginationResultVO<FileShare> findListByPage(FileShareQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileShare> list = this.findListByParam(param);
        PaginationResultVO<FileShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * add
     * @param bean
     * @return
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * addBatch
     * @param listBean
     * @return
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * addOrUpdateBatch
     * @param listBean
     * @return
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * updateByParam
     * @param bean
     * @param param
     * @return
     */
    @Override
    public Integer updateByParam(FileShare bean, FileShareQuery param) {
        StringTools.checkParam(param);
        return this.fileShareMapper.updateByParam(bean, param);
    }

    /**
     * deleteByParam
     * @param param
     * @return
     */
    @Override
    public Integer deleteByParam(FileShareQuery param) {
        StringTools.checkParam(param);
        return this.fileShareMapper.deleteByParam(param);
    }

    /**
     * getFileShareByShareId
     * @param shareId
     * @return
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * updateFileShareByShareId
     * @param bean
     * @param shareId
     * @return
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * deleteFileShareByShareId
     * @param shareId
     * @return
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    /**
     * saveShare
     * @param fileShare
     */
    @Override
    public void saveShare(FileShare fileShare) {
        ShareValidTypeEnum typeEnums = ShareValidTypeEnum.getByType(fileShare.getValidType());
        if (null == typeEnums) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (ShareValidTypeEnum.FOREVER != typeEnums) {
            fileShare.setExpireTime(DateUtils.getAfterDate(typeEnums.getDays()));
        }
        Date currentDate = new Date();
        fileShare.setShareTime(currentDate);
        if (StringTools.isEmpty(fileShare.getCode())) {
            fileShare.setCode(StringTools.getRandomString(Constants.LENGTH_5));
        }
        fileShare.setShareId(StringTools.getRandomString(Constants.LENGTH_20));
        fileShare.setShowCount(0);
        this.fileShareMapper.insert(fileShare);
    }

    /**
     * deleteFileShareBatch
     * @param shareIdArray
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileShareBatch(String[] shareIdArray, String userId) {
        Integer count = this.fileShareMapper.deleteFileShareBatch(shareIdArray, userId);
        if (count != shareIdArray.length) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * checkShareCode
     * @param shareId
     * @param code
     * @return
     */
    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare fileShare = this.fileShareMapper.selectByShareId(shareId);
        if (null == fileShare || (fileShare.getExpireTime() != null && new Date().after(fileShare.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        if (!fileShare.getCode().equals(code)) {
            throw new BusinessException("抽出コードエラー");
        }
        // update view count
        this.fileShareMapper.updateShareShowCount(shareId);
        SessionShareDto shareDto = new SessionShareDto();
        shareDto.setShareId(shareId);
        shareDto.setShareUserId(fileShare.getUserId());
        shareDto.setFileId(fileShare.getFileId());
        shareDto.setExpireTime(fileShare.getExpireTime());
        return shareDto;
    }
}