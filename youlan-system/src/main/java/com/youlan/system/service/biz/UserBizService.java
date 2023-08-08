package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.utils.IPageUtil;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.UserPost;
import com.youlan.system.entity.UserRole;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.dto.UserPasswdDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.service.OrgService;
import com.youlan.system.service.UserPostService;
import com.youlan.system.service.UserRoleService;
import com.youlan.system.service.UserService;
import com.youlan.system.utils.SystemAuthUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserBizService {
    private final UserService userService;
    private final OrgService orgService;
    private final UserPostService userPostService;
    private final UserRoleService userRoleService;
    private final ConfigBizService configBizService;

    /**
     * 新增用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(UserDTO dto) {
        User user = createAddOrUpdateUser(dto);
        //生成用户密码，密码需要加密
        userService.setUserPassword(user, dto.getUserPassword());
        //保存用户信息
        boolean saveUser = userService.save(user);
        if (!saveUser) {
            throw new BizRuntimeException("新增用户失败");
        }
        boolean addUserPostBatch = userPostService.addUserPostBatch(user.getId(), dto.getPostIdList());
        if (!addUserPostBatch) {
            throw new BizRuntimeException("新增用户岗位信息失败");
        }
        boolean addUserRoleBatch = userRoleService.addUserRoleBatch(user.getId(), dto.getRoleIdList());
        if (!addUserRoleBatch) {
            throw new BizRuntimeException("新增用户角色信息失败");
        }
        return true;
    }

    /**
     * 修改用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO dto) {
        User user = createAddOrUpdateUser(dto);
        //保证不允许修改密码
        user.setUserPassword(null);
        //修改用户信息
        boolean updateUser = userService.updateById(user);
        if (!updateUser) {
            throw new BizRuntimeException("修改用户失败");
        }
        boolean updateUserPostBatch = userPostService.updateUserPostBatch(user.getId(), dto.getPostIdList());
        if (!updateUserPostBatch) {
            throw new BizRuntimeException("修改用户岗位信息失败");
        }
        boolean updateUserRoleBatch = userRoleService.updateUserRoleBatch(user.getId(), dto.getRoleIdList());
        if (!updateUserRoleBatch) {
            throw new BizRuntimeException("修改用户角色信息失败");
        }
        return true;
    }

    /**
     * 根据{@link UserDTO}创建要新增或修改的{@link User}
     */
    public User createAddOrUpdateUser(UserDTO dto) {
        Long orgId = dto.getOrgId();
        Org org = orgService.loadOrgIfEnabled(dto.getOrgId());
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
        Optional<User> userOpt = userService.loadOneOpt(id);
        if (userOpt.isEmpty()) {
            throw new BizRuntimeException("用户信息不存在");
        }
        UserVO userVO = BeanUtil.copyProperties(userOpt.get(), UserVO.class);
        List<UserRole> userRoleList = userRoleService.getListByUserId(id);
        List<UserPost> userPostList = userPostService.getListByUserId(id);
        userVO.setUserRoleList(userRoleList);
        userVO.setUserPostList(userPostList);
        return userVO;
    }

    /**
     * 用户分页
     */
    public IPage<UserVO> getUserPageList(UserPageDTO dto) {
        return orgService.loadPage(IPageUtil.getIPage(dto), QueryWrapperUtil.getQueryWrapper(dto), UserVO.class);
    }

    /**
     * 用户导出
     */
    public List<UserVO> exportUserList(UserPageDTO dto) {
        List<UserVO> userList = orgService.loadMore(QueryWrapperUtil.getQueryWrapper(dto), UserVO.class);
        List<Long> orgIdSet = userList.stream().map(UserVO::getOrgId).collect(Collectors.toList());
        Map<Long, String> orgIdOrgNameMapping = orgService.getOrgIdOrgNameMap(orgIdSet);
        userList.forEach(user -> user.setOrgName(orgIdOrgNameMapping.get(user.getId())));
        return userList;
    }

    /**
     * 用户导入
     */
    public boolean importUserList() {
        throw new UnsupportedOperationException("导出未实现");
    }

    /**
     * 修改用户密码
     */
    public boolean updateUserPasswd(UserPasswdDTO dto) {
        String oldPasswd = dto.getOldPasswd();
        String newPasswd = dto.getNewPasswd();
        String confirmPasswd = dto.getConfirmPasswd();
        if (!StrUtil.equals(newPasswd, confirmPasswd)) {
            throw new BizRuntimeException("新密码与确认密码不一致");
        }
        Long userId = SystemAuthUtil.getUserId();
        User user = userService.loadUserIfExist(userId);
        boolean validUserPassword = userService.validUserPassword(oldPasswd, user.getUserPassword());
        if (!validUserPassword) {
            throw new BizRuntimeException("旧密码与用户密码不一致");
        }
        user = new User().setId(userId)
                .setUserPassword(newPasswd);
        return userService.updateById(user);
    }
}
