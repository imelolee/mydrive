package org.mydrive.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.mydrive.annotation.GlobalInterceptor;
import org.mydrive.annotation.VerifyParam;
import org.mydrive.component.RedisComponent;
import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.CreateImageCode;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.mydrive.entity.enums.VerifyRegexEnum;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.vo.ResponseVO;
import org.mydrive.exception.BusinessException;
import org.mydrive.service.UserInfoService;
import org.mydrive.utils.StringTools;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户信息表 Controller
 */
@RestController("userInfoController")

public class AccountController extends ABaseController {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private AppConfig appConfig;


    /**
     * 获取验证码
     * @param response
     * @param session
     * @param type
     * @throws IOException
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        //禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);


        CreateImageCode vCode = new CreateImageCode(100, 30, 5, 10);
        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, vCode.getCode());
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, vCode.getCode());
        }

        vCode.write(response.getOutputStream());

    }

    /**
     * 用户注册
     *
     * @param session
     * @param email
     * @param nickName
     * @param password
     * @param checkCode
     * @return
     */
    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 登录
     *
     * @param session
     * @param email
     * @param password
     * @param checkCode
     * @return
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto loginDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, loginDto);
            return getSuccessResponseVO(loginDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 重置密码
     *
     * @param session
     * @param email
     * @param password
     * @param checkCode
     * @return
     */
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true, checkLogin = false )
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 获取用户头像
     *
     * @param response
     * @param session
     * @param userId
     */
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getAvatar(HttpServletResponse response, HttpSession session, @VerifyParam(required = true) @PathVariable("userId") String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR;
        File folder = new File(avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
                printNoDefaultImage(response);
            }
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
        }
        response.setContentType("image/jpg");
        readFile(response, avatarPath);
    }

    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放置默认头像");
        } catch (Exception e) {
            logger.error("无默认头像", e);
        } finally {
            writer.close();
        }
    }


    /**
     * 获取登录用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getUserinfo(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        System.out.println(sessionWebUserDto);
        return getSuccessResponseVO(sessionWebUserDto);
    }

    /**
     * 获取用户空间
     *
     * @param session
     * @return
     */
    @RequestMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId());
        return getSuccessResponseVO(spaceDto);
    }

    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @RequestMapping("/logout")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    /**
     * 更新用户头像
     *
     * @param session
     * @param avatar
     * @return
     */
    @RequestMapping("/updateUserAvatar")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR);
        File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }
        try {
            avatar.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("上传头像失败", e);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setQqAvatar("");
        userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
        webUserDto.setAvatar(null);
        session.setAttribute(Constants.SESSION_KEY, webUserDto);

        return getSuccessResponseVO(null);
    }

    /**
     * 更新密码
     *
     * @param session
     * @param password
     * @return
     */
    @RequestMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                     @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeByMd5(password));
        userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }


}