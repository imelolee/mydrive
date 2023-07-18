package org.mydrive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.mydrive.component.RedisComponent;
import org.mydrive.entity.config.AppConfig;
import org.mydrive.entity.constants.Constants;
import org.mydrive.entity.dto.SessionWebUserDto;
import org.mydrive.entity.dto.SysSettingsDto;
import org.mydrive.entity.dto.UserSpaceDto;
import org.mydrive.entity.enums.UserStatusEnum;
import org.mydrive.entity.po.FileInfo;
import org.mydrive.entity.query.FileInfoQuery;
import org.mydrive.exception.BusinessException;
import org.mydrive.mappers.FileInfoMapper;
import org.springframework.stereotype.Service;

import org.mydrive.entity.enums.PageSize;
import org.mydrive.entity.query.UserInfoQuery;
import org.mydrive.entity.po.UserInfo;
import org.mydrive.entity.vo.PaginationResultVO;
import org.mydrive.entity.query.SimplePage;
import org.mydrive.mappers.UserInfoMapper;
import org.mydrive.service.UserInfoService;
import org.mydrive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户信息表 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

	@Resource
	private AppConfig appConfig;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据QqOpenId获取对象
	 */
	@Override
	public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.selectByQqOpenId(qqOpenId);
	}

	/**
	 * 根据QqOpenId修改
	 */
	@Override
	public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
		return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
	}

	/**
	 * 根据QqOpenId删除
	 */
	@Override
	public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
		return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
	}

	/**
	 * 根据NickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName修改
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	/**
	 * 用户注册
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null != userInfo){
			throw new BusinessException("邮箱账号已存在");
		}
		UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
		if (null != nickNameUser){
			throw new BusinessException("昵称已存在");
		}
		String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
		UserInfo newUserInfo = new UserInfo();
		newUserInfo.setUserId(userId);
		newUserInfo.setNickName(nickName);
		newUserInfo.setEmail(email);
		newUserInfo.setPassword(StringTools.encodeByMd5(password));
		newUserInfo.setJoinTime(new Date());
		newUserInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		newUserInfo.setUseSpace(0L);

		SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
		newUserInfo.setTotalSpace(sysSettingsDto.getUserInitUsespace() * Constants.MB);

		this.userInfoMapper.insert(newUserInfo);
	}

	@Override
	public SessionWebUserDto login(String email, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null == userInfo || !userInfo.getPassword().equals(StringTools.encodeByMd5(password))){
			throw new BusinessException("邮箱或密码错误");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
			throw new BusinessException("账号被禁用");
		}
		// 更新最后登录时间
		UserInfo updateInfo = new UserInfo();
		updateInfo.setLastLoginTime(new Date());

		this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());

		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setNickName(userInfo.getNickName());
		sessionWebUserDto.setUserId(userInfo.getUserId());
		// 判断是否为管理员账户
		if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)) {
			sessionWebUserDto.setAdmin(true);
		} else{
			sessionWebUserDto.setAdmin(false);
		}

		// 用户空间
		UserSpaceDto userSpaceDto = new UserSpaceDto();
		Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
		userSpaceDto.setUseSpace(useSpace);
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);

		return sessionWebUserDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resetPwd(String email, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null == userInfo) {
			throw new BusinessException("邮箱账户不存在");
		}
		UserInfo updateInfo = new UserInfo();
		updateInfo.setPassword(StringTools.encodeByMd5(password));
		this.userInfoMapper.updateByEmail(updateInfo, email);
	}
}