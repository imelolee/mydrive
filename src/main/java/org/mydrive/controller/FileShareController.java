package org.mydrive.controller;

import org.mydrive.annotation.GlobalInterceptor;
import org.mydrive.annotation.VerifyParam;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.query.FileShareQuery;
import org.mydrive.entity.po.FileShare;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.vo.ResponseVO;
import org.mydrive.service.FileShareService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 分享信息 Controller
 */
@RestController("fileShareController")
@RequestMapping("/share")
public class FileShareController extends ABaseController {

    @Resource
    private FileShareService fileShareService;

    @RequestMapping("/loadShareList")
    @GlobalInterceptor
    public ResponseVO loadShareList(HttpSession session, FileShareQuery query) {
        query.setOrderBy("share_time desc");
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        query.setUserId(webUserDto.getUserId());
        query.setQueryFileName(true);

        PaginationResultVO<FileShare> result = fileShareService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO shareFile(HttpSession session,
                                @VerifyParam(required = true) String fileId,
                                @VerifyParam(required = true) Integer validType,
                                String code) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileShare fileShare = new FileShare();
        fileShare.setValidType(validType);
        fileShare.setFileId(fileId);
        fileShare.setCode(code);
        fileShare.setUserId(webUserDto.getUserId());
        fileShareService.saveShare(fileShare);
        return getSuccessResponseVO(fileShare);
    }

    @RequestMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO cancelShare(HttpSession session,
                                  @VerifyParam(required = true) String shareIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileShareService.deleteFileShareBatch(shareIds.split(","), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

}