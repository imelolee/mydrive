package org.mydrive.controller;

import java.io.IOException;
import java.util.List;

import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.CreateImageCode;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.vo.ResponseVO;
import org.mydrive.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.scanner.Constant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户信息表 Controller
 */
@RestController("userInfoController")

public class AccountController extends ABaseController{

	@Resource
	private UserInfoService userInfoService;

	/**
	 * 获取验证码
	 */
	@RequestMapping("/checkCode")
	public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		//禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);


		CreateImageCode vCode = new CreateImageCode(100,30,5,10);
		String code = vCode.getCode();
		if (type == null || type == 0) {
			session.setAttribute(Constants.CHECK_CODE_KEY, vCode.getCode());
		} else {
			session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, vCode.getCode());
		}

		vCode.write(response.getOutputStream());

	}

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query){
		return getSuccessResponseVO(userInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserInfo bean) {
		userInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId查询对象
	 */
	@RequestMapping("/getUserInfoByUserId")
	public ResponseVO getUserInfoByUserId(String userId) {
		return getSuccessResponseVO(userInfoService.getUserInfoByUserId(userId));
	}

	/**
	 * 根据UserId修改对象
	 */
	@RequestMapping("/updateUserInfoByUserId")
	public ResponseVO updateUserInfoByUserId(UserInfo bean,String userId) {
		userInfoService.updateUserInfoByUserId(bean,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId删除
	 */
	@RequestMapping("/deleteUserInfoByUserId")
	public ResponseVO deleteUserInfoByUserId(String userId) {
		userInfoService.deleteUserInfoByUserId(userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email查询对象
	 */
	@RequestMapping("/getUserInfoByEmail")
	public ResponseVO getUserInfoByEmail(String email) {
		return getSuccessResponseVO(userInfoService.getUserInfoByEmail(email));
	}

	/**
	 * 根据Email修改对象
	 */
	@RequestMapping("/updateUserInfoByEmail")
	public ResponseVO updateUserInfoByEmail(UserInfo bean,String email) {
		userInfoService.updateUserInfoByEmail(bean,email);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email删除
	 */
	@RequestMapping("/deleteUserInfoByEmail")
	public ResponseVO deleteUserInfoByEmail(String email) {
		userInfoService.deleteUserInfoByEmail(email);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据QqOpenId查询对象
	 */
	@RequestMapping("/getUserInfoByQqOpenId")
	public ResponseVO getUserInfoByQqOpenId(String qqOpenId) {
		return getSuccessResponseVO(userInfoService.getUserInfoByQqOpenId(qqOpenId));
	}

	/**
	 * 根据QqOpenId修改对象
	 */
	@RequestMapping("/updateUserInfoByQqOpenId")
	public ResponseVO updateUserInfoByQqOpenId(UserInfo bean,String qqOpenId) {
		userInfoService.updateUserInfoByQqOpenId(bean,qqOpenId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据QqOpenId删除
	 */
	@RequestMapping("/deleteUserInfoByQqOpenId")
	public ResponseVO deleteUserInfoByQqOpenId(String qqOpenId) {
		userInfoService.deleteUserInfoByQqOpenId(qqOpenId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据NickName查询对象
	 */
	@RequestMapping("/getUserInfoByNickName")
	public ResponseVO getUserInfoByNickName(String nickName) {
		return getSuccessResponseVO(userInfoService.getUserInfoByNickName(nickName));
	}

	/**
	 * 根据NickName修改对象
	 */
	@RequestMapping("/updateUserInfoByNickName")
	public ResponseVO updateUserInfoByNickName(UserInfo bean,String nickName) {
		userInfoService.updateUserInfoByNickName(bean,nickName);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据NickName删除
	 */
	@RequestMapping("/deleteUserInfoByNickName")
	public ResponseVO deleteUserInfoByNickName(String nickName) {
		userInfoService.deleteUserInfoByNickName(nickName);
		return getSuccessResponseVO(null);
	}
}