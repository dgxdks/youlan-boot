package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.dto.UserResetPasswdDTO;
import com.youlan.system.entity.dto.UserUpdatePasswdDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.youlan.common.db.constant.DBConstant.VAL_STATUS_DISABLED;

@Service
@AllArgsConstructor
public class UserBizService {
    private final UserService userService;
    private final OrgService orgService;
    private final UserPostService userPostService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;

    /**
     * 新增用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(UserDTO dto) {
        //生成用户密码，密码需要加密
        User user = buildAddOrUpdateUser(dto)
                .setUserPassword(userService.genUserPassword(dto.getUserPassword()));
        //保存用户信息
        boolean saveUser = userService.save(user);
        if (!saveUser) {
            throw new BizRuntimeException("新增用户失败");
        }
        userPostService.addUserPostBatch(user.getId(), dto.getPostIdList());
        userRoleService.addUserRoleBatch(user.getId(), dto.getRoleIdList());
        return true;
    }

    /**
     * 修改用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO dto) {
        User user = buildAddOrUpdateUser(dto);
        //保证不允许修改密码和用户名称
        user.setUserPassword(null)
                .setUserName(null);
        //修改用户信息
        boolean updateUser = userService.updateById(user);
        if (!updateUser) {
            throw new BizRuntimeException("修改用户失败");
        }
        userPostService.updateUserPostBatch(user.getId(), dto.getPostIdList());
        userRoleService.updateUserRoleBatch(user.getId(), dto.getRoleIdList());
        return true;
    }

    /**
     * 根据{@link UserDTO}创建要新增或修改的{@link User}
     */
    public User buildAddOrUpdateUser(UserDTO dto) {
        Long orgId = dto.getOrgId();
        Org org = orgService.loadOrgIfExist(orgId);
        if (VAL_STATUS_DISABLED.equals(org.getOrgStatus())) {
            throw new BizRuntimeException(ApiResultCode.D0002);
        }
        User user = new User()
                .setId(dto.getId())
                .setOrgId(orgId)
                .setUserName(dto.getUserName())
                .setUserMobile(dto.getUserMobile())
                .setNickName(dto.getNickName())
                .setEmail(dto.getEmail())
                .setSex(dto.getSex())
                .setStatus(dto.getStatus())
                .setRemark(dto.getRemark());
        //用户名唯一性校验
        userService.checkUserNameRepeat(user);
        //用户手机唯一性校验
        userService.checkUserMobileRepeat(user);
        return user;
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUser(List<Long> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return true;
        }
        for (Long userId : idList) {
            //删除用户本身
            userService.removeById(userId);
            //删除用户关联角色
            userRoleService.removeByUserId(userId);
            //删除用户关联岗位
            userPostService.removeByUserId(userId);
        }
        return true;
    }

    /**
     * 用户详情
     */
    public UserVO loadUser(Long id) {
        User user = userService.loadUserIfExist(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        List<Long> roleIdList = userRoleService.getRoleIdListByUserId(id);
        List<Long> postIdList = userPostService.getPostIdListByUserId(id);
        userVO.setRoleIdList(roleIdList);
        userVO.setPostIdList(postIdList);
        return userVO;
    }

    /**
     * 用户分页
     */
    public IPage<UserVO> getUserPageList(UserPageDTO dto) {
        return userService.getBaseMapper().getList(DBHelper.getPage(dto), dto);
    }

    /**
     * 用户导出
     */
    public List<UserVO> exportUserList(UserPageDTO dto) {
        return userService.getBaseMapper().getList(dto);
    }

    /**
     * 重置用户密码
     */
    public boolean resetUserPasswd(UserResetPasswdDTO dto) {
        return userService.lambdaUpdate()
                .eq(User::getId, dto.getId())
                .set(User::getUserPassword, userService.genUserPassword(dto.getUserPassword()))
                .update();
    }

    /**
     * 用户个人信息详情
     */
    public UserVO loadUserProfile() {
        Long userId = SystemAuthHelper.getUserId();
        User user = userService.loadUserIfExist(userId);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        List<String> roleNameList = userRoleService.getBaseMapper().getRoleNameListByUserId(userId);
        List<String> postNameList = userPostService.getBaseMapper().getPostNameListByUserId(userId);
        String orgName = orgService.loadOrgNameByOrgId(user.getOrgId());
        userVO.setPostNameList(postNameList);
        userVO.setRoleNameList(roleNameList);
        userVO.setOrgName(orgName);
        return userVO;
    }

    /**
     * 用户个人信息修改
     */
    public void updateUserProfile(UserDTO userDTO) {
        User user = buildAddOrUpdateUser(userDTO);
        userService.updateById(user);
    }

    /**
     * 修改用户头像
     */
    public void updateUserAvatar(String avatarUrl) {
        this.userService.lambdaUpdate()
                .eq(User::getId, SystemAuthHelper.getUserId())
                .set(User::getAvatar, avatarUrl)
                .update();
    }

    /**
     * 修改用户密码
     */
    public boolean updateUserPasswd(UserUpdatePasswdDTO dto) {
        String oldPasswd = dto.getOldPasswd();
        String newPasswd = dto.getNewPasswd();
        String confirmPasswd = dto.getConfirmPasswd();
        if (!StrUtil.equals(newPasswd, confirmPasswd)) {
            throw new BizRuntimeException(ApiResultCode.A0023);
        }
        Long userId = SystemAuthHelper.getUserId();
        User user = userService.loadUserIfExist(userId);
        boolean validUserPassword = userService.validUserPassword(oldPasswd, user.getUserPassword());
        if (!validUserPassword) {
            throw new BizRuntimeException(ApiResultCode.A0024);
        }
        user = new User()
                .setId(userId)
                .setUserPassword(userService.genUserPassword(newPasswd));
        return userService.updateById(user);
    }

    /**
     * 用户授权角色更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAuthRole(Long userId, List<Long> roleIds) {
        userService.loadUserIfExist(userId);
        List<String> roleStrList = roleService.getRoleStrList(roleIds);
        userRoleService.updateUserRoleBatch(userId, roleIds);
        // 重新设置用户角色权限
        SystemAuthHelper.setUserRole(userId, roleStrList);
    }
}
