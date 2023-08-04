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
 * 分享信息 业务接口实现
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery param) {
        return this.fileShareMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(FileShareQuery param) {
        return this.fileShareMapper.selectCount(param);
    }

    /**
     * 分页查询方法
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
     * 新增
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(FileShare bean, FileShareQuery param) {
        StringTools.checkParam(param);
        return this.fileShareMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(FileShareQuery param) {
        StringTools.checkParam(param);
        return this.fileShareMapper.deleteByParam(param);
    }

    /**
     * 根据ShareId获取对象
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * 根据ShareId修改
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * 根据ShareId删除
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    /**
     * 保存分享
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
     * 批量删除分享链接
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
     * 检查分享提取码
     */
    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare fileShare = this.fileShareMapper.selectByShareId(shareId);
        if (null == fileShare || (fileShare.getExpireTime() != null && new Date().after(fileShare.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        if (!fileShare.getCode().equals(code)) {
            throw new BusinessException("提取码错误");
        }
        // 更新浏览次数
        this.fileShareMapper.updateShareShowCount(shareId);
        SessionShareDto shareDto = new SessionShareDto();
        shareDto.setShareId(shareId);
        shareDto.setShareUserId(fileShare.getUserId());
        shareDto.setFileId(fileShare.getFileId());
        shareDto.setExpireTime(fileShare.getExpireTime());
        return shareDto;
    }
}