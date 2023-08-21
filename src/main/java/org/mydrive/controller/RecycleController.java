package org.mydrive.controller;

import org.mydrive.annotation.GlobalInterceptor;
import org.mydrive.annotation.VerifyParam;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.enums.FileCategoryEnum;
import org.mydrive.entity.enums.FileDelFlagEnum;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.vo.FileInfoVO;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.vo.ResponseVO;
import org.mydrive.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) { 
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setPageSize(pageSize);
        fileInfoQuery.setPageNo(pageNo);
        fileInfoQuery.setUserId(getUserInfoFromSession(session).getUserId());
        fileInfoQuery.setOrderBy("recovery_time desc");
        fileInfoQuery.setFolderType(0);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        PaginationResultVO result = fileInfoService.findListByPage(fileInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    @RequestMapping("/recoverFile")
    @GlobalInterceptor
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.recoveryFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds, false);
        return getSuccessResponseVO(null);
    }

}
