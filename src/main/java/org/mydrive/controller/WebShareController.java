package org.mydrive.controller;

import org.apache.ibatis.annotations.Param;
import org.mydrive.annotation.GlobalInterceptor;
import org.mydrive.annotation.VerifyParam;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionShareDto;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.enums.FileCategoryEnum;
import org.mydrive.entity.enums.FileDelFlagEnum;
import org.mydrive.entity.enums.ResponseCodeEnum;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.po.FileShare;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.entity.query.FileShareQuery;
import org.mydrive.entity.vo.FileInfoVO;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.vo.ResponseVO;
import org.mydrive.entity.vo.ShareInfoVO;
import org.mydrive.exception.BusinessException;
import org.mydrive.service.FileInfoService;
import org.mydrive.service.FileShareService;
import org.mydrive.service.UserInfoService;
import org.mydrive.utils.CopyTools;
import org.mydrive.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController("webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {
    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;


    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareLoginInfo(HttpSession session,
                                        @VerifyParam(required = true) String shareId) {
        SessionShareDto sessionShareDto = getShareFromSession(session, shareId);
        if (sessionShareDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        // 判断是否是当前用户分享的文件
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        if (webUserDto != null && webUserDto.getUserId().equals(sessionShareDto.getShareUserId())) {
            shareInfoVO.setCurrentUser(true);
        } else {
            shareInfoVO.setCurrentUser(false);
        }
        return getSuccessResponseVO(shareInfoVO);
    }

    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    private ShareInfoVO getShareInfoCommon(String shareId) {
        FileShare fileShare = fileShareService.getFileShareByShareId(shareId);
        if (null == fileShare || (fileShare.getExpireTime() != null && new Date().after(fileShare.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        ShareInfoVO shareInfoVO = CopyTools.copy(fileShare, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileShare.getFileId(), fileShare.getUserId());
        if (fileInfo == null || !FileDelFlagEnum.USING.getFlag().equals(fileInfo.getDelFlag())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(fileInfo.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        SessionShareDto sessionShareDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, sessionShareDto);


        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId,
                                   @VerifyParam(required = true) String filePid) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STRING.equals(filePid)) {
            fileInfoService.checkRootFilePid(sessionShareDto.getFileId(), sessionShareDto.getShareUserId(), filePid);
            fileInfoQuery.setFilePid(filePid);
        } else {
            fileInfoQuery.setFileId(sessionShareDto.getFileId());
        }
        fileInfoQuery.setUserId(sessionShareDto.getShareUserId());
        fileInfoQuery.setOrderBy("last_update_time desc");
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        PaginationResultVO<FileInfo> result = fileInfoService.findListByPage(fileInfoQuery);

        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto sessionShareDto = getShareFromSession(session, shareId);
        if (null == sessionShareDto) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        if (sessionShareDto.getExpireTime() != null && new Date().after(sessionShareDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return sessionShareDto;

    }

    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId, @VerifyParam(required = true) String path) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        return super.getFolderInfo(path, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/getFile/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getFile(HttpServletResponse response, HttpSession session,
                        @VerifyParam(required = true) @PathVariable("shareId") String shareId,
                        @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        super.getFile(response, fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getImage(HttpServletResponse response, HttpSession session,
                         @VerifyParam(required = true) @PathVariable("shareId") String shareId,
                         @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        super.getFile(response, fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @VerifyParam(required = true) @PathVariable("shareId") String shareId,
                                        @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);

        return super.createDownloadUrl(fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @VerifyParam(required = true) @PathVariable("code") String code) throws Exception {
        super.download(request, response, code);
    }

    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO saveShare(HttpSession session,
                          @VerifyParam(required = true) String shareId,
                          @VerifyParam(required = true) String shareFileIds,
                          @VerifyParam(required = true) String myFolderId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        if (sessionShareDto.getShareUserId().equals(webUserDto.getUserId())){
            throw new BusinessException("此文件已存在，无法保存");
        }
        fileInfoService.saveShare(sessionShareDto.getFileId(), shareFileIds, myFolderId, sessionShareDto.getShareUserId(), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
